package com.drivequant.drivekit.common.ui.extension

fun String.capitalizeFirstLetter() : String {
    return if (this.isNotBlank()) {
        val first = this[0].uppercaseChar()
        val other = this.substring(1)
        "$first$other"
    } else {
        this
    }
}