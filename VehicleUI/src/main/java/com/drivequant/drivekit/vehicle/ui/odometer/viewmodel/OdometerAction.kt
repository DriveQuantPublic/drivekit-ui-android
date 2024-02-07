package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.app.Activity
import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerHistoryDetailActivity
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerVehicleDetailActivity

internal enum class OdometerAction(@StringRes private val descriptionResId: Int) : OdometerActionItem {
    SHOW(R.string.dk_vehicle_show),
    ADD(R.string.dk_vehicle_odometer_add_history);

    override fun getTitle(context: Context) = context.getString(descriptionResId)

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

internal interface OdometerActionItem {
    fun getTitle(context: Context): String
    fun onItemClicked(activity: Activity, vehicleId: String, parentFragment: Fragment?)
}
