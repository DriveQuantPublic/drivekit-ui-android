package com.drivequant.drivekit.ui.tripdetail.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeType
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.databaseutils.entity.EcoDriving
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.EcoDrivingViewModel
import kotlinx.android.synthetic.main.eco_driving_fragment.*

class EcoDrivingFragment : Fragment() {

    companion object {
        fun newInstance(ecoDriving: EcoDriving) : EcoDrivingFragment{
            val fragment = EcoDrivingFragment()
            fragment.viewModel = EcoDrivingViewModel(ecoDriving)
            return fragment
        }
    }

    private lateinit var viewModel: EcoDrivingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.eco_driving_fragment, container, false)
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
        (savedInstanceState?.getSerializable("viewModel") as EcoDrivingViewModel?)?.let {
            viewModel = it
        }
        score_gauge.configure(viewModel.getScore(), GaugeType.ECO_DRIVING)
        accelAdvice.text = context?.getString(viewModel.getAccelMessage())
        mainAdvice.text = context?.getString(viewModel.getMaintainMessage())
        decelAdvice.text = context?.getString(viewModel.getDecelMessage())
        gauge_type_title.text = context?.getString(viewModel.getGaugeTitle())

        val mainFontColor = DriveKitUI.colors.mainFontColor()

        image_accel_advice.setColorFilter(mainFontColor)
        image_decel_advice.setColorFilter(mainFontColor)
        image_main_advice.setColorFilter(mainFontColor)

        accelAdvice.bigText(mainFontColor)
        mainAdvice.bigText(mainFontColor)
        decelAdvice.bigText(mainFontColor)
        gauge_type_title.bigText(mainFontColor)
    }
}
