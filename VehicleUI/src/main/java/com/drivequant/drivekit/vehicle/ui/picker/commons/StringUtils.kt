package com.drivequant.drivekit.vehicle.ui.picker.commons

object StringUtils {
    fun trimStringArray(arrayOfStrings: Array<String>): Array<String> {
        for (i in arrayOfStrings.indices) {
            arrayOfStrings[i] = arrayOfStrings[i].trim { it <= ' ' }
        }
        return arrayOfStrings
    }
}
