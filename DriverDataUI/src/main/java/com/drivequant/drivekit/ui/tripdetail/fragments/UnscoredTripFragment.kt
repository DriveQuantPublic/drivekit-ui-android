package com.drivequant.drivekit.ui.tripdetail.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.UnscoredTripViewModel
import kotlinx.android.synthetic.main.unscored_trip_fragment.*

class UnscoredTripFragment : Fragment() {
    companion object {
        fun newInstance(
            trip: Trip?
        ): UnscoredTripFragment {
            val fragment = UnscoredTripFragment()
            fragment.viewModel = UnscoredTripViewModel(trip)
            return fragment
        }
    }

    private lateinit var viewModel: UnscoredTripViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.unscored_trip_fragment, container, false)
        FontUtils.overrideFonts(context, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        trip_duration.text = DKDataFormatter.formatDuration(requireContext(), viewModel.getDuration()!!)
        trip_duration.setTextColor(DriveKitUI.colors.primaryColor())
        trip_start_end.text = viewModel.getStartDate()?.formatDate(DKDatePattern.FORMAT_HOUR_MINUTE_LETTER)
            .plus(" - ")
            .plus(viewModel.getEndDate()?.formatDate(DKDatePattern.FORMAT_HOUR_MINUTE_LETTER))
        trip_start_end.setTextColor(DriveKitUI.colors.primaryColor())
        trip_message.text = context?.getString(viewModel.getNoScoreTripMessage())
    }
}