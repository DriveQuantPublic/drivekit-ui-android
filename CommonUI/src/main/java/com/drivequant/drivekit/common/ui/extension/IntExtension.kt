package com.drivequant.drivekit.common.ui.extension

fun Int.formatLeadingZero(): String {
    return String.format("%02d", this)
}