package com.drivequant.drivekit.challenge.ui.joinchallenge.common


import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import kotlinx.android.synthetic.main.dk_title_progress_bar.view.*


internal class TitleProgressBar(context: Context) : LinearLayout(context) {

    init {
        val view = View.inflate(context, R.layout.dk_title_progress_bar, null)
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        setStyle()
    }

    fun setProgress(progress: Int) {
        progress_bar_condition.progress = progress
    }

    fun setTitle(title: String, condition: String) {
        //TODO Improve
       val titleProgressBar = when (title) {
            "km" -> DKResource.convertToString(context,"dk_challenge_distance_kilometer")
            "nbTrip" -> DKResource.convertToString(context,"dk_challenge_nb_trip")
            else -> ""
        }

        text_view_title_condition.text = DKSpannable().append("$titleProgressBar : ", context.resSpans {
            color(DriveKitUI.colors.mainFontColor())
        }).append(condition, context.resSpans {
            color(DriveKitUI.colors.primaryColor())
        }).toSpannable()
    }

    private fun setStyle() {
        val foregroundDrawable = (progress_bar_condition.progressDrawable as LayerDrawable).getDrawable(1)
        foregroundDrawable.tintDrawable(DriveKitUI.colors.primaryColor())
    }
}