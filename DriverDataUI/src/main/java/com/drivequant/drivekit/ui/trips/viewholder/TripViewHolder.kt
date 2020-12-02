package com.drivequant.drivekit.ui.trips.viewholder

import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
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
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.commons.views.TripInfoView
import com.drivequant.drivekit.ui.extension.getOrComputeStartDate
import com.drivequant.drivekit.ui.trips.viewmodel.AdviceTripInfo
import com.drivequant.drivekit.ui.trips.viewmodel.DKTripInfo
import com.drivequant.drivekit.ui.trips.viewmodel.DisplayType
import com.drivequant.drivekit.ui.trips.viewmodel.TripData

class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textViewDepartureTime = itemView.findViewById<TextView>(R.id.text_view_departure_time)
    private val textViewDepartureCity = itemView.findViewById<TextView>(R.id.text_view_departure_city)
    private val textViewArrivalTime = itemView.findViewById<TextView>(R.id.text_view_arrival_time)
    private val textViewArrivalCity = itemView.findViewById<TextView>(R.id.text_view_arrival_city)
    private val viewSeparator = itemView.findViewById<View>(R.id.view_separator)
    private val tripInfoContainer = itemView.findViewById<ViewGroup>(R.id.container_trip_info)
    private val gaugeIndicator = itemView.findViewById<GaugeIndicator>(R.id.gauge_indicator_view)
    private val textIndicator = itemView.findViewById<TextView>(R.id.text_view_value)
    private val noScoreView = itemView.findViewById<ImageView>(R.id.no_score_view)
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
        textViewDepartureTime.normalText(DriveKitUI.colors.complementaryFontColor())
        textViewArrivalTime.normalText(DriveKitUI.colors.complementaryFontColor())

        computeTripData(trip, DriverDataUI.tripData)
        val tripInfo = DriverDataUI.customTripInfo ?: run { AdviceTripInfo() }
        computeTripInfo(trip, tripInfo)
    }

    private fun computeTripData(trip: Trip, tripData: TripData){
        when (tripData.displayType()){
            DisplayType.GAUGE -> {
                if (tripData.isScored(trip)){
                    showGaugeIndicator()
                    gaugeIndicator.configure(tripData.rawValue(trip)!!, tripData.getGaugeType())
                } else {
                    showNoScoreIndicator()
                }
            }
            DisplayType.TEXT -> {
                showTextIndicator()
                textIndicator.headLine2(DriveKitUI.colors.primaryColor())
                textIndicator.text = if (tripData == TripData.DURATION) (DKDataFormatter.formatDuration(itemView.context, tripData.rawValue(trip))) else (DKDataFormatter.formatDistance(itemView.context, tripData.rawValue(trip)))
            }
        }
    }

    private fun showGaugeIndicator() {
        gaugeIndicator.visibility = View.VISIBLE
        noScoreView.visibility = View.GONE
        textIndicator.visibility = View.GONE
    }

    private fun showNoScoreIndicator() {
        gaugeIndicator.visibility = View.GONE
        noScoreView.visibility = View.VISIBLE
        textIndicator.visibility = View.GONE
    }

    private fun showTextIndicator(){
        gaugeIndicator.visibility = View.GONE
        noScoreView.visibility = View.GONE
        textIndicator.visibility = View.VISIBLE
    }

    private fun computeTripInfo(trip: Trip, tripInfo: DKTripInfo) {
        if (tripInfo.isDisplayable(trip)) {
            tripInfoContainer.addView(TripInfoView(itemView.context, trip, tripInfo))
            tripInfoContainer.visibility = View.VISIBLE
        } else {
            tripInfoContainer.visibility = View.GONE
        }
    }
}