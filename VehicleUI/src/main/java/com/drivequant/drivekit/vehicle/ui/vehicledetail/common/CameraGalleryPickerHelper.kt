package com.drivequant.drivekit.vehicle.ui.vehicledetail.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.FileProvider
import android.util.Log
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
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
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

    fun retrieveAbsolutePathFromUri(context: Context, uri: Uri?): String? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri!!, filePathColumn, null, null, null)
        return if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val path = cursor.getString(columnIndex)
            cursor.close()
            path
        } else {
            null
        }
    }
}