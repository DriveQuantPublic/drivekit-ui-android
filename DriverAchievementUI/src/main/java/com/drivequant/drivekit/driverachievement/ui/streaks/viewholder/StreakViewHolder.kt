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
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.StreaksViewConfig
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksData
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreakStatus
import com.drivequant.drivekit.driverachievement.ui.utils.*


class StreakViewHolder(itemView: View, private val streaksViewConfig: StreaksViewConfig) :
    RecyclerView.ViewHolder(itemView) {
    private val context = itemView.context
    private val textViewStreakTitle = itemView.findViewById<TextView>(R.id.text_view_streak_title)
    private val textViewCurrentStreakData = itemView.findViewById<TextView>(R.id.text_view_current_streak_trip)
    private val textViewBestStreakData = itemView.findViewById<TextView>(R.id.text_view_best_streak_trip)
    private val textViewCurrentStreakDate = itemView.findViewById<TextView>(R.id.text_view_current_streak_date)
    private val textViewBestStreakDate = itemView.findViewById<TextView>(R.id.text_view_best_streak_date)
    private val textViewTripsCount = itemView.findViewById<TextView>(R.id.text_view_trips_count)
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

        if (streaksData.currentTripsCount == streaksData.bestTripsCount) {
            if (streaksData.currentTripsCount != 0) {
                textViewTripsCount.setTextColor(ContextCompat.getColor(context, R.color.dk_primary_color))
            }
            textViewBestStreakDate.text = streaksData.getBestStreakDate(context)
        } else {
            textViewBestStreakDate.text = context.getString(
                R.string.dk_streaks_since_to,
                streaksData.bestStartDate,
                streaksData.bestEndDate)
        }

        when (streaksData.getStreakStatus()) {
            StreakStatus.INIT,  StreakStatus.IN_PROGRESS -> {
                setStyle(reset = true)
            }

            StreakStatus.BEST -> {
                setStyle()
            }
        }
    }

    private fun showDescription(streaksData: StreaksData) {
        AlertDialogUtils.AlertBuilder()
            .init(context)
            .iconResId(streaksData.getIcon())
            .title(streaksData.getTitle(context))
            .message(streaksData.getDescriptionText(context))
            .positiveButton(streaksViewConfig.okText,
                DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            .show()
    }

    private fun setStyle(reset: Boolean = false) {
        if (reset) {
            textViewBestStreakData.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            textViewBestStreakData.typeface = textViewBestStreakDate.typeface
            textViewTripsCount.setTextColor(ContextCompat.getColor(context, R.color.dk_gray500))
            background.setStroke(2, ContextCompat.getColor(context, R.color.dk_stroke_color))
        } else {
            val primaryColor = ContextCompat.getColor(context, R.color.dk_primary_color)
            textViewBestStreakData.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            textViewBestStreakData.setTypeface(textViewBestStreakData.typeface, Typeface.BOLD)
            textViewTripsCount.setTextColor(primaryColor)
            background.setStroke(2, primaryColor)
        }
    }
}