package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerVehicleDetailActivity

enum class OdometerAction(
    private val descriptionResId: String
) : OdometerActionItem {
    SHOW("Show"),
    ADD("Add new reading");

    //TODO DKResource.convertToString(context, descriptionResId)
    override fun getTitle(context: Context) = descriptionResId

    override fun onItemClicked(
        context: Context,
        vehicleId: String) {
        when (this) {
            SHOW -> OdometerVehicleDetailActivity.launchActivity(context, vehicleId)
            ADD  -> Log.e("TEST_vehicleId", "add : ${vehicleId}")
        }
    }
}

interface OdometerActionItem {
    fun getTitle(context: Context): String
    fun onItemClicked(context: Context, vehicleId: String)
}