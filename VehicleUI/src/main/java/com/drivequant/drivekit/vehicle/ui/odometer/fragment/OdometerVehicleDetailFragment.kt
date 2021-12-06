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
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerHistoriesListActivity
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerHistoryDetailActivity
import com.drivequant.drivekit.vehicle.ui.odometer.common.OdometerDrawableListener
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerDetailViewModel
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerItemType
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerItemViewModel
import kotlinx.android.synthetic.main.dk_custom_filter_spinner_item.*
import kotlinx.android.synthetic.main.dk_fragment_odometer_vehicle_detail.*

class OdometerVehicleDetailFragment : Fragment(), OdometerDrawableListener {

    private var vehicleId: String? = null
    private lateinit var viewModel: OdometerDetailViewModel

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
        super.onSaveInstanceState(outState)
        vehicleId?.let {
            outState.putString("vehicleIdTag", it)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_odometer_vehicle_detail, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_vehicles_odometer_vehicles_detail"
            ), javaClass.simpleName
        )
        (savedInstanceState?.getString("vehicleIdTag"))?.let {
            vehicleId = it
        }

        initVehicleOdometerDetail()
    }

    private fun initVehicleOdometerDetail() {
        vehicleId?.let { vehicleId ->
            context?.let { context ->
                viewModel = ViewModelProviders.of(this,
                    OdometerDetailViewModel.OdometerDetailViewModelFactory(vehicleId)).get(OdometerDetailViewModel::class.java)
                initOdometerItems(vehicleId)
                initVehicle(context)
                displayOdometerReadings(context, vehicleId)
                updateOdometerClicked(context, vehicleId)
            }
        }
    }

    private fun displayOdometerReadings(context: Context, vehicleId: String) {
        button_display_odometer_readings.apply {
            visibility = if(viewModel.shouldShowDisplayReadingButton()) View.VISIBLE else View.GONE
            text = DKResource.convertToString(context, "dk_vehicle_odometer_histories_link")
            headLine2(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                activity?.let { activity ->
                    OdometerHistoriesListActivity.launchActivity(activity, vehicleId, this@OdometerVehicleDetailFragment)
                }
            }
        }
    }

    private fun updateOdometerClicked(context: Context, vehicleId: String) {
        button_update_odometer_reading.apply {
            text = DKResource.convertToString(context, "dk_vehicle_odometer_history_update")
            headLine2(DriveKitUI.colors.fontColorOnSecondaryColor())
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                OdometerHistoryDetailActivity.launchActivity(requireActivity(), vehicleId, -1, this@OdometerVehicleDetailFragment)
            }
        }
    }

    private fun initVehicle(context: Context) {
        viewModel.getVehicleDrawable(context)?.let { drawable ->
            Glide.with(context)
                .load(drawable)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(drawable)
                .into(image_item)
        }
        text_view_item_display_name.text = viewModel.getVehicleDisplayName(context)
    }

    private fun initOdometerItems(vehicleId: String) {
        val itemViewModel = OdometerItemViewModel(vehicleId)
        mileage_vehicle_item.configureOdometerItem(
            itemViewModel,
            OdometerItemType.ODOMETER,
            this)

        distance_estimated_item.configureOdometerItem(
            itemViewModel,
            OdometerItemType.ESTIMATED,
            this)

        distance_analyzed_item.configureOdometerItem(
            itemViewModel,
            OdometerItemType.ANALYZED,
            this)
    }

    override fun onDrawableClicked(view: View, odometerItemType: OdometerItemType) {
        val alertDialogData = when (odometerItemType) {
            OdometerItemType.ODOMETER -> Pair(
                "dk_vehicle_odometer_vehicle_title",
                "dk_vehicle_odometer_info_vehicle_distance_text"
            )
            OdometerItemType.ANALYZED -> Pair(
                "dk_vehicle_odometer_info_analysed_distance_title",
                "dk_vehicle_odometer_info_analysed_distance_text"
            )
            OdometerItemType.ESTIMATED -> Pair(
                "dk_vehicle_odometer_info_estimated_distance_title",
                "dk_vehicle_odometer_info_estimated_distance_text"
            )
        }.let {
            Pair(
                DKResource.convertToString(view.context, it.first),
                DKResource.convertToString(view.context, it.second)
            )
        }
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(requireContext())
            .layout(R.layout.template_alert_dialog_layout)
            .positiveButton()
            .show()

        val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView =
            alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
        titleTextView?.text = alertDialogData.first
        descriptionTextView?.text = alertDialogData.second

        titleTextView?.headLine1()
        descriptionTextView?.normalText()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && (
            requestCode == OdometerHistoryDetailActivity.UPDATE_VEHICLE_HISTORY_LIST_REQUEST_CODE ||
            requestCode == OdometerHistoryDetailActivity.UPDATE_VEHICLE_ODOMETER_DETAIL_REQUEST_CODE ||
            requestCode == OdometerHistoriesListActivity.UPDATE_VEHICLE_ODOMETER_DETAIL_REQUEST_CODE)) {
            initVehicleOdometerDetail()
            val intentData = Intent()
            activity?.setResult(Activity.RESULT_OK, intentData)
        }
    }
}