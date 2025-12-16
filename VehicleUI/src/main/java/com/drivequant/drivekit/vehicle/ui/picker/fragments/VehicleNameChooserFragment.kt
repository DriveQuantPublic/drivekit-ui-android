package com.drivequant.drivekit.vehicle.ui.picker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.component.DKPrimaryButton
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.databinding.FragmentVehicleNameChooserBinding
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehiclePickerViewModel
import com.google.android.material.textfield.TextInputLayout

class VehicleNameChooserFragment : Fragment() {

    private lateinit var viewModel: VehiclePickerViewModel

    val buttonEnableState by lazy { mutableStateOf(true) }

    private var _binding: FragmentVehicleNameChooserBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    companion object {
        fun newInstance(
            viewModel: VehiclePickerViewModel
        ): VehicleNameChooserFragment {
            val fragment = VehicleNameChooserFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVehicleNameChooserBinding.inflate(inflater, container, false)
        binding.root.setDKStyle()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!this::viewModel.isInitialized) {
            viewModel =
                ViewModelProvider(this, VehiclePickerViewModel.VehiclePickerViewModelFactory())[VehiclePickerViewModel::class.java]
        }
        binding.dkImageViewVehicleName.setImageResource(R.drawable.dk_vehicle_name_chooser)
        binding.textViewDescription.normalText()

        val editTextWrapper = view.findViewById(R.id.text_input_layout) as TextInputLayout
        editTextWrapper.editText?.setText(viewModel.getDefaultVehicleName())

        binding.buttonValidate.setContent {
            DKPrimaryButton(
                getString(com.drivequant.drivekit.common.ui.R.string.dk_common_validate),
                isEnabled = buttonEnableState.value
            ) {
                viewModel.progressBarObserver.observe(viewLifecycleOwner) { displayProgressCircular ->
                    displayProgressCircular?.let {
                        this.buttonEnableState.value = !displayProgressCircular
                    }
                }
                viewModel.name = editTextWrapper.editText?.editableText.toString()
                viewModel.computeNextScreen(requireContext(), VehiclePickerStep.NAME)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
