package com.drivequant.drivekit.ui.tripdetail.fragments

import android.arch.lifecycle.ViewModelProviders
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
import com.drivequant.drivekit.databaseutils.entity.Safety
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.SafetyViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.SafetyViewModelFactory
import kotlinx.android.synthetic.main.eco_driving_fragment.score_gauge
import kotlinx.android.synthetic.main.safety_fragment.*
import kotlinx.android.synthetic.main.safety_fragment.gauge_type_title

class SafetyFragment : Fragment() {

    companion object {
        fun newInstance(safety : Safety) : SafetyFragment {
            val fragment = SafetyFragment()
            fragment.safety = safety
            return fragment
        }
    }

    private lateinit var safety: Safety
    private lateinit var viewModel : SafetyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.safety_fragment, container, false)
        FontUtils.overrideFonts(context, view)
        view.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("safety", safety)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getSerializable("safety") as Safety?)?.let{
            safety = it
        }
        viewModel = ViewModelProviders.of(this,
            SafetyViewModelFactory(safety)
        ).get(
            SafetyViewModel::class.java)

        gauge_type_title.text = context?.getString(R.string.dk_common_safety)
        accel_description.text = context?.getString(R.string.dk_driverdata_safety_accel)
        brake_description.text = context?.getString(R.string.dk_driverdata_safety_decel)
        adherence_description.text = context?.getString(R.string.dk_driverdata_safety_adherence)

        val mainFontColor = DriveKitUI.colors.mainFontColor()
        val primaryColor = DriveKitUI.colors.primaryColor()

        gauge_type_title.normalText(mainFontColor)
        accel_description.normalText(mainFontColor)
        brake_description.normalText(mainFontColor)
        adherence_description.normalText(mainFontColor)

        accel_image.setColorFilter(mainFontColor)
        decel_image.setColorFilter(mainFontColor)
        adherence_image.setColorFilter(mainFontColor)

        accel_number_event.highlightSmall(primaryColor)
        brake_number_event.highlightSmall(primaryColor)
        adherence_number_event.highlightSmall(primaryColor)

        score_gauge.configure(viewModel.getScore(), GaugeType.SAFETY)
        accel_number_event.text = viewModel.getAccelNumberEvent().toString()
        brake_number_event.text = viewModel.getBrakeNumberEvent().toString()
        adherence_number_event.text = viewModel.getAdherenceNumberEvent().toString()
    }
}
