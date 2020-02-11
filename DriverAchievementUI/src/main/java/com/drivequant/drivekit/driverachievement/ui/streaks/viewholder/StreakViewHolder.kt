package com.drivequant.drivekit.driverachievement.ui.streaks.viewholder

import android.content.DialogInterface
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.StreaksViewConfig
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksData
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksStatus
import com.drivequant.drivekit.driverachievement.ui.utils.*


class StreakViewHolder(itemView: View, private val streaksViewConfig: StreaksViewConfig) :
    RecyclerView.ViewHolder(itemView) {
    private val context = itemView.context
    private val textViewStreakTitle = itemView.findViewById<TextView>(R.id.text_view_streak_title)
    private val textViewCurrentStreakTrip = itemView.findViewById<TextView>(R.id.text_view_current_streak_trip)
    private val textViewBestStreakTrip = itemView.findViewById<TextView>(R.id.text_view_best_streak_trip)
    private val textViewCurrentStreakDate = itemView.findViewById<TextView>(R.id.text_view_current_streak_date)
    private val textViewBestStreakDate = itemView.findViewById<TextView>(R.id.text_view_best_streak_date)
    private val textViewTripsCount = itemView.findViewById<TextView>(R.id.text_view_trips_count)
    private val imageViewStreak = itemView.findViewById<ImageView>(R.id.image_view_streak_icon)
    private val seekBar = itemView.findViewById<SeekBar>(R.id.seek_bar)
    private val imageViewInfo = itemView.findViewById<ImageView>(R.id.image_view_info)

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
        val currentTripsCount = streaksData.getCurrentStreak().tripNumber
        val bestTripsCount = streaksData.getBestStreak().tripNumber
        val currentDistance = DistanceUtils().formatDistance(context, streaksData.getCurrentStreak().distance)
        val bestDistance = DistanceUtils().formatDistance(context, streaksData.getBestStreak().distance)
        val currentDuration = DurationUtils().formatDuration(context, streaksData.getCurrentStreak().duration)
        val bestDuration = DurationUtils().formatDuration(context, streaksData.getBestStreak().duration)
        val currentStartDate = DateUtils().formatDate(streaksData.getCurrentStreak().startDate)
        val currentEndDate = DateUtils().formatDate(streaksData.getCurrentStreak().endDate)
        val bestStartDate = DateUtils().formatDate(streaksData.getBestStreak().startDate)
        val bestEndDate = DateUtils().formatDate(streaksData.getBestStreak().endDate)
        textViewTripsCount.text = "$bestTripsCount"

        when (streaksData.getStreakStatus()) {
            StreaksStatus.INIT -> {
                textViewCurrentStreakTrip.text = buildStreakData(currentTripsCount,currentDistance,currentDuration)
                textViewBestStreakTrip.text = buildStreakData(bestTripsCount, bestDistance, bestDuration)
                textViewCurrentStreakDate.text = context.getString(
                    R.string.dk_streaks_since,
                    currentStartDate
                )
                textViewBestStreakDate.text = context.getString(R.string.dk_streaks_empty)
            }
            StreaksStatus.IN_PROGRESS -> {
                resetStyle()
                // First streak
                if (currentTripsCount == bestTripsCount) {
                    if (currentTripsCount != 0) {
                        textViewTripsCount.setTextColor(ContextCompat.getColor(context, R.color.dk_primary_color))
                    }

                    textViewCurrentStreakTrip.text = buildStreakData(currentTripsCount,currentDistance,currentDuration)
                    textViewBestStreakTrip.text = context.getString(R.string.dk_streaks_congrats)
                    textViewCurrentStreakDate.text = context.getString(
                        R.string.dk_streaks_since,
                        currentStartDate
                    )
                    textViewBestStreakDate.text = context.getString(R.string.dk_streaks_congrats_text)
                } else {
                    // N - streak
                    if (currentTripsCount == 0 && bestTripsCount != 0) {
                        textViewCurrentStreakDate.text = streaksData.getResetText(context)
                    } else {
                        textViewCurrentStreakDate.text = context.getString(
                            R.string.dk_streaks_since_to,
                            currentStartDate,
                            currentEndDate
                        )
                    }

                    textViewCurrentStreakTrip.text = buildStreakData(currentTripsCount,currentDistance,currentDuration)
                    textViewBestStreakTrip.text = buildStreakData(bestTripsCount, bestDistance, bestDuration)
                    textViewBestStreakDate.text = context.getString(
                        R.string.dk_streaks_since_to,
                        bestStartDate,
                        bestEndDate
                    )
                }
            }
            StreaksStatus.BEST -> {
                setStyle()
                textViewBestStreakTrip.text = context.getString(R.string.dk_streaks_congrats)
                textViewBestStreakDate.text = context.getString(R.string.dk_streaks_congrats_text)
                textViewCurrentStreakTrip.text = buildStreakData(currentTripsCount,currentDistance,currentDuration)
                textViewCurrentStreakDate.text = context.getString(
                        R.string.dk_streaks_since_to,
                        currentStartDate,
                        currentEndDate
                    )
            }
        }
    }

    private fun buildStreakData(tripsCount: Int, distance: String?, duration: String): Spanned {
        val streakTrip = context.resources.getQuantityString(R.plurals.streak_trip_plural, tripsCount)
        return Html.fromHtml(
            "${HtmlUtils.getTextHighlight(
                "$tripsCount $streakTrip",
                context
            )} - $distance - $duration"
        )
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

    private fun setStyle() {
        textViewBestStreakTrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        textViewBestStreakTrip.setTypeface(textViewBestStreakTrip.typeface, Typeface.BOLD)
        textViewTripsCount.setTextColor(ContextCompat.getColor(context, R.color.dk_primary_color))
    }

    private fun resetStyle() {
        textViewBestStreakTrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        textViewBestStreakTrip.typeface = textViewBestStreakDate.typeface
        textViewTripsCount.setTextColor(ContextCompat.getColor(context, R.color.dk_gray500))
    }
}