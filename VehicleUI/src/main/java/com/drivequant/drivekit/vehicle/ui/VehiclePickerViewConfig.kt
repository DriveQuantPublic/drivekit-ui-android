package com.drivequant.drivekit.vehicle.ui

import android.content.Context
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.CategoryConfigType
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehicleBrandItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehicleTypeItem
import java.io.Serializable

class VehiclePickerViewConfig(
    context: Context,
    val vehicleTypeItems: List<VehicleTypeItem> = listOf(VehicleTypeItem.CAR),
    val brands: List<VehicleBrandItem> = VehicleTypeItem.CAR.getBrands(context),
    val categoryConfigTypes: CategoryConfigType = CategoryConfigType.BOTH_CONFIG,
    val displayBrandsWithIcons: Boolean = true
) : Serializable