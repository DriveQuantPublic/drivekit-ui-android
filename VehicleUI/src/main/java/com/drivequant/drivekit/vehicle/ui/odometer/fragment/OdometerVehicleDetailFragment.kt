package com.drivequant.drivekit.vehicle.ui.odometer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import kotlinx.android.synthetic.main.dk_custom_filter_spinner_item.*
import kotlinx.android.synthetic.main.dk_fragment_odometer_vehicle_detail.*

class OdometerVehicleDetailFragment : Fragment() {

    private var vehicleId: String? = null

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
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dk_fragment_odometer_vehicle_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getString("vehicleId"))?.let {
           vehicleId = it
        }
        vehicleId?.let {
            val vehicleOdometer =
                DriveKitVehicle.odometerQuery().whereEqualTo("vehicleId", it).queryOne()
                    .executeOne()
            vehicleOdometer?.let { odometer ->
                mileage_vehicle_item.apply {
                    setOdometerDistance("${odometer.distance}")
                    //TODO setItemTitle(DKResource.convertToString(requireContext(), "dk_"))
                    setTitle("Vehicle mileage")
                    setDescription("Last updated 24/08/2021")
                }
                distance_analyzed_item.apply {
                    setOdometerDistance("${odometer.analyzedDistance}")
                    setTitle("Distance analyzed by the applicaiton")
                    setDescription("inlcuding 0 this year")
                }
                distance_estimated_item.apply {
                    setOdometerDistance("${odometer.estimatedYearDistance}")
                    setTitle("Estimated annual distance")
                    setDescription("for the year 2021")
                }
            }

            context?.let { context ->
                VehicleUtils().getVehicleDrawable(context, it)?.let { drawable ->
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
        fun newInstance(vehicleId: String) =
            OdometerVehicleDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("vehicleId_arg", vehicleId)
                }
            }
    }
}