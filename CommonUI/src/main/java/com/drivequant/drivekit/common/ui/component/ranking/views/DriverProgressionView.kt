package com.drivequant.drivekit.common.ui.component.ranking.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DKRankingViewModel
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DriverProgression
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.dk_driver_progression_view.view.*


class DriverProgressionView  : LinearLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_driver_progression_view, null).setDKStyle()
        addView(view, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT))

        setStyle()
    }

    fun setDriverProgression(rankingViewModel: DKRankingViewModel) {
        val progressionIconId =
            when (rankingViewModel.getProgression()) {
                DriverProgression.GOING_DOWN -> "dk_common_arrow_down"
                DriverProgression.GOING_UP -> "dk_common_arrow_up"
                else -> null
            }
        text_view_global_rank.text = rankingViewModel.getDriverGlobalRank(context)
        progressionIconId?.let {
            image_view_driver_progression.setImageDrawable(
                DKResource.convertToDrawable(
                    context,
                    it
                )
            )
        }?:run {
            image_view_driver_progression.visibility = View.GONE
        }
        driver_progression_container.setBackgroundColor(rankingViewModel.getBackgroundColor())
        DKResource.convertToDrawable(context, "dk_common_info")?.let {
            it.tintDrawable(DriveKitUI.colors.secondaryColor())
            image_view_info_popup_condition.apply {
                visibility = if (rankingViewModel.getConditionVisibility()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                setImageDrawable(it)
                setOnClickListener {
                    val alertDialog = DKAlertDialog.LayoutBuilder()
                        .init(context)
                        .layout(R.layout.template_alert_dialog_layout)
                        .positiveButton(DKResource.convertToString(context, "dk_common_ok")) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()

                    val titleTextView =
                        alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                    val descriptionTextView =
                        alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                    titleTextView?.text = rankingViewModel.getConditionTitle(context)
                    descriptionTextView?.text = rankingViewModel.getConditionDescription(context)
                    titleTextView?.headLine1()
                    descriptionTextView?.normalText()
                }
            }
        }
    }

    private fun setStyle() {
        text_view_global_rank.headLine2()
    }
}