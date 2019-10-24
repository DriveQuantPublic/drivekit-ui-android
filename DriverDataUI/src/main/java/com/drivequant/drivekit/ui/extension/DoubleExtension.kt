package com.drivequant.drivekit.ui.extension

fun Double?.lessThan(other: Double) : Boolean {
    return (this ?: return false) < other
}

fun Double?.lessOrEqualsThan(other: Double) : Boolean {
    return (this ?: return false) <= other
}

fun Double?.strictlyEquals(other: Double) : Boolean {
    return (this ?: return false) == other
}

fun Double.removeZeroDecimal() : String {
    var text = String.format("%.1f", this)
    if (text.last() == '0'){
        text = text.removeRange(text.count() - 2, text.count())
    }
    return text
}