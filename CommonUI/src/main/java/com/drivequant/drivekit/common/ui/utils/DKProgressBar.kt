package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.normalText

class DKProgressBar : LinearLayout {

    private lateinit var alertDialog: DKAlertDialog.LayoutBuilder

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

    fun show(message: String?) {
        val label = alertDialog.show().findViewById<TextView>(R.id.dk_text_view_progress_message)
        label?.run {
            if (message.isNullOrEmpty()) {
                visibility = View.GONE
            } else {
                text = message
                normalText(DriveKitUI.colors.mainFontColor())
                visibility = View.VISIBLE
            }
        }
    }

    fun hide() {
        alertDialog.dismiss()
    }
}
