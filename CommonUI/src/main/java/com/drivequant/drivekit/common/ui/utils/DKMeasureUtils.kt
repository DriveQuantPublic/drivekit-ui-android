package com.drivequant.drivekit.common.ui.utils

import android.content.res.Resources

fun Int.convertDpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Float.convertDpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
