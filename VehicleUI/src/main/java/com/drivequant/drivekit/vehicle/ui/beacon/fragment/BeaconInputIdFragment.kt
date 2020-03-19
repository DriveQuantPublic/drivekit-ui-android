package com.drivequant.drivekit.vehicle.ui.beacon.fragment

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import kotlinx.android.synthetic.main.fragment_beacon_input_id.*

class BeaconInputIdFragment : Fragment () {

    companion object {
        fun newInstance(viewModel: BeaconViewModel, vehicle: Vehicle) : BeaconInputIdFragment {
            val fragment = BeaconInputIdFragment()
            fragment.viewModel = viewModel
            fragment.vehicle = vehicle
            return fragment
        }
    }

    private lateinit var viewModel : BeaconViewModel
    private lateinit var vehicle : Vehicle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_beacon_input_id, container, false).setDKStyle()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mainFontColor = DriveKitUI.colors.mainFontColor()

        text_view_beacon_code_text.normalText(mainFontColor)
        text_view_beacon_code_text.text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_code_title")

        code_wrapper.hint = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_code_hint")

        button_validate.button()
        button_validate.text = DKResource.convertToString(requireContext(), "dk_common_validate")
        button_validate.setOnClickListener {
            manageValidateClick()
        }

        viewModel.codeObserver.observe(this, Observer {

        })
    }

    private fun manageValidateClick(){
        val codeValue: String = code_field.text.toString().trim()
        if (codeValue.isEmpty()){
            code_wrapper.isErrorEnabled = true
            code_wrapper.error = DKResource.convertToString(requireContext(), "dk_common_error_empty_field")

        } else {
            code_wrapper.isErrorEnabled = false
            viewModel.checkCode(codeValue)
        }
    }
}