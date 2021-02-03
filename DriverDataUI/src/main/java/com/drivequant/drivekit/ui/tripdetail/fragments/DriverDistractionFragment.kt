package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeType
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKTripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import kotlinx.android.synthetic.main.driver_distraction_fragment.*

class DriverDistractionFragment : Fragment() {

    companion object {
        fun newInstance(tripDetailViewModel: DKTripDetailViewModel): DriverDistractionFragment {
            val fragment = DriverDistractionFragment()
            fragment.viewModel = tripDetailViewModel
            return fragment
        }
    }

    private lateinit var viewModel: DKTripDetailViewModel

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

        score_gauge.configure(viewModel.getScore()!!, GaugeType.DISTRACTION, Typeface.BOLD)
        gauge_type_title.text = requireContext().getString(R.string.dk_common_distraction)
        gauge_type_title.normalText()


        distraction_phone_call.setDistractionEvent("1 appel téléphonique")
        distraction_unlock.setDistractionContent("écran allumé 1m30 et 1,5 km")

        distraction_unlock.setDistractionEvent("Déverouillages de l'écran")
        distraction_phone_call.setDistractionContent("256 m parcourus")

        phone_call_duration.headLine2(DriveKitUI.colors.primaryColor())
        unlock_number_event.headLine2(DriveKitUI.colors.primaryColor())

        unlock_number_event.text = "3"
        phone_call_duration.text = "15 s"

        val phoneCallDurationBackground =  phone_call_duration.background as GradientDrawable
        val unlockNumberEventBackground =  unlock_number_event.background as GradientDrawable
        phoneCallDurationBackground.setStroke(5, DriveKitUI.colors.complementaryFontColor())
        unlockNumberEventBackground.setStroke(5, DriveKitUI.colors.complementaryFontColor())
    }
}
