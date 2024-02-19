package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import com.drivequant.drivekit.common.ui.DriveKitUI

class DKButtonPrimary : DKButtonBase {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        shape.apply {
            GradientDrawable.RECTANGLE
            setColor(DriveKitUI.colors.secondaryColor())
        }
    }
}