package com.drivequant.drivekit.ui.tripdetail.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.TripDetailViewConfig
import com.drivequant.drivekit.ui.TripsViewConfig
import com.drivequant.drivekit.ui.tripdetail.viewmodel.UnscoredTripViewModel
import com.drivequant.drivekit.ui.utils.DurationUtils
import kotlinx.android.synthetic.main.unscored_trip_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class UnscoredTripFragment : Fragment() {
    companion object {
        fun newInstance(
            trip: Trip?,
            tripsViewConfig: TripsViewConfig,
            tripDetailViewConfig: TripDetailViewConfig
        ): UnscoredTripFragment {
            val fragment = UnscoredTripFragment()
            fragment.viewModel = UnscoredTripViewModel(
                trip,
                tripDetailViewConfig
            )
            fragment.tripsViewConfig = tripsViewConfig
            fragment.tripDetailViewConfig = tripDetailViewConfig
            return fragment
        }
    }

    private val dateFormatHour = SimpleDateFormat("HH'h'mm", Locale.getDefault())
    private lateinit var viewModel: UnscoredTripViewModel
    private lateinit var tripsViewConfig: TripsViewConfig
    private lateinit var tripDetailViewConfig: TripDetailViewConfig

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.unscored_trip_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        trip_duration.text = DurationUtils()
            .formatDuration(requireContext(), viewModel.getDuration())
        trip_duration.setTextColor(DriveKitUI.colors.primaryColor())
        trip_start_end.text = dateFormatHour.format(viewModel.getStartDate())
            .plus(" - ")
            .plus(dateFormatHour.format(viewModel.getEndDate()))
        trip_start_end.setTextColor(DriveKitUI.colors.primaryColor())
        trip_message.text = viewModel.getNoScoreTripMessage()
    }
}