@file:JvmName("DKDoubleUtils")
package com.drivequant.drivekit.common.ui.extension

import java.text.DecimalFormat
import java.util.*

fun Double.removeZeroDecimal(): String = DecimalFormat("0.#").format(this)

fun Double?.lessOrEqualsThan(other: Double): Boolean {
    return (this ?: return false) <= other
}

fun Double.convertKmsToMiles(): Double = this * 0.621371

fun Double.format(floatingNumber: Int): String = String.format(Locale.getDefault(),"%.${floatingNumber}f",this)
