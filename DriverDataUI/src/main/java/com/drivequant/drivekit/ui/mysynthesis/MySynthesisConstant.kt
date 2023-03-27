package com.drivequant.drivekit.ui.mysynthesis

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.ui.R

internal object MySynthesisConstant {
    const val INDICATOR_BORDER_WIDTH = 2f
    val indicatorSize = 11.convertDpToPx()
    fun getGaugeCornerRadius(context: Context) = context.resources.getDimension(R.dimen.dk_mysynthesis_gauge_height) / 2
}
