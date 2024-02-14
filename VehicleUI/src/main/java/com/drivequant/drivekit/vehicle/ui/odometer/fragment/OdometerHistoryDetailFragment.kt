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
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.databinding.DkFragmentOdometerHistoryDetailBinding
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerHistoryDetailViewModel
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import com.google.android.material.textfield.TextInputEditText

class OdometerHistoryDetailFragment : Fragment() {

    private var vehicleId: String? = null
    private var historyId: Int = -1
    private lateinit var viewModel: OdometerHistoryDetailViewModel
    private var _binding: DkFragmentOdometerHistoryDetailBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

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
    ): View {
        _binding = DkFragmentOdometerHistoryDetailBinding.inflate(inflater, container, false)
        binding.root.setDKStyle(Color.WHITE)
        return binding.root
    }

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
                    R.string.dk_tag_vehicles_odometer_histories_detail
                } else {
                    if (historyId == -1) {
                        R.string.dk_tag_vehicles_odometer_histories_add
                    } else {
                        R.string.dk_tag_vehicles_odometer_histories_edit
                    }
                }.let {
                    DriveKitUI.analyticsListener?.trackScreen(getString(it), javaClass.simpleName)
                }
                initVehicle(context, vehicleId)
                initMileageRecord(context)
                onValidateButtonClicked(context)
                onDeleteOdometerHistory(context)
                onDistanceClicked(context)
                onCancelButtonClicked()
                binding.vehicleItem.setBackgroundColor(DriveKitUI.colors.neutralColor())
                viewModel.odometerActionObserver.observe(viewLifecycleOwner) {
                    updateProgressVisibility(false)
                    Toast.makeText(context, it.first, Toast.LENGTH_LONG).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initVehicle(context: Context, vehicleId: String) {
        binding.spinnerItem.textViewItemDisplayName.apply {
            smallText(ContextCompat.getColor(context, com.drivequant.drivekit.common.ui.R.color.dkGrayColor))
            text = viewModel.getVehicleFormattedName(context)
        }
        VehicleUtils().getVehicleDrawable(context, vehicleId)?.let { drawable ->
            Glide.with(context)
                .load(drawable)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(drawable)
                .into(binding.spinnerItem.imageItem)
        }
    }

    private fun initMileageRecord(context: Context) {
        binding.editTextDistance.apply {
            if (viewModel.canEditOrAddHistory()) {
                setEditTextTitle(viewModel.getHistoryDistance(context), DriveKitUI.colors.complementaryFontColor())
            } else {
                setEditTextTitle(viewModel.getHistoryDistance(context))
            }
        }
        binding.editTextDate.setEditTextTitle(viewModel.getHistoryUpdateDate())
        binding.textViewHistoryDetailTitle.apply {
            setText(R.string.dk_vehicle_odometer_odometer_history_detail_title)
            normalText(DriveKitUI.colors.secondaryColor())
        }
    }

    private fun onValidateButtonClicked(context: Context) {
        binding.buttonValidateReference.apply {
            visibility = if (viewModel.canEditOrAddHistory()) View.VISIBLE else View.GONE
            setOnClickListener {
                if (viewModel.showMileageDistanceErrorMessage()) {
                    Toast.makeText(
                        context,
                        R.string.dk_vehicle_odometer_history_error,
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

    private fun onCancelButtonClicked() {
        binding.buttonCancelAction.apply {
            normalText(DriveKitUI.colors.secondaryColor())
            setText(com.drivequant.drivekit.common.ui.R.string.dk_common_cancel)
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
        binding.editTextDistance.apply {
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
                        setText(R.string.dk_vehicle_odometer_mileage_kilometer)
                        normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
                        setBackgroundColor(DriveKitUI.colors.primaryColor())
                    }
                    editText.apply {
                        inputType = InputType.TYPE_CLASS_NUMBER
                        if (viewModel.canAddHistory()) {
                            this.hint = getString(R.string.dk_vehicle_odometer_mileage_kilometer)
                            this.typeface = DriveKitUI.primaryFont(context)
                        } else {
                            this.setText(viewModel.getFormattedMileageDistance(context, false))
                        }
                    }
                    alertDialog.apply {
                        setCancelable(true)
                        setButton(
                            DialogInterface.BUTTON_POSITIVE,
                            getString(com.drivequant.drivekit.common.ui.R.string.dk_common_validate)
                        ) { dialog, _ ->
                            viewModel.mileageDistance = if (editText.editableText.toString().isBlank()) 0.0 else editText.editableText.toString().toDouble()
                            this@OdometerHistoryDetailFragment.binding.editTextDistance.setEditTextTitle(viewModel.getFormattedMileageDistance(context))
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
        binding.buttonDeleteReference.apply {
            normalText(DriveKitUI.colors.secondaryColor())
            setText(com.drivequant.drivekit.common.ui.R.string.dk_common_delete)
            visibility = if (viewModel.canDeleteHistory()) View.VISIBLE else View.GONE
            setOnClickListener {
                updateProgressVisibility(true)
                viewModel.deleteOdometerHistory()
            }
        }
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        binding.progressCircular.visibility = if (displayProgress) {
            View.VISIBLE
        } else {
            View.GONE
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
