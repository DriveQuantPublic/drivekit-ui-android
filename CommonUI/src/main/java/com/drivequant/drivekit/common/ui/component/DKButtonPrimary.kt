package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.util.AttributeSet
import com.drivequant.drivekit.common.ui.graphical.DKColors

class DKButtonPrimary : DKButtonBase {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        super.shape.setColor(DKColors.secondaryColor)
    }
}
