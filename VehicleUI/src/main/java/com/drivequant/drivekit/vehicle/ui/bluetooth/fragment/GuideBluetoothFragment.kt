package com.drivequant.drivekit.vehicle.ui.bluetooth.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.bluetooth.viewmodel.BluetoothViewModel
import kotlinx.android.synthetic.main.fragment_bluetooth_guide.*

class GuideBluetoothFragment: Fragment() {

    companion object {
        fun newInstance(viewModel: BluetoothViewModel, vehicleId: String): GuideBluetoothFragment {
            val fragment = GuideBluetoothFragment()
            fragment.viewModel = viewModel
            fragment.vehicleId = vehicleId
            return fragment
        }
    }

    private lateinit var viewModel : BluetoothViewModel
    private lateinit var vehicleId : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bluetooth_guide, container, false).setDKStyle()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        text_view_guide_title.headLine1()
        text_view_guide_title.text = DKResource.convertToString(requireContext(), "dk_vehicle_bluetooth_guide_header")

        text_view_guide_desc1.normalText()
        text_view_guide_desc1.text = DKResource.convertToString(requireContext(), "dk_vehicle_bluetooth_guide_desc1_android")

        text_view_guide_desc2.normalText()
        text_view_guide_desc2.text = DKResource.convertToString(requireContext(), "dk_vehicle_bluetooth_guide_desc2_android")

        text_view_guide_desc3.normalText()
        text_view_guide_desc3.text = DKResource.convertToString(requireContext(), "dk_vehicle_bluetooth_guide_desc3_android")

        button_start.button()
        button_start.text = DKResource.convertToString(requireContext(), "dk_vehicle_begin")
        button_start.setOnClickListener {
            viewModel.onStartButtonClicked()
        }
    }
}