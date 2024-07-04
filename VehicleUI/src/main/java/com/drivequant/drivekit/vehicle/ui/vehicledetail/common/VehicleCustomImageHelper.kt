package com.drivequant.drivekit.vehicle.ui.vehicledetail.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.listener.OnCameraPictureTakenCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 * Created by steven on 26/07/2019.
 */
internal object VehicleCustomImageHelper {
    const val REQUEST_CAMERA = 11

    fun getVehicleFileName(vehicleId: String) = "$vehicleId.png"

    fun openPhotoPicker(pickMedia: ActivityResultLauncher<PickVisualMediaRequest>) {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun getImageUri(vehicleId: String): Uri? {
        val file = File(buildVehicleDirectory(), getVehicleFileName(vehicleId))
        return if (file.exists() && file.canRead()) {
            return file.toUri()
        } else {
            null
        }
    }

    suspend fun saveImage(context: Context, filename: String, uri: Uri): Boolean =
        withContext(Dispatchers.IO) {
            val directory = buildVehicleDirectory()
            if (!directory.exists()) {
                val success = directory.mkdirs()
                if (!success) {
                    DriveKitLog.e(DriveKitVehicleUI.TAG, "Couldn't create directory")
                    return@withContext false
                }
            }

            val stream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(stream)
            originalBitmap.density = Bitmap.DENSITY_NONE
            val matrix = buildMatrixOrientation(context, uri)

            val computedImageSize =
                Pair(originalBitmap.width, originalBitmap.height).computeNewImageSize(context)
            val bitmap = Bitmap.createBitmap(originalBitmap, 0, 0, computedImageSize.first, computedImageSize.second, matrix, true)

            try {
                val file = File(directory, filename)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    return@withContext true
                }
            } catch (e: IOException) {
                DriveKitLog.e(DriveKitVehicleUI.TAG, "Couldn't create vehicle file: $e")
                return@withContext false
            } finally {
                stream?.close()
            }
        }

    private fun buildVehicleDirectory(): File {
        val filesDir: File = DriveKit.applicationContext.filesDir
        val directory = "DriveKitVehicleUI"
        return File(filesDir, directory)
    }

    fun openCamera(activity: Activity, outputFilename: String, cameraCallback: OnCameraPictureTakenCallback) {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val file = createImageFile(activity, outputFilename)
            val uri: Uri = FileProvider.getUriForFile(activity, activity.packageName + ".provider", file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            val filePath = "file://" + file.absolutePath
            cameraCallback.onFilePathReady(filePath)
            startActivityForResult(activity, intent, REQUEST_CAMERA, null)
        } catch (e: Exception) {
            DriveKitLog.e(DriveKitVehicleUI.TAG, "Could not open camera: $e")
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(activity: Activity, filename: String): File {
        // taken from https://developer.android.com/training/camera/photobasics
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(filename, ".jpg", storageDir)
    }

    private fun buildMatrixOrientation(context: Context, uri: Uri): Matrix? =
        getImageOrientation(context, uri)?.let {
            val matrix = Matrix()
            matrix.setRotate(it.toFloat())
            return matrix
        } ?: run {
            null
        }

    private fun getImageOrientation(context: Context, uri: Uri): Int? {
        var stream: InputStream? = null
        try {
            stream = context.contentResolver.openInputStream(uri)
            val exifInterface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && stream != null) {
                ExifInterface(stream)
            } else {
                null // DriveKit will no longer support Android 6.0 and 7.0 in Q4 2024.
            }

            exifInterface?.let {
                val orientation = it.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                return when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    else -> null
                }
            }
        } catch (e: Exception) {
            DriveKitLog.e(DriveKitVehicleUI.TAG, "Couldn't get image orientation: $e")
        } finally {
            stream?.close()
        }
        return null
    }

    private fun Pair<Int, Int>.computeNewImageSize(context: Context): Pair<Int, Int> {
        val originalWidth = this.first
        val originalHeight = this.second

        val deviceScreenWidth = (context.resources.displayMetrics.widthPixels * context.resources.displayMetrics.density).toInt()
        val imageViewHeight = context.resources.getDimension(R.dimen.dk_vehicle_image_height).convertDpToPx()

        if (originalWidth > deviceScreenWidth && originalHeight > imageViewHeight) {
            val ratioWidth = originalWidth.toFloat() / deviceScreenWidth.toFloat()
            val newHeight = (originalHeight / ratioWidth).toInt()

            if (newHeight < imageViewHeight) {
                val ratioHeight = originalHeight.toFloat() / imageViewHeight.toFloat()
                val newWidth = (originalWidth / ratioHeight).toInt()
                return Pair(newWidth, imageViewHeight)
            } else {
                return Pair(deviceScreenWidth, newHeight)
            }
        } else {
            return this
        }
    }

    internal fun reset() {
        try {
            val files = buildVehicleDirectory().listFiles()
            var count = 0
            if (files != null) {
                files.forEach {
                    if (it.exists()) {
                        count++
                        it.delete()
                    }
                }
                DriveKitLog.i(DriveKitVehicleUI.TAG, "$count vehicle file(s) deleted")
            }
        } catch (e: Exception) {
            DriveKitLog.e(DriveKitVehicleUI.TAG, "An error occured during vehicle image deletion: $e")
        }
    }
}