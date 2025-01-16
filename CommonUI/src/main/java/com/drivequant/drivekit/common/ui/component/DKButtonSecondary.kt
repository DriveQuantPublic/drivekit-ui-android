package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.util.AttributeSet
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.intColor
import com.drivequant.drivekit.common.ui.utils.convertDpToPx

class DKButtonSecondary : DKButtonBase {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        super.shape.setStroke(2.convertDpToPx(), R.color.secondaryColor.intColor(context))
        updateTypeface()
    }
}
