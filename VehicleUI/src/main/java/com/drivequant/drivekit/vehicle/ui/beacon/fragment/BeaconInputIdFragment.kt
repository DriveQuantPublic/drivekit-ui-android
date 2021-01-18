package com.drivequant.drivekit.vehicle.ui.beacon.fragment

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconInfoStatus
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import kotlinx.android.synthetic.main.fragment_beacon_input_id.*

class BeaconInputIdFragment : Fragment () {

    companion object {
        fun newInstance(viewModel: BeaconViewModel) : BeaconInputIdFragment {
            val fragment = BeaconInputIdFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel : BeaconViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_beacon_input_id, container, false).setDKStyle()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mainFontColor = DriveKitUI.colors.mainFontColor()

        text_view_beacon_code_text.bigText(mainFontColor)
        text_view_beacon_code_text.text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_code_title")

        code_wrapper.hint = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_code_hint")

        button_validate.button()
        button_validate.text = DKResource.convertToString(requireContext(), "dk_common_validate")
        button_validate.setOnClickListener {
            manageValidateClick(it)
        }

        if (viewModel.isFirstTime) {
            viewModel.codeObserver.observe(this, Observer {
                it?.let { map ->
                    val beaconCode = map.keys.first()
                    when (map[beaconCode]) {
                        VehicleBeaconInfoStatus.SUCCESS -> viewModel.onCodeValid()
                        VehicleBeaconInfoStatus.ERROR, VehicleBeaconInfoStatus.UNKNOWN_BEACON -> displayError(
                            beaconCode
                        )
                    }
                }
            })
        }
    }

    private fun displayError(vararg args: String) {
        val alert = DKAlertDialog.LayoutBuilder()
            .init(requireContext())
            .layout(R.layout.template_alert_dialog_layout)
            .cancelable(true)
            .positiveButton(DKResource.convertToString(requireContext(), "dk_common_ok"),
                DialogInterface.OnClickListener
                { dialog, _ -> dialog.dismiss() })
            .show()

        val title = alert.findViewById<TextView>(R.id.text_view_alert_title)
        val description = alert.findViewById<TextView>(R.id.text_view_alert_description)

        title?.text = DKResource.buildString(
            requireContext(),
            DriveKitUI.colors.mainFontColor(),
            DriveKitUI.colors.mainFontColor(),
            "app_name"
        )
        title?.headLine1()

        if (args.isNotEmpty()) {
            description?.text = DKResource.buildString(requireContext(),
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.mainFontColor(),"dk_vehicle_beacon_setup_code_invalid_id", args[0])
        } else {
            description?.text = DKResource.convertToString(requireContext(), "dk_vehicle_failed_to_retrieve_beacon")
        }
        description?.normalText()
    }

    private fun manageValidateClick(view: View){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        val codeValue: String = code_field.text.toString().trim()
        if (codeValue.isEmpty()){
            code_wrapper.isErrorEnabled = true
            code_wrapper.error = DKResource.convertToString(requireContext(), "dk_common_error_empty_field")
        } else {
            code_wrapper.isErrorEnabled = false
            viewModel.checkCode(codeValue)
            viewModel.isFirstTime = true
        }
    }
}