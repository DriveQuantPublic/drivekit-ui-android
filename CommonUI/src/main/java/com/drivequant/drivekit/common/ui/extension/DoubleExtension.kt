@file:JvmName("DKDoubleUtils")
package com.drivequant.drivekit.common.ui.extension

import java.text.DecimalFormat
import java.util.*

fun Double.removeZeroDecimal(): String = DecimalFormat("0.#").format(this)

fun Double.convertKmsToMiles(): Double = this * 0.621371

fun Double.format(floatingNumber: Int): String = String.format(Locale.getDefault(),"%.${floatingNumber}f",this)

fun Double.ceilDuration(): Double {
    var computedDuration = this
    computedDuration = if (computedDuration % 60 > 0) {
        (computedDuration / 60).toInt() * 60 + 60.toDouble()
    } else {
        ((computedDuration / 60).toInt() * 60).toDouble()
    }
    return computedDuration
}