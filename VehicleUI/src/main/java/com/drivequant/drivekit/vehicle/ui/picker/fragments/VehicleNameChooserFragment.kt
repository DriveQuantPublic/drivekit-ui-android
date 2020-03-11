package com.drivequant.drivekit.vehicle.ui.picker.fragments

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.vehicle.picker.VehicleVersion
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehiclePickerViewModel
import kotlinx.android.synthetic.main.fragment_vehicle_name_chooser.*

class VehicleNameChooserFragment : Fragment() {

    private lateinit var viewModel: VehiclePickerViewModel
    private lateinit var viewConfig: VehiclePickerViewConfig
    private lateinit var vehicleVersion: VehicleVersion

    companion object {
        fun newInstance(
            viewModel: VehiclePickerViewModel,
            vehiclePickerViewConfig: VehiclePickerViewConfig)
                : VehicleNameChooserFragment {
            val fragment = VehicleNameChooserFragment()
            fragment.viewModel = viewModel
            fragment.vehicleVersion = viewModel.selectedVersion
            fragment.viewConfig = vehiclePickerViewConfig
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vehicle_name_chooser, container, false)
        FontUtils.overrideFonts(context, view)
        view.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextWrapper = view.findViewById(R.id.text_input_layout) as TextInputLayout
        editTextWrapper.editText?.setText(viewModel.getDefaultVehicleName())
        button_validate.button()
        button_validate.setOnClickListener {
            // TODO createVehicle in VehiclePickerViewModel
        }
    }
}