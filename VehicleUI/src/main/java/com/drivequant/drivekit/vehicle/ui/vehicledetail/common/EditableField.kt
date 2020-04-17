package com.drivequant.drivekit.vehicle.ui.vehicledetail.common

import com.drivequant.drivekit.common.ui.component.EditableText
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.Field

data class EditableField(
    val field: Field,
    val editableText: EditableText
)