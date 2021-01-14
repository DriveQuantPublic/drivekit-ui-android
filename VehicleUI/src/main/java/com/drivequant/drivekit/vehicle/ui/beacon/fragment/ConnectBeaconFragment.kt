package com.drivequant.drivekit.vehicle.ui.beacon.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import kotlinx.android.synthetic.main.fragment_beacon_connect.*

class ConnectBeaconFragment : Fragment() {

    companion object {
        fun newInstance(viewModel: BeaconViewModel) : ConnectBeaconFragment {
            val fragment = ConnectBeaconFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel : BeaconViewModel

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
            if (!viewModel.isBluetoothSensorEnabled()) {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(requireContext())
                    .layout(R.layout.template_alert_dialog_layout)
                    .positiveButton(
                        DKResource.convertToString(
                            requireContext(), "dk_common_ok"), DialogInterface.OnClickListener { _, _ ->
                            viewModel.apply {
                                enableBluetoothSensor()
                                onConnectButtonClicked()
                            }
                        })
                    .negativeButton(DKResource.convertToString(requireContext(), "dk_common_close"),
                        DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                    .show()

                val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                titleTextView?.apply {
                    text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_enable_bluetooth_alert_title")
                    headLine1()
                }
                descriptionTextView?.apply {
                    text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_enable_bluetooth_alert_message")
                    normalText()
                }
            } else {
                viewModel.onConnectButtonClicked()
            }
        }
    }
}