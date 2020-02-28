package com.drivequant.drivekit.common.ui.extension

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.ResSpans

inline fun Context.resSpans(options: ResSpans.() -> Unit) = ResSpans(this).apply(options)