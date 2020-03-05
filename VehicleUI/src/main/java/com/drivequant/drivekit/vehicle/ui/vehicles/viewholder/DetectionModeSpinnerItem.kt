package com.drivequant.drivekit.vehicle.ui.vehicles.viewholder

import android.content.Context
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.DetectionModeType

data class DetectionModeSpinnerItem(
    private var title: String,
    private var detectionModeType: DetectionModeType
){
    override fun toString(): String {
        return title
    }
}


fun buildItem(context: Context, detectionModeType: DetectionModeType): DetectionModeSpinnerItem {
    return DetectionModeSpinnerItem(context.getString(detectionModeType.title), detectionModeType)
}