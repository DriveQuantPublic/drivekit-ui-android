package com.drivequant.drivekit.vehicle.ui.odometer.fragment

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerHistoryDetailViewModel
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dk_custom_filter_spinner_item_no_padding.*
import kotlinx.android.synthetic.main.dk_fragment_odometer_history_detail.*


class OdometerHistoryDetailFragment : Fragment() {

    private var vehicleId: String? = null
    private var historyId: Int = -1
    private lateinit var viewModel: OdometerHistoryDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vehicleId = it.getString("vehicleIdTag")
            historyId = it.getInt("historyIdTag")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        vehicleId?.let {
            outState.putString("vehicleIdTag", it)
        }
        outState.putInt("historyIdTag", historyId)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.dk_fragment_odometer_history_detail, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getString("vehicleIdTag"))?.let {
            vehicleId = it
        }

        (savedInstanceState?.getInt("historyIdTag"))?.let {
            historyId = it
        }

        vehicleId?.let { vehicleId ->
            context?.let { context ->
            viewModel = ViewModelProviders.of(this,
                OdometerHistoryDetailViewModel.OdometerHistoryDetailViewModelFactory(vehicleId,
                    historyId)).get(OdometerHistoryDetailViewModel::class.java)

                initVehicle(context, vehicleId)
                initMileageRecord(context)
                onValidateButtonClicked(context)
                onDeleteOdometerHistory(context)
                onDistanceClicked(context)
                onCancelButtonClicked(context)

                viewModel.odometerActionObserver.observe(this, {
                    updateProgressVisibility(false)
                    Toast.makeText(context, DKResource.convertToString(context, it.first), Toast.LENGTH_LONG).show()
                    if (it.second) {
                        activity?.finish()
                    }
                })
            }
        }
    }

    private fun initVehicle(context: Context, vehicleId: String) {
        text_view_item_display_name.apply {
            smallText(Color.parseColor("#616161"))
            text = DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne()
                .executeOne()?.let {
                    VehicleUtils().buildFormattedName(context, it)
                }
        }
        VehicleUtils().getVehicleDrawable(context, vehicleId)?.let { drawable ->
            Glide.with(context)
                .load(drawable)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(drawable)
                .into(image_item)
        }
    }

    private fun initMileageRecord(context: Context) {
        edit_text_distance.setEditTextTitle(viewModel.getHistoryDistance(context))
        edit_text_date.setEditTextTitle(viewModel.getHistoryUpdateDate())
        text_view_history_detail_title.apply {
            text = DKResource.convertToString(context, "dk_vehicle_odometer_odometer_history_detail_title")
            normalText(DriveKitUI.colors.secondaryColor())
        }
    }

    private fun onValidateButtonClicked(context: Context) {
        button_validate_reference.apply {
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            text = DKResource.convertToString(context, "dk_common_validate")
            headLine2(DriveKitUI.colors.fontColorOnSecondaryColor())
            visibility =
                if (viewModel.isAddMode() || viewModel.canEditHistory()) View.VISIBLE else View.GONE
            setOnClickListener {
                updateProgressVisibility(true)
                if (viewModel.canEditHistory()) {
                    viewModel.updateOdometerHistory()
                } else {
                    viewModel.addOdometerHistory()
                }
            }
        }
    }

    private fun onCancelButtonClicked(context: Context) {
        button_cancel_action.apply {
            normalText(DriveKitUI.colors.secondaryColor())
            text = DKResource.convertToString(context, "dk_common_cancel")
            visibility = if (viewModel.isAddMode() || viewModel.canEditHistory()) View.VISIBLE else View.GONE
            setOnClickListener {
                activity?.finish()
            }
        }
    }

    private fun onDistanceClicked(context: Context) {
        edit_text_distance.apply {
            setOnClickListener {
                if (viewModel.isAddMode() || viewModel.canEditHistory()) {
                    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val view = inflater.inflate(com.drivequant.drivekit.common.ui.R.layout.dk_alert_dialog_edit_value, null)
                    val builder = androidx.appcompat.app.AlertDialog.Builder(context)
                    builder.setView(view)
                    val alertDialog = builder.create()
                    val titleTextView = view.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_title)
                    val editText = view.findViewById<TextInputEditText>(com.drivequant.drivekit.common.ui.R.id.edit_text_field)

                    titleTextView.apply {
                        text = DKResource.convertToString(context, "dk_vehicle_odometer_mileage_kilometer")
                        normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
                        setBackgroundColor(DriveKitUI.colors.primaryColor())
                    }
                    editText.apply {
                        inputType = InputType.TYPE_CLASS_NUMBER
                        if (viewModel.isAddMode()) {
                            this.hint = DKResource.convertToString(
                                context,
                                "dk_vehicle_odometer_mileage_kilometer"
                            )
                        } else {
                            this.setText(viewModel.getFormattedMileageDistance(context, false))
                        }
                    }
                    alertDialog.apply {
                        setCancelable(true)
                        setButton(
                            DialogInterface.BUTTON_POSITIVE,
                            DKResource.convertToString(context, "dk_common_validate")
                        ) { dialog, _ ->
                            dialog.dismiss()
                            viewModel.mileageDistance = if (editText.editableText.toString().isBlank()) 0.0 else editText.editableText.toString().toDouble()
                            this@OdometerHistoryDetailFragment.edit_text_distance.setEditTextTitle(
                                viewModel.getFormattedMileageDistance(context)
                            )
                        }
                        setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }
                        show()
                    }
                }
            }
        }
    }

    private fun onDeleteOdometerHistory(context: Context) {
        button_delete_reference.apply {
            normalText(DriveKitUI.colors.secondaryColor())
            text = DKResource.convertToString(context, "dk_common_delete")
            visibility = if (viewModel.canDeleteHistory()) View.VISIBLE else View.GONE
            setOnClickListener {
                updateProgressVisibility(true)
                viewModel.deleteOdometerHistory()
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

    companion object {
        fun newInstance(vehicleId: String, historyId: Int) =
            OdometerHistoryDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("vehicleIdTag", vehicleId)
                    putInt("historyIdTag", historyId)
                }
            }
    }
}
