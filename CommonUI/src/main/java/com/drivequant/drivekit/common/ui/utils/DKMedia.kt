package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object DKMedia {

    fun readCSVFile(context: Context, resourceMedia: Int): List<Array<String>>? {
        val inputStream = context.resources.openRawResource(resourceMedia)
        val resultList: MutableList<Array<String>> = ArrayList()
        val reader = BufferedReader(InputStreamReader(inputStream))
        try {
            while (reader.readLine()?.let {
                    val row = it.split(",").toTypedArray()
                    resultList.add(trimStringArray(row))
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

    private fun trimStringArray(arrayOfStrings: Array<String>): Array<String> {
        for (i in arrayOfStrings.indices) {
            arrayOfStrings[i] = arrayOfStrings[i].trim { it <= ' ' }
        }
        return arrayOfStrings
    }
}