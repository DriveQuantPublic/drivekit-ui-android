package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeType
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKTripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapTraceType
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import kotlinx.android.synthetic.main.driver_distraction_fragment.*

class DriverDistractionFragment : Fragment(), View.OnClickListener {


    companion object {
        fun newInstance(tripDetailViewModel: DKTripDetailViewModel): DriverDistractionFragment {
            val fragment = DriverDistractionFragment()
            fragment.viewModel = tripDetailViewModel
            return fragment
        }
    }

    private lateinit var viewModel: DKTripDetailViewModel
    private lateinit var phoneCallDurationBackground: GradientDrawable
    private lateinit var unlockNumberEventBackground: GradientDrawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.driver_distraction_fragment, container, false)
        view.setBackgroundColor(Color.WHITE)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("viewModel", viewModel)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getSerializable("viewModel") as TripDetailViewModel?)?.let {
            viewModel = it
        }
        phoneCallDurationBackground = phone_call_duration.background as GradientDrawable
        unlockNumberEventBackground = unlock_number_event.background as GradientDrawable

        phone_call_duration.apply {
            setOnClickListener(this@DriverDistractionFragment)
            text = viewModel.getPhoneCallsDuration(requireContext())
        }
        unlock_number_event.apply {
            setOnClickListener(this@DriverDistractionFragment)
            text = viewModel.getUnlockNumberEvent()
        }
        viewModel.getScore()?.let {
            score_gauge.configure(it, GaugeType.DISTRACTION, Typeface.BOLD)
        }

        gauge_type_title.text = requireContext().getString(R.string.dk_common_distraction)
        
        val textBasUnlock = DKResource.buildString(
            requireContext(),
            DriveKitUI.colors.secondaryColor(),
            DriveKitUI.colors.secondaryColor(),
            "dk_driverdata_unlock_screen_content",
            viewModel.getUnlockDuration(requireContext()),
            viewModel.getUnlockDistance(requireContext())
        )

        distraction_phone_call.apply {
            setDistractionEvent(viewModel.getPhoneCallsNumber(requireContext()).first)
            setDistractionContent(viewModel.getPhoneCallsNumber(requireContext()).second)
        }
        distraction_unlock.apply {
            setDistractionContent(textBasUnlock.toString())
            setDistractionEvent(DKResource.convertToString(context, "dk_driverdata_unlock_event"))
        }
        phoneCallDurationBackground.setStroke(2, DriveKitUI.colors.complementaryFontColor())
        unlockNumberEventBackground.setStroke(2, DriveKitUI.colors.complementaryFontColor())

        setStyle()
    }


    private fun setStyle() {
        gauge_type_title.normalText()
        phone_call_duration.headLine2(DriveKitUI.colors.primaryColor())
        unlock_number_event.headLine2(DriveKitUI.colors.primaryColor())
    }

    private fun setSelectedDistractionType(gradientDrawable: GradientDrawable, selected: Boolean) {
        val drawable = gradientDrawable.mutate()
        DrawableCompat.setTint(
            drawable,
            if (selected) DriveKitUI.colors.secondaryColor() else DriveKitUI.colors.complementaryFontColor()
        )
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.unlock_number_event -> {
                    viewModel.getSelectedTraceType().postValue(MapTraceType.UNLOCK_SCREEN)
                    setSelectedDistractionType(
                        unlockNumberEventBackground,
                        true
                    )
                    setSelectedDistractionType(
                        phoneCallDurationBackground,
                        false
                    )
                }
                R.id.phone_call_duration -> {
                    viewModel.getSelectedTraceType().postValue(MapTraceType.PHONE_CALL)
                    setSelectedDistractionType(
                        phoneCallDurationBackground,
                        true
                    )
                    setSelectedDistractionType(
                        unlockNumberEventBackground,
                        false
                    )
                }
            }
        }
    }
}
