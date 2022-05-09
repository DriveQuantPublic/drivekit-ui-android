package com.drivequant.drivekit.vehicle.ui.odometer.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerHistoryDetailViewModel
import kotlinx.android.synthetic.main.dk_fragment_odometer_init.*
import kotlinx.android.synthetic.main.dk_fragment_odometer_init.button_validate_reference


class OdometerInitFragment : Fragment() {

    private var vehicleId: String? = null
    private lateinit var viewModel: OdometerHistoryDetailViewModel

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
    ): View = inflater.inflate(R.layout.dk_fragment_odometer_init, container, false).setDKStyle(
        Color.WHITE)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_vehicles_odometer_init"
            ), javaClass.simpleName
        )
        (savedInstanceState?.getString("vehicleIdTag"))?.let {
            vehicleId = it
        }
        vehicleId?.let { vehicleId ->
            context?.let { context ->
                viewModel = ViewModelProviders.of(this,
                    OdometerHistoryDetailViewModel.OdometerHistoryDetailViewModelFactory(vehicleId,
                        -1)).get(OdometerHistoryDetailViewModel::class.java)
                addOdometerReading(context)

                text_view_odometer_desc.apply {
                    text = DKResource.convertToString(context, "dk_vehicle_odometer_car_desc")
                    normalText()
                }

                text_view_vehicle_distance_field.apply {
                    hint = DKResource.convertToString(context, "dk_vehicle_odometer_enter_mileage")
                    setHintTextColor(DriveKitUI.colors.complementaryFontColor())
                    normalText()
                }
                viewModel.odometerActionObserver.observe(requireActivity(), {
                    updateProgressVisibility(false)
                    Toast.makeText(context, DKResource.convertToString(context, it.first), Toast.LENGTH_LONG).show()
                    if (it.second) {
                        DriveKitVehicleUI.vehiclePickerComplete?.onVehiclePickerFinished(vehicleId)
                        activity?.finish()
                    }
                })
            }
        }
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        progress_circular?.apply {
            visibility = if (displayProgress) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun addOdometerReading(context: Context) {
        button_validate_reference.apply {
            text = DKResource.convertToString(context, "dk_common_validate")
            headLine2(DriveKitUI.colors.fontColorOnSecondaryColor())
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                val isEditTextDistanceBlank = text_view_vehicle_distance_field.editableText.toString().isBlank()
                viewModel.mileageDistance = if (isEditTextDistanceBlank) 0.0 else text_view_vehicle_distance_field.editableText.toString().toDouble()
                if (viewModel.showMileageDistanceErrorMessage() || isEditTextDistanceBlank) {
                    text_input_layout_distance.apply {
                        isErrorEnabled = true
                        error = DKResource.convertToString(context, "dk_vehicle_odometer_history_error")
                    }
                } else {
                    updateProgressVisibility(true)
                    viewModel.addOdometerHistory()
                }
            }
        }
    }
}