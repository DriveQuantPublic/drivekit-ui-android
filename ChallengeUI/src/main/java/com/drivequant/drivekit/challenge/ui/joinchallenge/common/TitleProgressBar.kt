package com.drivequant.drivekit.challenge.ui.joinchallenge.common

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable


internal class TitleProgressBar(context: Context) : LinearLayout(context) {

    private val conditionProgressBar: ProgressBar
    private val titleConditionTextView: TextView

    init {
        val view = View.inflate(context, R.layout.dk_title_progress_bar, null)
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        this.conditionProgressBar = view.findViewById(R.id.progress_bar_condition)
        this.titleConditionTextView = view.findViewById(R.id.text_view_title_condition)
        setStyle()
    }

    fun setProgress(progress: Int) {
        conditionProgressBar.progress = progress
    }

    fun setTitle(title: String, condition: String) {
       val titleProgressBar = when (title) {
            "km" -> DKResource.convertToString(context,"dk_challenge_distance_kilometer")
            "nbTrip" -> DKResource.convertToString(context,"dk_challenge_nb_trip")
            else -> ""
        }
        titleConditionTextView.apply {
            text = DKSpannable().append("$titleProgressBar : ", context.resSpans {
                color(DriveKitUI.colors.mainFontColor())
            }).append(condition, context.resSpans {
                color(DriveKitUI.colors.primaryColor())
            }).toSpannable()
            typeface = DriveKitUI.primaryFont(context)
        }
    }

    private fun setStyle() {
        val foregroundDrawable = (conditionProgressBar.progressDrawable as LayerDrawable).getDrawable(1)
        foregroundDrawable.tintDrawable(DriveKitUI.colors.primaryColor())

        val params = conditionProgressBar.layoutParams as MarginLayoutParams
        params.setMargins(
            context.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_margin_half).toInt(),
            params.topMargin,
            context.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_margin_half).toInt(),
            params.bottomMargin)

        layoutParams = params
    }
}
