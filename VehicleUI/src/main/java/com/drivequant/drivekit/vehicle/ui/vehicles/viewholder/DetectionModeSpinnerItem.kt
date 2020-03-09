package com.drivequant.drivekit.vehicle.ui.vehicles.viewholder

import android.content.Context
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.DetectionModeType

data class DetectionModeSpinnerItem(
    private var context: Context,
    var detectionModeType: DetectionModeType
){
    override fun toString(): String {
        return detectionModeType.getTitle(context)
    }
}