package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.getEngineTypeName

enum class EngineField : Field {
    MOTOR,
    CONSUMPTION;

    override fun getTitle(context: Context, vehicle: Vehicle): String? {
        return when (this) {
            MOTOR -> R.string.dk_motor
            CONSUMPTION -> R.string.dk_consumption
        }.let { context.getString(it) }
    }

    override fun getValue(context: Context, vehicle: Vehicle): String? {
        return when (this) {
            MOTOR -> vehicle.getEngineTypeName(context)
            CONSUMPTION -> DKDataFormatter.formatConsumption(context, vehicle.consumption).convertToString()
        }
    }

    override fun alwaysDisplayable(vehicle: Vehicle): Boolean {
        return true
    }

    override fun getErrorDescription(context: Context, value: String, vehicle: Vehicle): String? {
        return null
    }

    override fun onFieldUpdated(context: Context, value: String, vehicle: Vehicle, listener: FieldUpdatedListener) {
        // do nothing
    }
}
