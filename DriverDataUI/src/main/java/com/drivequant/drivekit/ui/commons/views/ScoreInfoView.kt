package com.drivequant.drivekit.ui.commons.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.component.GaugeConfiguration
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.getScoreInfoContent
import com.drivequant.drivekit.ui.extension.getScoreInfoTitle

class ScoreInfoView : LinearLayout {
    constructor(context: Context, gaugeConfiguration: GaugeConfiguration) : super(context) {
        init(gaugeConfiguration)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun init(gaugeConfiguration: GaugeConfiguration) {
        val view = View.inflate(context, R.layout.score_info_item, null)
        val scoreInfoImageView = view.findViewById<ImageView>(R.id.image_view_score_info)
        scoreInfoImageView.setColorFilter(DKColors.secondaryColor)
        view.setOnClickListener {
            val alert = DKAlertDialog.LayoutBuilder()
                .init(context)
                .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                .cancelable(true)
                .positiveButton(context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_ok)) { dialog, _ -> dialog.dismiss() }
                .show()

            val title = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
            val description = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)
            val icon = alert.findViewById<ImageView>(com.drivequant.drivekit.common.ui.R.id.image_view_alert_icon)

            title?.text = gaugeConfiguration.getScoreInfoTitle(context)
            description?.text = gaugeConfiguration.getScoreInfoContent(context)
            icon?.setImageResource(gaugeConfiguration.getIcon())
            title?.headLine1()
            description?.normalText()
        }
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}
