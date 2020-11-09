package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeType
import com.drivequant.drivekit.common.ui.extension.highlightSmall
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.databaseutils.entity.DriverDistraction
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DriverDistractionViewModel
import kotlinx.android.synthetic.main.driver_distraction_fragment.*

class DriverDistractionFragment : Fragment() {

    companion object {
        fun newInstance(driverDistraction: DriverDistraction) : DriverDistractionFragment {
            val fragment = DriverDistractionFragment()
            fragment.viewModel =
                DriverDistractionViewModel(
                    driverDistraction
                )
            return fragment
        }
    }

    private lateinit var viewModel: DriverDistractionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.driver_distraction_fragment, container, false)
        FontUtils.overrideFonts(context, view)
        view.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("viewModel", viewModel)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getSerializable("viewModel") as DriverDistractionViewModel?)?.let{
            viewModel = it
        }
        gauge_type_title.text = requireContext().getString(R.string.dk_common_distraction)
        unlockNumberDescription.text = requireContext().getString(R.string.dk_driverdata_unlock_number)
        unlockDistanceDescription.text = requireContext().getString(R.string.dk_driverdata_unlock_distance)
        unlockDurationDescription.text = requireContext().getString(R.string.dk_driverdata_unlock_duration)

        gauge_type_title.normalText()
        unlockNumberDescription.normalText()
        unlockDistanceDescription.normalText()
        unlockDurationDescription.normalText()

        score_gauge.configure(viewModel.getScore(), GaugeType.DISTRACTION, Typeface.BOLD)
        unlockNumberEvent.text = viewModel.getUnlockNumberEvent()
        distanceUnlocked.text = viewModel.getUnlockDistance(requireContext())
        durationUnlocked.text = viewModel.getUnlockDuration(requireContext())

        unlockNumberEvent.highlightSmall(DriveKitUI.colors.primaryColor())
        distanceUnlocked.highlightSmall(DriveKitUI.colors.primaryColor())
        durationUnlocked.highlightSmall(DriveKitUI.colors.primaryColor())
    }
}
