package com.drivequant.drivekit.ui.tripdetail.fragments

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeType
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.databaseutils.entity.EcoDriving
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.EcoDrivingViewModel
import kotlinx.android.synthetic.main.eco_driving_fragment.*
import kotlinx.android.synthetic.main.eco_driving_fragment.gauge_type_title
import kotlinx.android.synthetic.main.eco_driving_fragment.score_gauge
import kotlinx.android.synthetic.main.speeding_fragment.view.*

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
        view.score_info.setColorFilter(DriveKitUI.colors.secondaryColor())
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
        score_gauge.configure(viewModel.getScore(), GaugeType.ECO_DRIVING, Typeface.BOLD)
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

        score_info.setOnClickListener {
            val alert = DKAlertDialog.LayoutBuilder()
                .init(requireContext())
                .layout(R.layout.template_alert_dialog_layout)
                .cancelable(true)
                .positiveButton(
                    DKResource.convertToString(requireContext(), "dk_common_ok"),
                    DialogInterface.OnClickListener
                    { dialog, _ -> dialog.dismiss() })
                .show()

            val title = alert.findViewById<TextView>(R.id.text_view_alert_title)
            val description = alert.findViewById<TextView>(R.id.text_view_alert_description)
            val icon = alert.findViewById<ImageView>(R.id.image_view_alert_icon)

            title?.text =
                DKResource.convertToString(requireContext(), "dk_driverdata_eco_score")
            description?.text = DKResource.convertToString(
                requireContext(),
                "dk_driverdata_eco_score_info"
            )
            icon?.setImageResource(R.drawable.dk_common_ecodriving)
            title?.headLine1()
            description?.normalText()
        }
    }
}
