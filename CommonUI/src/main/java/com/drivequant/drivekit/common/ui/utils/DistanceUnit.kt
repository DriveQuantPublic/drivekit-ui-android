package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R

enum class DistanceUnit {
    KM, MILE;

    companion object{
        fun configuredUnit(context: Context) : String {
            return when (DriveKitUI.distanceUnit) {
                MILE -> context.resources.getString(R.string.dk_common_unit_kilometer)
                KM -> context.resources.getString(R.string.dk_common_unit_kilometer)
            }
        }
    }
}