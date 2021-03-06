package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeConfiguration
import com.drivequant.drivekit.common.ui.extension.normalText
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
        view.setBackgroundColor(Color.WHITE)
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
        score_gauge.configure(viewModel.getScore(), GaugeConfiguration.ECO_DRIVING(viewModel.getScore()), Typeface.BOLD)
        score_info.init(GaugeConfiguration.ECO_DRIVING(viewModel.getScore()))

        accelAdvice.text = context?.getString(viewModel.getAccelMessage())
        mainAdvice.text = context?.getString(viewModel.getMaintainMessage())
        decelAdvice.text = context?.getString(viewModel.getDecelMessage())
        gauge_type_title.text = context?.getString(viewModel.getGaugeTitle())

        val mainFontColor = DriveKitUI.colors.mainFontColor()

        image_accel_advice.setColorFilter(mainFontColor)
        image_decel_advice.setColorFilter(mainFontColor)
        image_main_advice.setColorFilter(mainFontColor)

        accelAdvice.normalText(mainFontColor)
        mainAdvice.normalText(mainFontColor)
        decelAdvice.normalText(mainFontColor)
        gauge_type_title.normalText(mainFontColor)
    }
}
