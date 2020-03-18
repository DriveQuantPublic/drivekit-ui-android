package com.drivequant.drivekit.vehicle.ui.bluetooth.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.bluetooth.viewmodel.BluetoothViewModel
import kotlinx.android.synthetic.main.fragment_bluetooth_error.*

class ErrorBluetoothFragment: Fragment() {

    companion object {
        fun newInstance(vehicleId: String): ErrorBluetoothFragment {
            val fragment = ErrorBluetoothFragment()
            fragment.vehicleId = vehicleId
            return fragment
        }
    }

    private lateinit var viewModel : BluetoothViewModel
    private lateinit var vehicleId : String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bluetooth_error, container, false).setDKStyle()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, BluetoothViewModel.BluetoothViewModelFactory(vehicleId)).get(BluetoothViewModel::class.java)

        val mainFontColor = DriveKitUI.colors.mainFontColor()
        val secondaryColor = DriveKitUI.colors.secondaryColor()

        text_view_bluetooth_failed.text = DKResource.convertToString(requireContext(), "dk_vehicle_bluetooth_not_found")
        text_view_bluetooth_failed.normalText(mainFontColor)

        button_cancel.text = DKResource.convertToString(requireContext(), "dk_common_cancel")
        button_cancel.button()
        button_cancel.setOnClickListener {
            activity?.finish()
        }

        text_view_open_settings.text = DKResource.convertToString(requireContext(), "dk_vehicle_open_bluetooth_settings")
        text_view_open_settings.normalText(secondaryColor)
        text_view_open_settings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_SETTINGS))
            activity?.finish()
        }
    }
}