package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerHistoryDetailActivity
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerVehicleDetailActivity

enum class OdometerAction(
    private val descriptionResId: String) : OdometerActionItem {
    SHOW("dk_vehicle_show"),
    ADD("dk_vehicle_odometer_add_reference");

    override fun getTitle(context: Context) = DKResource.convertToString(context, descriptionResId)

    override fun onItemClicked(
        context: Context,
        vehicleId: String) {
        when (this) {
            SHOW -> OdometerVehicleDetailActivity.launchActivity(context, vehicleId)
            ADD  -> OdometerHistoryDetailActivity.launchActivity(context, vehicleId, -1)
        }
    }
}

interface OdometerActionItem {
    fun getTitle(context: Context): String
    fun onItemClicked(context: Context, vehicleId: String)
}