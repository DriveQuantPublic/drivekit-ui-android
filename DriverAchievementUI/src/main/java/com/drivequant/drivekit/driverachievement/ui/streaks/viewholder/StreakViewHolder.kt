package com.drivequant.drivekit.driverachievement.ui.streaks.viewholder

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.StreaksViewConfig
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksData
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksStatus
import com.drivequant.drivekit.driverachievement.ui.utils.AlertDialogUtils
import com.drivequant.drivekit.driverachievement.ui.utils.DateUtils
import com.drivequant.drivekit.driverachievement.ui.utils.DistanceUtils
import com.drivequant.drivekit.driverachievement.ui.utils.DurationUtils


class StreakViewHolder(itemView: View, private val streaksViewConfig: StreaksViewConfig) :
    RecyclerView.ViewHolder(itemView) {
    private val context = itemView.context
    private val textViewStreakTitle = itemView.findViewById<TextView>(R.id.text_view_streak_title)
    private val textViewCurrentStreakTrip =
        itemView.findViewById<TextView>(R.id.text_view_current_streak_trip)
    private val textViewBestStreakTrip =
        itemView.findViewById<TextView>(R.id.text_view_best_streak_trip)
    private val textViewCurrentStreakDate =
        itemView.findViewById<TextView>(R.id.text_view_current_streak_date)
    private val textViewBestStreakDate =
        itemView.findViewById<TextView>(R.id.text_view_best_streak_date)
    private val textViewNbCurrentTrip =
        itemView.findViewById<TextView>(R.id.text_view_nb_current_trip)
    private val imageViewStreak = itemView.findViewById<ImageView>(R.id.image_view_streak_icon)
    private val seekBar = itemView.findViewById<SeekBar>(R.id.seekbar)
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
        val nbCurrentTrip = streaksData.getCurrentStreak().tripNumber
        val nbBestTrip = streaksData.getBestStreak().tripNumber
        val currentDistance =
            DistanceUtils().formatDistance(context, streaksData.getCurrentStreak().distance)
        val bestDistance =
            DistanceUtils().formatDistance(context, streaksData.getBestStreak().distance)
        val currentDuration =
            DurationUtils().formatDuration(context, streaksData.getCurrentStreak().duration)
        val bestDuration =
            DurationUtils().formatDuration(context, streaksData.getBestStreak().duration)
        val currentStartDate = DateUtils().formatDate(streaksData.getCurrentStreak().startDate)
        val currentEndDate = DateUtils().formatDate(streaksData.getCurrentStreak().endDate)
        val bestStartDate = DateUtils().formatDate(streaksData.getBestStreak().startDate)
        val bestEndDate = DateUtils().formatDate(streaksData.getBestStreak().endDate)
        textViewNbCurrentTrip.text = "$nbBestTrip"

        when (streaksData.getStreakStatus()) {
            StreaksStatus.INIT -> {

                textViewCurrentStreakTrip.text =
                    context.resources.getQuantityString(
                        R.plurals.streak_trip_plural, nbCurrentTrip,
                        nbCurrentTrip,
                        currentDistance,
                        currentDuration
                    )

                textViewBestStreakTrip.text =
                    context.resources.getQuantityString(
                        R.plurals.streak_trip_plural, nbBestTrip,
                        nbBestTrip,
                        bestDistance,
                        bestDuration
                    )

                textViewCurrentStreakDate.text = context.getString(
                    R.string.dk_streaks_since,
                    currentStartDate
                )

                textViewBestStreakDate.text = context.getString(R.string.dk_streaks_empty)
            }
            StreaksStatus.IN_PROGRESS -> {
                //TODO : resetStyle()
                // First streak
                if (nbCurrentTrip == nbBestTrip) {
                    if (nbCurrentTrip != 0) {
                        textViewNbCurrentTrip.setTextColor(Color.parseColor("#77e2b0"))
                    }

                    textViewCurrentStreakTrip.text = context.resources.getQuantityString(
                        R.plurals.streak_trip_plural,
                        nbCurrentTrip,
                        nbCurrentTrip,
                        currentDistance,
                        currentDuration
                    )
                    textViewBestStreakTrip.text = context.getString(R.string.dk_streaks_congrats)
                    textViewCurrentStreakDate.text = context.getString(
                        R.string.dk_streaks_since,
                        currentStartDate
                    )
                    textViewBestStreakDate.text =
                        context.getString(R.string.dk_streaks_congrats_text)
                } else {
                    // N - streak
                    if (nbCurrentTrip == 0 && nbBestTrip != 0) {
                        textViewCurrentStreakDate.text = streaksData.getResetText(context)
                    } else {
                        textViewCurrentStreakDate.text = context.getString(
                            R.string.dk_streaks_since_to,
                            currentStartDate,
                            currentEndDate
                        )
                    }

                    textViewCurrentStreakTrip.text =
                        context.resources.getQuantityString(
                            R.plurals.streak_trip_plural, nbCurrentTrip,
                            nbCurrentTrip,
                            currentDistance,
                            currentDuration
                        )

                    textViewBestStreakTrip.text =
                        context.resources.getQuantityString(
                            R.plurals.streak_trip_plural, nbBestTrip,
                            nbBestTrip,
                            bestDistance,
                            bestDuration
                        )

                    textViewBestStreakDate.text = context.getString(
                        R.string.dk_streaks_since_to,
                        bestStartDate,
                        bestEndDate
                    )
                }
            }
            StreaksStatus.BEST -> {
                textViewNbCurrentTrip.setTextColor(Color.parseColor("#77e2b0"))
                textViewBestStreakTrip.text = context.getString(R.string.dk_streaks_congrats)
                textViewBestStreakDate.text = context.getString(R.string.dk_streaks_congrats_text)
                textViewBestStreakTrip.textSize = 18f

                textViewBestStreakTrip.setTypeface(
                    textViewCurrentStreakDate.typeface,
                    Typeface.BOLD
                )
                textViewCurrentStreakTrip.text =
                    context.resources.getQuantityString(
                        R.plurals.streak_trip_plural, nbCurrentTrip,
                        nbCurrentTrip,
                        currentDistance,
                        currentDuration
                    )
                textViewCurrentStreakDate.text =
                    context.getString(
                        R.string.dk_streaks_since_to,
                        currentStartDate,
                        currentEndDate
                    )
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
                DialogInterface.OnClickListener { p0, _ -> p0.dismiss() })
            .show()
    }

    private fun resetStyle() {

    }

}