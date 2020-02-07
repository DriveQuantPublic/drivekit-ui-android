package com.drivequant.drivekit.driverachievement.ui.streaks.viewholder

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.StreaksViewConfig
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksData
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksStatus
import com.drivequant.drivekit.driverachievement.ui.utils.DistanceUtils
import com.drivequant.drivekit.driverachievement.ui.utils.DurationUtils
import java.text.SimpleDateFormat
import java.util.*

class StreakViewHolder(itemView: View, private val streaksViewConfig: StreaksViewConfig) :
    RecyclerView.ViewHolder(itemView) {
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    private val textViewStreakTitle = itemView.findViewById<TextView>(R.id.text_view_streak_title)
    private val textViewCurrentStreakTrip = itemView.findViewById<TextView>(R.id.text_view_current_streak_trip)
    private val textViewBestStreakTrip = itemView.findViewById<TextView>(R.id.text_view_best_streak_trip)
    private val textViewCurrentStreakDate = itemView.findViewById<TextView>(R.id.text_view_current_streak_date)
    private val textViewBestStreakDate = itemView.findViewById<TextView>(R.id.text_view_best_streak_date)
    private val textViewNbCurrentTrip = itemView.findViewById<TextView>(R.id.text_view_nb_current_trip)
    private val imageViewStreak = itemView.findViewById<ImageView>(R.id.image_view_streak_icon)
    private val context = itemView.context

    fun bind(streaksData: StreaksData) {
        val nbCurrentTrip = streaksData.getCurrentStreak().tripNumber
        val nbBestTrip = streaksData.getBestStreak().tripNumber

        textViewStreakTitle.text = streaksData.getTitle(itemView.context)
        imageViewStreak.setImageResource(streaksData.getIcon())

        textViewNbCurrentTrip.text = "$nbCurrentTrip"

            when (streaksData.getStreakStatus()) {
            StreaksStatus.INIT -> {

                textViewCurrentStreakTrip.text =
                    context.resources.getQuantityString(
                        R.plurals.streak_trip_plural, nbCurrentTrip,
                        streaksData.getCurrentStreak().tripNumber,
                        DistanceUtils().formatDistance(
                            context,
                            streaksData.getCurrentStreak().distance
                        ),
                        DurationUtils().formatDuration(
                            context,
                            streaksData.getCurrentStreak().duration
                        )
                    )

                textViewBestStreakTrip.text =
                    context.resources.getQuantityString(
                        R.plurals.streak_trip_plural, nbBestTrip,
                        streaksData.getBestStreak().tripNumber,
                        DistanceUtils().formatDistance(
                            context,
                            streaksData.getBestStreak().distance
                        ),
                        DurationUtils().formatDuration(
                            context,
                            streaksData.getBestStreak().duration
                        )
                    )

                textViewCurrentStreakDate.text = itemView.context.getString(
                    R.string.dk_streaks_since,
                    dateFormat.format(streaksData.getCurrentStreak().startDate)
                )

                textViewBestStreakDate.text = context.getString(R.string.dk_streaks_empty)
            }

            StreaksStatus.FIRST -> {
                textViewNbCurrentTrip.setTextColor(Color.parseColor("#77e2b0"))

                textViewCurrentStreakTrip.text =
                    context.resources.getQuantityString(
                        R.plurals.streak_trip_plural, nbCurrentTrip,
                        streaksData.getCurrentStreak().tripNumber,
                        DistanceUtils().formatDistance(
                            context,
                            streaksData.getCurrentStreak().distance
                        ),
                        DurationUtils().formatDuration(
                            context,
                            streaksData.getCurrentStreak().duration
                        )
                    )
                textViewBestStreakTrip.text = context.getString(R.string.dk_streaks_congrats)

                textViewCurrentStreakDate.text = context.getString(
                    R.string.dk_streaks_since,
                    dateFormat.format(streaksData.getCurrentStreak().startDate)
                )
                textViewBestStreakDate.text = context.getString(R.string.streaks_congrats_text)
            }

            StreaksStatus.IN_PROGRESS -> {

                textViewCurrentStreakTrip.text =
                    context.resources.getQuantityString(
                        R.plurals.streak_trip_plural, nbCurrentTrip,
                        streaksData.getCurrentStreak().tripNumber,
                        DistanceUtils().formatDistance(
                            context,
                            streaksData.getCurrentStreak().distance
                        ),
                        DurationUtils().formatDuration(
                            context,
                            streaksData.getCurrentStreak().duration
                        )
                    )

                textViewBestStreakTrip.text =
                    context.resources.getQuantityString(
                        R.plurals.streak_trip_plural, nbBestTrip,
                        streaksData.getBestStreak().tripNumber,
                        DistanceUtils().formatDistance(
                            context,
                            streaksData.getBestStreak().distance
                        ),
                        DurationUtils().formatDuration(
                            context,
                            streaksData.getBestStreak().duration
                        )
                    )

                textViewCurrentStreakDate.text = context.getString(
                    R.string.dk_streaks_since_to,
                    dateFormat.format(streaksData.getCurrentStreak().startDate),
                    dateFormat.format(streaksData.getCurrentStreak().endDate)
                )

                textViewBestStreakDate.text = context.getString(
                    R.string.dk_streaks_since_to,
                    dateFormat.format(streaksData.getBestStreak().startDate),
                    dateFormat.format(streaksData.getBestStreak().endDate)
                )
            }

            StreaksStatus.BEST -> {
                textViewNbCurrentTrip.setTextColor(Color.parseColor("#77e2b0"))
                textViewBestStreakTrip.text = context.getString(R.string.dk_streaks_congrats)
                textViewBestStreakDate.text = context.getString(R.string.streaks_congrats_text)

                textViewCurrentStreakTrip.text =
                    context.resources.getQuantityString(
                        R.plurals.streak_trip_plural, nbCurrentTrip,
                        streaksData.getCurrentStreak().tripNumber,
                        DistanceUtils().formatDistance(
                            context,
                            streaksData.getCurrentStreak().distance
                        ),
                        DurationUtils().formatDuration(
                            context,
                            streaksData.getCurrentStreak().duration
                        )
                    )
                textViewCurrentStreakDate.text =
                    context.getString(
                        R.string.dk_streaks_since_to,
                        dateFormat.format(streaksData.getCurrentStreak().startDate),
                        dateFormat.format(streaksData.getCurrentStreak().endDate)
                    )
            }
        }
    }
}