@file:JvmName("DKDateUtils")
package com.drivequant.drivekit.common.ui.extension

import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import java.text.SimpleDateFormat
import java.util.Date

fun Date.formatDate(pattern: DKDatePattern): String {
    val dateFormat = pattern.getSimpleDateFormat()
    return dateFormat.format(this)
}

fun Date.formatDateWithPattern(sdf: SimpleDateFormat): String = sdf.format(this)
