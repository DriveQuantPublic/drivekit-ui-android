package com.drivequant.drivekit.challenge.ui.joinchallenge.common


import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.dk_title_progress_bar.view.*


class TitleProgressBar(context: Context) : LinearLayout(context) {

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
        text_view_title_condition.text = "$titleProgressBar : $condition"
    }

    private fun setStyle() {
        progress_bar_condition.setBackgroundColor(DriveKitUI.colors.neutralColor())
        val progressBarDrawable = progress_bar_condition.progressDrawable as LayerDrawable
        val foregroundDrawable = progressBarDrawable.getDrawable(1)
        val backgroundDrawable = progressBarDrawable.getDrawable(0)
        foregroundDrawable.tintDrawable(DriveKitUI.colors.primaryColor())
        backgroundDrawable.tintDrawable(DriveKitUI.colors.neutralColor())
    }
}