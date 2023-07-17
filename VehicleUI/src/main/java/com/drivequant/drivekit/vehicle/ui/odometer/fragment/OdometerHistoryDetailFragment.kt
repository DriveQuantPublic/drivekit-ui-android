package com.drivequant.drivekit.vehicle.ui.odometer.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerHistoryDetailViewModel
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dk_custom_filter_spinner_item_no_padding.image_item
import kotlinx.android.synthetic.main.dk_custom_filter_spinner_item_no_padding.text_view_item_display_name
import kotlinx.android.synthetic.main.dk_fragment_odometer_history_detail.button_cancel_action
import kotlinx.android.synthetic.main.dk_fragment_odometer_history_detail.button_delete_reference
import kotlinx.android.synthetic.main.dk_fragment_odometer_history_detail.button_validate_reference
import kotlinx.android.synthetic.main.dk_fragment_odometer_history_detail.edit_text_date
import kotlinx.android.synthetic.main.dk_fragment_odometer_history_detail.edit_text_distance
import kotlinx.android.synthetic.main.dk_fragment_odometer_history_detail.progress_circular
import kotlinx.android.synthetic.main.dk_fragment_odometer_history_detail.text_view_history_detail_title
import kotlinx.android.synthetic.main.dk_fragment_odometer_history_detail.vehicle_item


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
    ): View =
        inflater.inflate(R.layout.dk_fragment_odometer_history_detail, container, false).setDKStyle(
            Color.WHITE)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (savedInstanceState?.getString("vehicleIdTag"))?.let {
            vehicleId = it
        }

        (savedInstanceState?.getInt("historyIdTag"))?.let {
            historyId = it
        }

        vehicleId?.let { vehicleId ->
            context?.let { context ->
                viewModel = ViewModelProvider(this,
                    OdometerHistoryDetailViewModel.OdometerHistoryDetailViewModelFactory(vehicleId,
                        historyId))[OdometerHistoryDetailViewModel::class.java]
                if (!viewModel.canEditOrAddHistory()) {
                    "dk_tag_vehicles_odometer_histories_detail"
                } else {
                    if (historyId == -1) {
                        "dk_tag_vehicles_odometer_histories_add"
                    } else {
                        "dk_tag_vehicles_odometer_histories_edit"
                    }
                }.let {
                    DriveKitUI.analyticsListener?.trackScreen(
                        DKResource.convertToString(
                            requireContext(),
                            it
                        ), javaClass.simpleName
                    )
                }
                initVehicle(context, vehicleId)
                initMileageRecord(context)
                onValidateButtonClicked(context)
                onDeleteOdometerHistory(context)
                onDistanceClicked(context)
                onCancelButtonClicked(context)
                vehicle_item.setBackgroundColor(DriveKitUI.colors.neutralColor())
                viewModel.odometerActionObserver.observe(viewLifecycleOwner) {
                    updateProgressVisibility(false)
                    Toast.makeText(
                        context,
                        DKResource.convertToString(context, it.first),
                        Toast.LENGTH_LONG
                    ).show()
                    if (it.second) {
                        val intentData = Intent()
                        activity?.let { activity ->
                            activity.apply {
                                setResult(Activity.RESULT_OK, intentData)
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initVehicle(context: Context, vehicleId: String) {
        text_view_item_display_name.apply {
            smallText(ContextCompat.getColor(context, R.color.dkGrayColor))
            text = viewModel.getVehicleFormattedName(context)
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
        edit_text_distance.apply {
            if (viewModel.canEditOrAddHistory()) {
                setEditTextTitle(viewModel.getHistoryDistance(context), DriveKitUI.colors.complementaryFontColor())
            } else {
                setEditTextTitle(viewModel.getHistoryDistance(context))
            }
        }
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
                if (viewModel.canEditOrAddHistory()) View.VISIBLE else View.GONE
            setOnClickListener {
                if (viewModel.showMileageDistanceErrorMessage()) {
                    Toast.makeText(
                        context,
                        DKResource.convertToString(context, "dk_vehicle_odometer_history_error"),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    updateProgressVisibility(true)
                    if (viewModel.canEditHistory()) {
                        viewModel.updateOdometerHistory()
                    } else {
                        viewModel.addOdometerHistory()
                    }
                }
            }
        }
    }

    private fun onCancelButtonClicked(context: Context) {
        button_cancel_action.apply {
            normalText(DriveKitUI.colors.secondaryColor())
            text = DKResource.convertToString(context, "dk_common_cancel")
            visibility = if (viewModel.canEditOrAddHistory()) View.VISIBLE else View.GONE
            setOnClickListener {
                activity?.let {
                    val intentData = Intent()
                    it.setResult(Activity.RESULT_CANCELED, intentData)
                    it.finish()
                }
            }
        }
    }

    private fun onDistanceClicked(context: Context) {
        edit_text_distance.apply {
            setOnClickListener {
                if (viewModel.canEditOrAddHistory()) {
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
                        if (viewModel.canAddHistory()) {
                            this.hint = DKResource.convertToString(
                                context,
                                "dk_vehicle_odometer_mileage_kilometer"
                            )
                            this.typeface = DriveKitUI.primaryFont(context)
                        } else {
                            this.setText(viewModel.getFormattedMileageDistance(context, false))
                        }
                    }
                    alertDialog.apply {
                        setCancelable(true)
                        setButton(
                            DialogInterface.BUTTON_POSITIVE,
                            DKResource.convertToString(context, "dk_common_validate")) { dialog, _ ->
                            viewModel.mileageDistance = if (editText.editableText.toString().isBlank()) 0.0 else editText.editableText.toString().toDouble()
                            this@OdometerHistoryDetailFragment.edit_text_distance.setEditTextTitle(
                                viewModel.getFormattedMileageDistance(context))
                            dialog.dismiss()
                        }
                        setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }
                        show()
                        getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                            typeface = DriveKitUI.primaryFont(context)
                        }
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
