package com.drivequant.drivekit.vehicle.ui.odometer.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.DKPrimaryButton
import com.drivequant.drivekit.common.ui.component.DKSecondaryButton
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.injectContent
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.databinding.DkFragmentOdometerVehicleDetailBinding
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerHistoriesListActivity
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerHistoryDetailActivity
import com.drivequant.drivekit.vehicle.ui.odometer.common.OdometerDrawableListener
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerDetailViewModel
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerItemType
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerItemViewModel

class OdometerVehicleDetailFragment : Fragment(), OdometerDrawableListener {

    private var vehicleId: String? = null
    private lateinit var viewModel: OdometerDetailViewModel
    private var _binding: DkFragmentOdometerVehicleDetailBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    companion object {
        fun newInstance(vehicleId: String) =
            OdometerVehicleDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("vehicleIdArg", vehicleId)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vehicleId = it.getString("vehicleIdArg")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        vehicleId?.let {
            outState.putString("vehicleIdTag", it)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DkFragmentOdometerVehicleDetailBinding.inflate(inflater, container, false)
        binding.root.setDKStyle()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_vehicles_odometer_vehicles_detail), javaClass.simpleName)
        savedInstanceState?.getString("vehicleIdTag")?.let {
            vehicleId = it
        }

        initVehicleOdometerDetail()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initVehicleOdometerDetail() {
        vehicleId?.let { vehicleId ->
            context?.let { context ->
                viewModel = ViewModelProvider(this,
                    OdometerDetailViewModel.OdometerDetailViewModelFactory(vehicleId))[OdometerDetailViewModel::class.java]
                initOdometerItems(vehicleId)
                initVehicle(context)
                manageUpdateOdometerButton(vehicleId)
                manageDisplayOdometerReadingsButton(vehicleId)
            }
        }
    }

    private fun manageUpdateOdometerButton(vehicleId: String) {
        binding.buttonUpdateOdometerReading.injectContent {
            DKPrimaryButton(getString(R.string.dk_vehicle_odometer_history_update)) {
                OdometerHistoryDetailActivity.launchActivity(requireActivity(), vehicleId, -1, this@OdometerVehicleDetailFragment)
            }
        }
    }

    private fun manageDisplayOdometerReadingsButton(vehicleId: String) {
        binding.buttonDisplayOdometerReadings.apply {
            visibility = if (viewModel.shouldShowDisplayReadingButton()) View.VISIBLE else View.GONE
            setContent {
                DKSecondaryButton(getString(R.string.dk_vehicle_odometer_histories_link)) {
                    activity?.let { activity ->
                        OdometerHistoriesListActivity.launchActivity(activity, vehicleId, this@OdometerVehicleDetailFragment)
                    }
                }
            }
        }
    }

    private fun initVehicle(context: Context) {
        binding.spinnerItem.imageItem.setImageResource(viewModel.getVehicleDrawableRes())
        binding.spinnerItem.textViewItemDisplayName.text = viewModel.getVehicleDisplayName(context)
    }

    private fun initOdometerItems(vehicleId: String) {
        val itemViewModel = OdometerItemViewModel(vehicleId)
        binding.mileageVehicleItem.configureOdometerItem(
            itemViewModel,
            OdometerItemType.ODOMETER,
            this)

        binding.distanceEstimatedItem.configureOdometerItem(
            itemViewModel,
            OdometerItemType.ESTIMATED,
            this)

        binding.distanceAnalyzedItem.configureOdometerItem(
            itemViewModel,
            OdometerItemType.ANALYZED,
            this)
    }

    override fun onDrawableClicked(view: View, odometerItemType: OdometerItemType) {
        val alertDialogData = when (odometerItemType) {
            OdometerItemType.ODOMETER -> Pair(
                R.string.dk_vehicle_odometer_vehicle_title,
                R.string.dk_vehicle_odometer_info_vehicle_distance_text
            )
            OdometerItemType.ANALYZED -> Pair(
                R.string.dk_vehicle_odometer_info_analysed_distance_title,
                R.string.dk_vehicle_odometer_info_analysed_distance_text
            )
            OdometerItemType.ESTIMATED -> Pair(
                R.string.dk_vehicle_odometer_info_estimated_distance_title,
                R.string.dk_vehicle_odometer_info_estimated_distance_text
            )
        }.let {
            Pair(
                getString(it.first),
                getString(it.second)
            )
        }
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(requireContext())
            .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
            .positiveButton()
            .show()

        val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView =
            alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
        titleTextView?.text = alertDialogData.first
        descriptionTextView?.text = alertDialogData.second
    }


    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && (
            requestCode == OdometerHistoryDetailActivity.UPDATE_VEHICLE_HISTORY_LIST_REQUEST_CODE ||
            requestCode == OdometerHistoryDetailActivity.UPDATE_VEHICLE_ODOMETER_DETAIL_REQUEST_CODE)) {
            initVehicleOdometerDetail()
            val intentData = Intent()
            activity?.setResult(Activity.RESULT_OK, intentData)
        }
    }
}
