package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerHistoryDetailActivity
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerVehicleDetailActivity

enum class OdometerAction(
    private val descriptionResId: String) : OdometerActionItem {
    SHOW("dk_vehicle_show"),
    ADD("dk_vehicle_odometer_add_reference");

    override fun getTitle(context: Context) = DKResource.convertToString(context, descriptionResId)

    override fun onItemClicked(
        activity: Activity,
        vehicleId: String,
        parentFragment: Fragment?) {
        when (this) {
            SHOW -> OdometerVehicleDetailActivity.launchActivity(activity, vehicleId, parentFragment)
            ADD  -> OdometerHistoryDetailActivity.launchActivity(activity, vehicleId, -1, parentFragment)
        }
    }
}

interface OdometerActionItem {
    fun getTitle(context: Context): String
    fun onItemClicked(activity: Activity, vehicleId: String, parentFragment: Fragment?)
}