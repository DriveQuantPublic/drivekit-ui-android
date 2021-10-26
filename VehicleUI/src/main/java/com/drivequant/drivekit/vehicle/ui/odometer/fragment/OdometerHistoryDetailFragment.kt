package com.drivequant.drivekit.vehicle.ui.odometer.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.dk_custom_filter_spinner_item.*
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
            viewModel = ViewModelProviders.of(
                this,
                OdometerHistoryDetailViewModel.OdometerHistoryDetailViewModelFactory(
                    vehicleId,
                    historyId)).get(OdometerHistoryDetailViewModel::class.java)

            context?.let { context ->
                configureVehicle(context, vehicleId)
                edit_text_distance.setEditTextTitle(viewModel.getHistoryDistance(context))
                edit_text_date.setEditTextTitle(viewModel.getHistoryUpdateDate())
            }
        }

        button_validate_reference.apply {
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            text = DKResource.convertToString(context, "dk_common_validate")
            headLine2(DriveKitUI.colors.fontColorOnSecondaryColor())
            visibility = if (viewModel.isAddMode() || viewModel.canEditHistory()) View.VISIBLE else View.GONE
            setOnClickListener {
                //TODO validate
            }
        }

        button_delete_reference.apply {
            normalText(DriveKitUI.colors.secondaryColor())
            text = DKResource.convertToString(context, "dk_common_delete")
            visibility = if (viewModel.canDeleteHistory()) View.VISIBLE else View.GONE
            setOnClickListener {
                //TODO delete
            }
        }

        button_cancel_action.apply {
            normalText(DriveKitUI.colors.secondaryColor())
            text = DKResource.convertToString(context, "dk_common_cancel")
            visibility = if (viewModel.isAddMode() || viewModel.canEditHistory()) View.VISIBLE else View.GONE
            setOnClickListener {
                //TODO cancel
            }
        }

        text_view_history_detail_title.apply {
            text = DKResource.convertToString(context, "dk_vehicle_odometer_odometer_history_detail_title")
            normalText(DriveKitUI.colors.secondaryColor())
        }
    }

    private fun configureVehicle(context: Context, vehicleId: String) {
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
