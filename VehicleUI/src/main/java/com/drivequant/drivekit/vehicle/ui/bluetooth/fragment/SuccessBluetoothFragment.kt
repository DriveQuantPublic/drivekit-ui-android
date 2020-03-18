package com.drivequant.drivekit.vehicle.ui.bluetooth.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.bluetooth.viewmodel.BluetoothViewModel
import kotlinx.android.synthetic.main.fragment_bluetooth_success.*

class SuccessBluetoothFragment: Fragment() {

    companion object {
        fun newInstance(viewModel: BluetoothViewModel, vehicle: Vehicle): SuccessBluetoothFragment {
            val fragment = SuccessBluetoothFragment()
            fragment.viewModel = viewModel
            fragment.vehicle = vehicle
            return fragment
        }
    }

    private lateinit var viewModel : BluetoothViewModel
    private lateinit var vehicle: Vehicle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bluetooth_success, container, false).setDKStyle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainFontColor = DriveKitUI.colors.mainFontColor()
        val btDeviceName = vehicle.bluetooth?.name?.let { it }?: run { " "} // TODO method to retrieve btName (which can be btMacAddress)
        val vehicleName = vehicle.name // TODO computeTitle

        text_view_congrats_title.headLine1(mainFontColor)
        text_view_congrats_title.text = DKResource.convertToString(view.context, "dk_vehicle_bluetooth_congrats_title")

        text_view_congrats_description.normalText(mainFontColor)
        text_view_congrats_description.text = DKResource.buildString(view.context, "dk_vehicle_bluetooth_congrats_desc", btDeviceName, vehicleName!!) // TODO: remove !!

        text_view_congrats_notice.normalText(mainFontColor)
        text_view_congrats_notice.text = DKResource.convertToString(view.context, "dk_vehicle_bluetooth_congrats_notice")

        button_finish.text = DKResource.convertToString(view.context, "dk_common_finish")
        button_finish.button()
        button_finish.setOnClickListener {
            activity?.finish()
        }
    }
}