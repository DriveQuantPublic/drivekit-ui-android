package com.drivequant.drivekit.common.ui.extension

import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import java.text.SimpleDateFormat
import java.util.*

fun Date.formatDate(pattern: DKDatePattern) : String{
    val dateFormat = SimpleDateFormat(pattern.getPattern(), Locale.getDefault())
    return dateFormat.format(this)
}