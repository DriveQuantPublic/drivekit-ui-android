package com.drivequant.drivekit.ui.trips.viewholder

import android.graphics.Color
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeIndicator
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.commons.views.TripInfoView
import com.drivequant.drivekit.ui.extension.computeCeilDuration
import com.drivequant.drivekit.ui.extension.getOrComputeStartDate
import com.drivequant.drivekit.ui.extension.image
import com.drivequant.drivekit.ui.trips.viewmodel.*
import com.drivequant.drivekit.ui.trips.viewmodel.TripsListViewModel

internal class TripViewHolder(itemView: View, private val viewModel: TripsListViewModel) : RecyclerView.ViewHolder(itemView) {
    private val textViewDepartureTime = itemView.findViewById<TextView>(R.id.text_view_departure_time)
    private val textViewDepartureCity = itemView.findViewById<TextView>(R.id.text_view_departure_city)
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

    fun bind(trip: Trip, isLastChild: Boolean){
        textViewDepartureTime.text = trip.getOrComputeStartDate()?.formatDate(DKDatePattern.HOUR_MINUTE_LETTER)
        textViewDepartureCity.text = trip.departureCity
        textViewArrivalTime.text = trip.endDate.formatDate(DKDatePattern.HOUR_MINUTE_LETTER)
        textViewArrivalCity.text = trip.arrivalCity
        viewSeparator.visibility = if (isLastChild) View.GONE else View.VISIBLE
        viewSeparator.setBackgroundColor(DriveKitUI.colors.neutralColor())

        DrawableCompat.setTint(circleBottom.background, DriveKitUI.colors.secondaryColor())
        DrawableCompat.setTint(circleTop.background, DriveKitUI.colors.secondaryColor())
        circleSeparator.setBackgroundColor(DriveKitUI.colors.secondaryColor())

        textViewDepartureCity.normalText(DriveKitUI.colors.mainFontColor())
        textViewArrivalCity.normalText(DriveKitUI.colors.mainFontColor())
        textViewDepartureTime.normalText(Color.parseColor("#9E9E9E"))
        textViewArrivalTime.normalText(Color.parseColor("#9E9E9E"))

        computeTripData(trip, viewModel.tripListConfiguration)
        computeTripInfo(trip, viewModel.getTripInfo())
    }
  
    private fun computeTripData(trip: Trip, tripListConfiguration: TripListConfiguration) {
        when (tripListConfiguration) {
            is TripListConfiguration.MOTORIZED -> configureMotorizedTripData(trip)
            is TripListConfiguration.ALTERNATIVE -> configureAlternativeTripData(trip)
        }
    }

    private fun configureMotorizedTripData(trip: Trip) {
        val tripData = DriverDataUI.tripData
        when (tripData.displayType()) {
            DisplayType.GAUGE -> {
                if (tripData.isScored(trip)) {
                    showGaugeIndicator()
                    gaugeIndicator.configure(tripData.rawValue(trip)!!, tripData.getGaugeType())
                } else {
                    showImageIndicator(trip)
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
                            tripData.rawValue(trip)
                        )
                    }
            }
        }
    }

    private fun configureAlternativeTripData(trip: Trip) {
        showImageIndicator(trip)
    }

    private fun showGaugeIndicator() {
        gaugeIndicator.visibility = View.VISIBLE
        imageView.visibility = View.GONE
        textIndicator.visibility = View.GONE
    }

    private fun showImageIndicator(trip: Trip) {
        gaugeIndicator.visibility = View.GONE
        imageView.visibility = View.VISIBLE
        textIndicator.visibility = View.GONE

        when (viewModel.tripListConfiguration){
            is TripListConfiguration.MOTORIZED -> {
                imageView.setImageDrawable(DKResource.convertToDrawable(imageView.context, "dk_no_score"))
            }
            is TripListConfiguration.ALTERNATIVE -> {
                val mode = trip.declaredTransportationMode?.transportationMode ?: trip.transportationMode
                imageView.setImageDrawable(mode.image(itemView.context))
            }
        }
    }

    private fun showTextIndicator(){
        gaugeIndicator.visibility = View.GONE
        imageView.visibility = View.GONE
        textIndicator.visibility = View.VISIBLE
    }

    private fun computeTripInfo(trip: Trip, tripInfo: DKTripInfo?) {
        if (tripInfo != null && tripInfo.isDisplayable(trip)) {
            tripInfoContainer.addView(TripInfoView(itemView.context, trip, tripInfo))
            tripInfoContainer.visibility = View.VISIBLE
        } else {
            tripInfoContainer.removeAllViews()
            tripInfoContainer.visibility = View.GONE
        }
    }
}