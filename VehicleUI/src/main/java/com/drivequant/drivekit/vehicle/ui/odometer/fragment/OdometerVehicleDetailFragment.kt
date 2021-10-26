package com.drivequant.drivekit.vehicle.ui.odometer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vehicleId = it.getString("vehicleId_arg")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        vehicleId?.let {
            outState.putString("vehicleId", it)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_odometer_vehicle_detail, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getString("vehicleId"))?.let {
            vehicleId = it
        }
        vehicleId?.let { vehicleId ->
            viewModel = ViewModelProviders.of(
                this,
                OdometerDetailViewModel.OdometerDetailViewModelFactory(vehicleId)
            ).get(OdometerDetailViewModel::class.java)

            val itemViewModel = OdometerItemViewModel(vehicleId)
            mileage_vehicle_item.configureOdometerItem(
                itemViewModel,
                OdometerItemType.ODOMETER,
                this
            )
            distance_estimated_item.configureOdometerItem(
                itemViewModel,
                OdometerItemType.ESTIMATED,
                this
            )
            distance_analyzed_item.configureOdometerItem(
                itemViewModel,
                OdometerItemType.ANALYZED,
                this
            )

            context?.let { context ->
                viewModel.getVehicleDrawable(context)?.let { drawable ->
                    Glide.with(context)
                        .load(drawable)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(drawable)
                        .into(image_item)
                }

                text_view_item_display_name.text = viewModel.getVehicleDisplayName(context)

                button_update_odometer_reading.apply {
                    text = DKResource.convertToString(context, "dk_vehicle_odometer_reference_update")
                    headLine2(DriveKitUI.colors.fontColorOnSecondaryColor())
                    setBackgroundColor(DriveKitUI.colors.secondaryColor())
                    setOnClickListener {
                        OdometerHistoryDetailActivity.launchActivity(context, vehicleId, -1)
                    }
                }

                button_display_odometer_readings.apply {
                    text = DKResource.convertToString(context, "dk_vehicle_odometer_references_link")
                    headLine2(DriveKitUI.colors.secondaryColor())
                    setOnClickListener {
                        OdometerHistoriesListActivity.launchActivity(context, vehicleId)
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(vehicleId: String) =
            OdometerVehicleDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("vehicleId_arg", vehicleId)
                }
            }
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
}