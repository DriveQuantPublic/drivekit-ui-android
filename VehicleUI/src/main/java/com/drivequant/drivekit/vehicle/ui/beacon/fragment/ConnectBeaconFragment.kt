package com.drivequant.drivekit.vehicle.ui.beacon.fragment

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
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import kotlinx.android.synthetic.main.fragment_beacon_connect.*

class ConnectBeaconFragment : Fragment() {

    companion object {
        fun newInstance(viewModel: BeaconViewModel, vehicle: Vehicle) : ConnectBeaconFragment {
            val fragment = ConnectBeaconFragment()
            fragment.viewModel = viewModel
            fragment.vehicle = vehicle // TODO remove vehicle params
            return fragment
        }
    }

    private lateinit var viewModel : BeaconViewModel
    private lateinit var vehicle : Vehicle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_beacon_connect, container, false).setDKStyle()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mainFontColor = DriveKitUI.colors.mainFontColor()

        text_view_connect_title.headLine1(mainFontColor)
        text_view_connect_title.text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_guide_title")

        text_view_connect_desc_1.normalText(mainFontColor)
        text_view_connect_desc_1.text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_guide_desc1")

        text_view_connect_desc_2.normalText(mainFontColor)
        text_view_connect_desc_2.text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_guide_desc2")

        text_view_connect_desc_3.normalText(mainFontColor)
        text_view_connect_desc_3.text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_guide_desc3")

        button_begin.button()
        button_begin.text =  DKResource.convertToString(requireContext(), "dk_vehicle_begin")
        button_begin.setOnClickListener {
            viewModel.onConnectButtonClicked()
        }

    }
}