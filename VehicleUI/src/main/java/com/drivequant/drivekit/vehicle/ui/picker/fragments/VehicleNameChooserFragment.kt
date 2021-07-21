package com.drivequant.drivekit.vehicle.ui.picker.fragments

import android.os.Bundle
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehiclePickerViewModel
import kotlinx.android.synthetic.main.fragment_vehicle_name_chooser.*

class VehicleNameChooserFragment : Fragment() {

    private lateinit var viewModel: VehiclePickerViewModel

    companion object {
        fun newInstance(
            viewModel: VehiclePickerViewModel)
                : VehicleNameChooserFragment {
            val fragment = VehicleNameChooserFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_vehicle_name_chooser, container, false).setDKStyle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!this::viewModel.isInitialized) {
            viewModel =
                ViewModelProviders.of(this, VehiclePickerViewModel.VehiclePickerViewModelFactory())
                    .get(VehiclePickerViewModel::class.java)
        }
        dk_image_view_vehicle_name.setImageDrawable(DKResource.convertToDrawable(requireContext(), "dk_vehicle_name_chooser"))
        text_view_description.normalText()
        text_view_description.text = DKResource.convertToString(requireContext(), "dk_vehicle_name_chooser_description")

        val editTextWrapper = view.findViewById(R.id.text_input_layout) as TextInputLayout
        editTextWrapper.editText?.setText(viewModel.getDefaultVehicleName())
        button_validate.button()
        button_validate.text = DKResource.convertToString(requireContext(), "dk_common_validate")
        button_validate.setOnClickListener {
            viewModel.name = editTextWrapper.editText?.editableText.toString()
            viewModel.computeNextScreen(requireContext(), VehiclePickerStep.NAME)
        }
    }
}