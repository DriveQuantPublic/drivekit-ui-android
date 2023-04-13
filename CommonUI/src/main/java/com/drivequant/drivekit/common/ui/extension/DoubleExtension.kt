@file:JvmName("DKDoubleUtils")
package com.drivequant.drivekit.common.ui.extension

import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@JvmOverloads
fun Double.removeZeroDecimal(floatingPointNumber: Int = 1) =
    DKDataFormatter.formatNumber(this, floatingPointNumber)

fun Double.convertKmsToMiles(): Double = this * 0.621371

fun Double.format(floatingNumber: Int) =
    BigDecimal(this).setScale(floatingNumber, RoundingMode.HALF_UP).toDouble().removeZeroDecimal(floatingNumber)

fun Double.ceilDuration(): Double {
    var computedDuration = this
    computedDuration = if (computedDuration % 60 > 0) {
        (computedDuration / 60).toInt() * 60 + 60.toDouble()
    } else {
        ((computedDuration / 60).toInt() * 60).toDouble()
    }
    return computedDuration
}
