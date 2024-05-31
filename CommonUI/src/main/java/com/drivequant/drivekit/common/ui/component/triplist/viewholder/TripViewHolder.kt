package com.drivequant.drivekit.common.ui.component.triplist.viewholder

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.GaugeIndicator
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.DisplayType
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.component.triplist.extension.computeArrivalInfo
import com.drivequant.drivekit.common.ui.component.triplist.extension.computeCeilDuration
import com.drivequant.drivekit.common.ui.component.triplist.extension.computeDepartureInfo
import com.drivequant.drivekit.common.ui.component.triplist.extension.getOrComputeStartDate
import com.drivequant.drivekit.common.ui.component.triplist.views.TripInfoView
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.extension.tint
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.convertToString


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

    fun bind(trip: DKTripListItem, tripData: TripData, isLastChild: Boolean) {
        textViewDepartureTime.text =
            trip.getOrComputeStartDate()?.formatDate(DKDatePattern.HOUR_MINUTE_LETTER)
        textViewDepartureCity.text = trip.computeDepartureInfo()
        textViewArrivalTime.text = trip.getEndDate().formatDate(DKDatePattern.HOUR_MINUTE_LETTER)
        textViewArrivalCity.text = trip.computeArrivalInfo()
        viewSeparator.visibility = if (isLastChild) View.GONE else View.VISIBLE

        val context = itemView.context
        circleBottom.background.tint(context, R.color.secondaryColor)
        circleTop.background.tint(context, R.color.secondaryColor)

        textViewDepartureCity.normalText()
        textViewArrivalCity.normalText()
        textViewDepartureTime.normalText()
        textViewArrivalTime.normalText()

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
                if (trip.isScored(tripData) && trip.getScore(tripData) != null) {
                    showGaugeIndicator()
                    gaugeIndicator.configure(
                        trip.getScore(tripData)!!,
                        tripData.getGaugeType(trip.getScore(tripData)!!)
                    )
                } else {
                    showImageIndicator()
                    imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context, R.drawable.dk_no_score))
                }
            }
            DisplayType.TEXT -> {
                showTextIndicator()
                textIndicator.smallText(true)
                textIndicator.text =
                    if (tripData == TripData.DURATION) {
                        DKDataFormatter.formatDuration(itemView.context, trip.computeCeilDuration())
                            .convertToString()
                    } else {
                        DKDataFormatter.formatMeterDistanceInKm(
                            itemView.context,
                            trip.getScore(tripData)
                        ).convertToString()
                    }
            }
        }
    }

    private fun configureAlternativeTripData(trip: DKTripListItem) {
        trip.getTransportationModeResource(itemView.context)?.let {
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
        if (trip.isInfoDisplayable()) {
            tripInfoContainer.addView(
                TripInfoView(
                    itemView.context,
                    trip
                )
            )
            tripInfoContainer.visibility = View.VISIBLE
        } else {
            tripInfoContainer.removeAllViews()
            tripInfoContainer.visibility = View.INVISIBLE
        }
    }
}
