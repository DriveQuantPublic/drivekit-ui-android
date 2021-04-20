package com.drivequant.drivekit.common.ui.utils

import kotlin.text.Typography.nbsp

sealed class FormatType(open val value: String) {
    data class VALUE(override val value: String) : FormatType(value)
    data class UNIT(override val value: String) : FormatType(value)
    data class SEPARATOR(override val value: String = nbsp.toString()) : FormatType(value)
}

fun List<FormatType>.convertToString(): String {
    var computedString = ""
    this.forEach {
        computedString += it.value
    }
    return computedString
}