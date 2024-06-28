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
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.listener.OnCameraPictureTakenCallback
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Created by steven on 26/07/2019.
 */
object CameraGalleryPickerHelper {
    const val REQUEST_GALLERY = 10
    const val REQUEST_CAMERA = 11

    fun openPhotoPicker(pickMedia: ActivityResultLauncher<PickVisualMediaRequest>) {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun getImage(vehicleId: String): Uri? {
        val file = File(buildVehicleDirectory(), "$vehicleId.png")
        return if (file.exists() && file.canRead()) {
            return file.toUri()
        } else {
            null
        }
    }

    //TODO add Coroutine ?
    fun saveImage(context: Context, filename: String, uri: Uri, width: Int, height: Int, callback: (success: Boolean, newUri: Uri?) -> Unit) {
        val isExternalStorageWritable = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        if (isExternalStorageWritable) {
            val directory = buildVehicleDirectory()
            if (!directory.exists()) {
                val success = directory.mkdirs()
                if (!success) {
                    DriveKitLog.e(DriveKitVehicleUI.TAG, "Couldn't create directory")
                    callback(false, null)
                }
            }

            val stream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(stream)
            originalBitmap.density = Bitmap.DENSITY_NONE
            val matrix = buildMatrixOrientation(context, uri)

            val bitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)

            try {
                val file = File(directory, filename)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    callback(true, file.toUri())
                }
            } catch (e: IOException) {
                DriveKitLog.e(DriveKitVehicleUI.TAG, "Couldn't create create vehicle file: $e")
                callback(false, null)
            }
        } else {
            DriveKitLog.e(DriveKitVehicleUI.TAG, "Couldn't save image $uri: external storage is not writable")
            callback(false, null)
        }
    }

    fun buildVehicleDirectory(): File {
        val directory = "DriveKitVehicleUI"
        val externalStorageVolumes = ContextCompat.getExternalFilesDirs(DriveKit.applicationContext, null)
        val filesDir: File = externalStorageVolumes.firstOrNull()?.let {
            if (Environment.getExternalStorageState(it) == Environment.MEDIA_MOUNTED) { // à voir s'il faut le faire
                it
            } else {
                null
            }
        } ?: DriveKit.applicationContext.filesDir
        return File(filesDir, directory)
    }

    fun openCamera(activity: Activity, outputFilename: String, cameraCallback: OnCameraPictureTakenCallback) {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val file = createImageFile(activity, outputFilename)
            val uri: Uri = FileProvider.getUriForFile(activity, activity.packageName + ".provider", file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            val filePath = "file://" + file.absolutePath
            cameraCallback.pictureTaken(filePath)
            startActivityForResult(activity, intent, REQUEST_CAMERA, null)
        } catch (e: Exception) {
            Log.e("DriveKit Vehicle UI", "Could not open camera : $e")
        }
    }

    fun buildUriFromIntentData(data: Intent?): Uri? {
        return data?.data
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
        } ?: run { null }

    private fun getImageOrientation(context: Context, uri: Uri): Int? {
        val stream = context.contentResolver.openInputStream(uri)

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
        return null
    }
}