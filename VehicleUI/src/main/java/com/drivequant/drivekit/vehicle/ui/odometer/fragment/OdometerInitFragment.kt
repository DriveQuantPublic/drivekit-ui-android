package com.drivequant.drivekit.vehicle.ui.odometer.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.databinding.DkFragmentOdometerInitBinding
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerHistoryDetailViewModel

class OdometerInitFragment : Fragment() {

    private var vehicleId: String? = null
    private lateinit var viewModel: OdometerHistoryDetailViewModel
    private var _binding: DkFragmentOdometerInitBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    companion object {
        fun newInstance(vehicleId: String?) =
            OdometerInitFragment().apply {
                arguments = Bundle().apply {
                    putString("vehicleIdTag", vehicleId)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vehicleId = it.getString("vehicleIdTag")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        vehicleId?.let {
            outState.putString("vehicleIdTag", it)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DkFragmentOdometerInitBinding.inflate(inflater, container, false)
        binding.root.setDKStyle(android.R.color.white)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_vehicles_odometer_init), javaClass.simpleName)
        savedInstanceState?.getString("vehicleIdTag")?.let {
            vehicleId = it
        }
        vehicleId?.let { vehicleId ->
            context?.let { context ->
                viewModel = ViewModelProvider(this,
                    OdometerHistoryDetailViewModel.OdometerHistoryDetailViewModelFactory(vehicleId,
                        -1))[OdometerHistoryDetailViewModel::class.java]
                addOdometerReading(context)

                binding.textViewOdometerDesc.apply {
                    setText(R.string.dk_vehicle_odometer_car_desc)
                    normalText()
                }

                binding.textViewVehicleDistanceField.apply {
                    setHint(R.string.dk_vehicle_odometer_enter_mileage)
                    normalText()
                }
                viewModel.odometerActionObserver.observe(requireActivity()) {
                    updateProgressVisibility(false)
                    Toast.makeText(
                        context,
                        it.first,
                        Toast.LENGTH_LONG
                    ).show()
                    if (it.second) {
                        DriveKitVehicleUI.vehiclePickerComplete?.onVehiclePickerFinished(vehicleId)
                        activity?.finish()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        binding.progressCircular.visibility = if (displayProgress) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun addOdometerReading(context: Context) {
        binding.buttonValidateReference.setOnClickListener {
            val isEditTextDistanceBlank = binding.textViewVehicleDistanceField.editableText.toString().isBlank()
            viewModel.mileageDistance =
                if (isEditTextDistanceBlank) 0.0 else binding.textViewVehicleDistanceField.editableText.toString().toDouble()
            if (viewModel.showMileageDistanceErrorMessage() || isEditTextDistanceBlank) {
                binding.textInputLayoutDistance.apply {
                    isErrorEnabled = true
                    error = getString(R.string.dk_vehicle_odometer_history_error)
                    typeface = DriveKitUI.primaryFont(context)
                }
            } else {
                updateProgressVisibility(true)
                viewModel.addOdometerHistory()
            }
        }
    }
}
