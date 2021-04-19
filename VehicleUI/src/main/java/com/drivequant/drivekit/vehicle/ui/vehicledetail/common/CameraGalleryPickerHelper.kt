package com.drivequant.drivekit.vehicle.ui.vehicledetail.common

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import com.drivequant.drivekit.vehicle.ui.listener.OnCameraPictureTakenCallback
import java.io.File
import java.io.IOException

/**
 * Created by steven on 26/07/2019.
 */
object CameraGalleryPickerHelper {
    const val REQUEST_GALLERY = 10
    const val REQUEST_CAMERA = 11

    fun openGallery(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(activity, intent, REQUEST_GALLERY, null)
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
        val storageDir =
            activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(filename, ".jpg", storageDir)
    }
}