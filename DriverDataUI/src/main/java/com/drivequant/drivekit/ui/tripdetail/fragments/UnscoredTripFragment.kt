package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.UnscoredTripViewModel
import kotlinx.android.synthetic.main.unscored_trip_fragment.image_view_unscored_trip_info
import kotlinx.android.synthetic.main.unscored_trip_fragment.trip_duration
import kotlinx.android.synthetic.main.unscored_trip_fragment.trip_message
import kotlinx.android.synthetic.main.unscored_trip_fragment.trip_start_end

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
        view.setDKStyle(Color.WHITE)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("viewModel", viewModel)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.getSerializableCompat("viewModel", UnscoredTripViewModel::class.java)?.let {
            viewModel = it
        }

        trip_duration.text = DKDataFormatter.formatDuration(requireContext(), viewModel.getDuration()!!).convertToString()
        trip_start_end.text = viewModel.getStartDate()?.formatDate(DKDatePattern.HOUR_MINUTE_LETTER)
            .plus(" - ")
            .plus(viewModel.getEndDate()?.formatDate(DKDatePattern.HOUR_MINUTE_LETTER))
        trip_message.text = context?.getString(viewModel.getNoScoreTripMessage())

        trip_message.setTextColor(DriveKitUI.colors.fontColorOnSecondaryColor())
        trip_message.setBackgroundColor(DriveKitUI.colors.warningColor())
        trip_start_end.setTextColor(DriveKitUI.colors.primaryColor())
        trip_duration.highlightMedium(DriveKitUI.colors.primaryColor())
        image_view_unscored_trip_info.background.tintDrawable(DriveKitUI.colors.warningColor())
    }
}
