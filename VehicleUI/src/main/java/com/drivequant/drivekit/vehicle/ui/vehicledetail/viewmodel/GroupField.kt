package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.support.annotation.Keep
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.enums.VehicleCategory
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI

enum class GroupField{
    GENERAL,
    ENGINE,
    CHARACTERISTICS,
    BEACON,
    BLUETOOTH;

    @Keep
    companion object {
        fun getGroupFields(vehicle: Vehicle): List<GroupField> {
            val groupFields = mutableListOf<GroupField>()
            VehicleCategory.getVehicleType(vehicle.typeIndex)?.let { vehicleType ->
                when (vehicleType){
                    VehicleType.CAR -> groupFields.addAll(values())
                    VehicleType.TRUCK -> groupFields.addAll(listOf(GENERAL, CHARACTERISTICS, BEACON, BLUETOOTH))
                }
            }
            return groupFields
        }
    }

    fun getFields(vehicle: Vehicle): List<Field> {
        val fields = mutableListOf<Field>()
        when (this){
            GENERAL -> {
                for (item in GeneralField.values()){
                    if (item.alwaysDisplayable(vehicle)){
                        fields.add((item))
                    }
                }
            }

            ENGINE -> {
                fields.addAll(EngineField.values().toList())
            }

            CHARACTERISTICS -> {
                fields.addAll(CharacteristicField.values().toList())
            }

            BEACON -> {
                for (item in BeaconField.values()){
                    if (item.alwaysDisplayable(vehicle)){
                        fields.add((item))
                    }
                }
            }

            BLUETOOTH -> {
                for (item in BluetoothField.values()){
                    if (item.alwaysDisplayable(vehicle)){
                        fields.add((item))
                    }
                }
            }
        }
        getCustomFields()?.let {
            fields.addAll(it)
        }
        return fields
    }

    private fun getCustomFields(): List<Field>?{
        return DriveKitVehicleUI.customFields[this]?.let {
            it
        }?: run {
            null
        }
    }

    fun isDisplayable(vehicle: Vehicle): Boolean {
        return getFields(vehicle).isNotEmpty()
    }
}