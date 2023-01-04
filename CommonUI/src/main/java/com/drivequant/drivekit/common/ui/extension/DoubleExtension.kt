@file:JvmName("DKDoubleUtils")
package com.drivequant.drivekit.common.ui.extension

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.ceil

fun Double.removeZeroDecimal(): String = DecimalFormat("0.#").format(this)

fun Double.convertKmsToMiles(): Double = this * 0.621371

fun Double.format(floatingNumber: Int) =
    BigDecimal(this).setScale(floatingNumber, RoundingMode.HALF_UP).toDouble().removeZeroDecimal()

fun Double.ceilDuration(): Double {
    var computedDuration = this
    computedDuration = if (computedDuration % 60 > 0) {
        (computedDuration / 60).toInt() * 60 + 60.toDouble()
    } else {
        ((computedDuration / 60).toInt() * 60).toDouble()
    }
    return computedDuration
}

fun Double.ceilToValueDivisibleBy10(): Double {
    val intValue = ceil(this).toInt()
    val nextValueDivisibleBy10 = ((intValue / 10).plus(if (intValue % 10 == 0) 0 else 1)) * 10
    return nextValueDivisibleBy10.toDouble()
}