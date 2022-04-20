package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.normalText

class DKProgressBar : LinearLayout {

    private var alertDialog: DKAlertDialog.LayoutBuilder? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context, attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

   private fun init() {
        alertDialog = DKAlertDialog.LayoutBuilder().init(context).layout(R.layout.dk_layout_progress_bar_message).cancelable(false)
    }

    fun show(message: String) {
        val label = alertDialog?.show()?.findViewById<TextView>(R.id.dk_text_view_progress_message)
        label?.text = message
        label?.normalText(DriveKitUI.colors.mainFontColor())
    }

    fun hide() {
        alertDialog?.dismiss()
    }
}