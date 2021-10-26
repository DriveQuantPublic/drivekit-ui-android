package com.drivequant.drivekit.vehicle.ui.odometer.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import kotlinx.android.synthetic.main.dk_custom_filter_spinner_item.*


class OdometerHistoryDetailFragment : Fragment() {

    private var vehicleId: String? = null
    private var historyId: Int = -1

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
        if (historyId != -1) {
            outState.putInt("historyIdTag", historyId)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.dk_fragment_odometer_history_detail, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vehicleId?.let { vehicleId ->
            context?.let { context ->
                text_view_item_display_name.text =
                    DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne()
                        .executeOne()?.let {
                            VehicleUtils().buildFormattedName(context, it)
                        }

                VehicleUtils().getVehicleDrawable(context, vehicleId)?.let { drawable ->
                    Glide.with(context)
                        .load(drawable)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(drawable)
                        .into(image_item)
                }
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
