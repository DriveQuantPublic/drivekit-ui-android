package com.drivequant.drivekit.driverachievement.ui.streaks.viewholder

import android.content.DialogInterface
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksData
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreakStatus


class StreakViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val context = itemView.context
    private val textViewStreakTitle = itemView.findViewById<TextView>(R.id.text_view_streak_title)
    private val textViewCurrentStreakData = itemView.findViewById<TextView>(R.id.text_view_current_streak_trip)
    private val textViewBestStreakData = itemView.findViewById<TextView>(R.id.text_view_best_streak_trip)
    private val textViewCurrentStreakDate = itemView.findViewById<TextView>(R.id.text_view_current_streak_date)
    private val textViewBestStreakDate = itemView.findViewById<TextView>(R.id.text_view_best_streak_date)
    private val textViewTripsCount = itemView.findViewById<TextView>(R.id.text_view_trips_count)
    private val textViewBestTitle = itemView.findViewById<TextView>(R.id.best_title)
    private val textViewCurrentTitle = itemView.findViewById<TextView>(R.id.current_title)
    private val imageViewStreak = itemView.findViewById<ImageView>(R.id.image_view_streak_icon)
    private val seekBar = itemView.findViewById<SeekBar>(R.id.seek_bar)
    private val imageViewInfo = itemView.findViewById<ImageView>(R.id.image_view_info)
    private val background =  textViewTripsCount.background as GradientDrawable

    fun bind(streaksData: StreaksData) {
        textViewStreakTitle.text = streaksData.getTitle(itemView.context)
        imageViewStreak.setImageResource(streaksData.getIcon())
        imageViewInfo.setOnClickListener {
            showDescription(streaksData)
        }

        textViewStreakTitle.headLine1()
        textViewBestTitle.headLine2()
        textViewCurrentTitle.headLine2()

        setupSeekBar(streaksData)
        setData(streaksData)
    }

    private fun setupSeekBar(streaksData: StreaksData) {
        seekBar.setPadding(0, 0, 0, 0)
        seekBar.progress = streaksData.computePercentage()
        seekBar.setOnTouchListener { _, _ -> true }
    }

    private fun setData(streaksData: StreaksData) {
        textViewTripsCount.text = "${streaksData.bestTripsCount}"
        textViewCurrentStreakData.text = streaksData.getCurrentStreakData(context)
        textViewBestStreakData.text = streaksData.getBestStreakData(context)
        textViewCurrentStreakDate.text = streaksData.getCurrentStreakDate(context)
        textViewBestStreakDate.text = streaksData.getBestStreakDate(context)

        when (streaksData.getStreakStatus()) {
            StreakStatus.INIT,  StreakStatus.IN_PROGRESS, StreakStatus.RESET -> {
                setStyle(reset = true)
            }

            StreakStatus.BEST -> {
                setStyle()
            }
        }
    }

    private fun showDescription(streaksData: StreaksData) {

       val alert = DKAlertDialog.LayoutBuilder()
           .init(context)
           .layout(R.layout.template_alert_dialog_layout)
           .cancelable(true)
           .positiveButton(context.getString(R.string.dk_common_ok),DialogInterface.OnClickListener
           { dialog, _ ->  dialog.dismiss() })
           .show()

        val title = alert.findViewById<TextView>(R.id.text_view__alert_title)
        val description = alert.findViewById<TextView>(R.id.text_view_alert_description)
        val icon = alert.findViewById<ImageView>(R.id.image_view_alert_icon)

        title?.text = streaksData.getTitle(context)
        description?.text = streaksData.getDescriptionText(context)
        icon?.setImageResource(streaksData.getIcon())

        title?.headLine1()
        description?.normalText()
    }

    private fun setStyle(reset: Boolean = false) {
        if (reset) {
            textViewBestStreakData.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            textViewBestStreakData.typeface = textViewBestStreakDate.typeface
            textViewTripsCount.setTextColor(DriveKitUI.colors.mainFontColor())
            background.setStroke(5, ContextCompat.getColor(context, R.color.dk_trips_count_stroke))
            seekBar.thumb.mutate().alpha = 255
        } else {
            textViewBestStreakData.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            textViewBestStreakData.setTypeface(textViewBestStreakData.typeface, Typeface.BOLD)
            textViewTripsCount.setTextColor(DriveKitUI.colors.secondaryColor())
            background.setStroke(5, DriveKitUI.colors.secondaryColor())
            seekBar.thumb.mutate().alpha = 0
        }
    }
}