package com.drivequant.drivekit.vehicle.ui

import android.content.Context
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.CategoryType
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehicleBrandItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehicleType
import java.io.Serializable

class VehiclePickerViewConfig(
    context: Context,
    val vehicleTypes: List<VehicleType> = listOf(VehicleType.CAR),
    val brands: List<VehicleBrandItem> = VehicleType.CAR.getBrands(context),
    val categoryTypes: CategoryType = CategoryType.BOTH_CONFIG,
    val displayBrandsWithIcons: Boolean = true
) : Serializable