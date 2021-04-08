package com.drivequant.drivekit.common.ui.component.tripslist.viewholder

import android.graphics.Color
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.GaugeIndicator
import com.drivequant.drivekit.common.ui.component.tripslist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.tripslist.DisplayType
import com.drivequant.drivekit.common.ui.component.tripslist.TripData
import com.drivequant.drivekit.common.ui.component.tripslist.TripInfoView
import com.drivequant.drivekit.common.ui.component.tripslist.extension.computeCeilDuration
import com.drivequant.drivekit.common.ui.component.tripslist.extension.getOrComputeStartDate
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKResource

internal class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textViewDepartureTime =
        itemView.findViewById<TextView>(R.id.text_view_departure_time)
    private val textViewDepartureCity =
        itemView.findViewById<TextView>(R.id.text_view_departure_city)
    private val textViewArrivalTime = itemView.findViewById<TextView>(R.id.text_view_arrival_time)
    private val textViewArrivalCity = itemView.findViewById<TextView>(R.id.text_view_arrival_city)
    private val viewSeparator = itemView.findViewById<View>(R.id.view_separator)
    private val tripInfoContainer = itemView.findViewById<ViewGroup>(R.id.container_trip_info)
    private val gaugeIndicator = itemView.findViewById<GaugeIndicator>(R.id.gauge_indicator_view)
    private val textIndicator = itemView.findViewById<TextView>(R.id.text_view_value)
    private val imageView = itemView.findViewById<ImageView>(R.id.no_score_view)
    private val circleTop = itemView.findViewById<ImageView>(R.id.image_circle_top)
    private val circleBottom = itemView.findViewById<ImageView>(R.id.image_circle_bottom)
    private val circleSeparator = itemView.findViewById<View>(R.id.view_circle_separator)

    fun bind(trip: DKTripListItem, tripData: TripData, isLastChild: Boolean) {
        textViewDepartureTime.text =
            trip.getOrComputeStartDate()?.formatDate(DKDatePattern.HOUR_MINUTE_LETTER)
        textViewDepartureCity.text = trip.getDepartureCity()
        textViewArrivalTime.text = trip.getEndDate().formatDate(DKDatePattern.HOUR_MINUTE_LETTER)
        textViewArrivalCity.text = trip.getArrivalCity()
        viewSeparator.visibility = if (isLastChild) View.GONE else View.VISIBLE
        viewSeparator.setBackgroundColor(DriveKitUI.colors.neutralColor())

        DrawableCompat.setTint(circleBottom.background, DriveKitUI.colors.secondaryColor())
        DrawableCompat.setTint(circleTop.background, DriveKitUI.colors.secondaryColor())
        circleSeparator.setBackgroundColor(DriveKitUI.colors.secondaryColor())

        textViewDepartureCity.normalText(DriveKitUI.colors.mainFontColor())
        textViewArrivalCity.normalText(DriveKitUI.colors.mainFontColor())
        textViewDepartureTime.normalText(Color.parseColor("#9E9E9E"))
        textViewArrivalTime.normalText(Color.parseColor("#9E9E9E"))

        computeTripData(trip, tripData)
        computeTripInfo(trip)
    }

    private fun computeTripData(trip: DKTripListItem, tripData: TripData) {
        if (trip.isAlternative()) {
            configureAlternativeTripData(trip)
        } else {
            configureMotorizedTripData(trip, tripData)
        }
    }

    private fun configureMotorizedTripData(trip: DKTripListItem, tripData: TripData) {
        when (tripData.displayType()) {
            DisplayType.GAUGE -> {
                if (trip.isScored(tripData)) {
                    showGaugeIndicator()
                    gaugeIndicator.configure(trip.getScore(tripData)!!, tripData.getGaugeType())
                } else {
                    showImageIndicator()
                    imageView.setImageDrawable(
                        DKResource.convertToDrawable(
                            imageView.context,
                            "dk_no_score"
                        )
                    )
                }
            }
            DisplayType.TEXT -> {
                showTextIndicator()
                textIndicator.headLine2(DriveKitUI.colors.primaryColor())
                textIndicator.text =
                    if (tripData == TripData.DURATION) {
                        DKDataFormatter.formatDuration(itemView.context, trip.computeCeilDuration())
                    } else {
                        DKDataFormatter.formatMeterDistanceInKm(
                            itemView.context,
                            trip.getScore(tripData)
                        )
                    }
            }
        }
    }

    private fun configureAlternativeTripData(trip: DKTripListItem) {
        trip.getTransportationModeResourceId(itemView.context)?.let {
             showImageIndicator()
            imageView.setImageDrawable(it)
        }
    }

    private fun showGaugeIndicator() {
        gaugeIndicator.visibility = View.VISIBLE
        imageView.visibility = View.GONE
        textIndicator.visibility = View.GONE
    }

    private fun showImageIndicator() {
        gaugeIndicator.visibility = View.GONE
        imageView.visibility = View.VISIBLE
        textIndicator.visibility = View.GONE
    }

    private fun showTextIndicator() {
        gaugeIndicator.visibility = View.GONE
        imageView.visibility = View.GONE
        textIndicator.visibility = View.VISIBLE
    }

    private fun computeTripInfo(trip: DKTripListItem) {
        trip.isDisplayable()?.let {
            tripInfoContainer.addView(TripInfoView(itemView.context, trip))
            tripInfoContainer.visibility = View.VISIBLE
        } ?: run {
            tripInfoContainer.removeAllViews()
            tripInfoContainer.visibility = View.GONE
        }
    }
}

