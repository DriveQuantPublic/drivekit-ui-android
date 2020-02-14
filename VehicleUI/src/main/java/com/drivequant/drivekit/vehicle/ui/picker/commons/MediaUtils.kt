package com.drivequant.drivekit.vehicle.ui.picker.commons

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object MediaUtils {
    fun readCSVFile(context: Context, resourceMedia: Int, delimiter: String = ","): List<Array<String>>? {
        val inputStream = context.resources.openRawResource(resourceMedia)
        val resultList: MutableList<Array<String>> = ArrayList()
        val reader = BufferedReader(InputStreamReader(inputStream))
        try {
            while (reader.readLine()?.let {
                    val row = it.split(delimiter).toTypedArray()
                    resultList.add(StringUtils.trimStringArray(row))
            } != null);
        } catch (ex: IOException) {
            throw RuntimeException("Error in reading CSV file: $ex")
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                throw RuntimeException("Error while closing input stream: $e")
            }
        }
        return resultList
    }
}