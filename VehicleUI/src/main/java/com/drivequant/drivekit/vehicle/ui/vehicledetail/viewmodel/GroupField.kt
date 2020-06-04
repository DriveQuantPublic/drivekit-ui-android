package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.support.annotation.Keep
import com.drivequant.drivekit.databaseutils.entity.Vehicle
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
            VehicleType.getVehicleType(vehicle.typeIndex)?.let { vehicleType ->
                when (vehicleType){
                    VehicleType.CAR -> groupFields.addAll(values())
                    VehicleType.TRUCK -> groupFields.addAll(listOf(GENERAL, CHARACTERISTICS, BEACON, BLUETOOTH))
                }
            }
            return groupFields
        }
    }

    fun getFields(vehicle: Vehicle): List<Field> {
        val fields = when (this){
            GENERAL -> addFieldsIfDisplayable(GeneralField.values().asList(), vehicle)
            ENGINE -> addFieldsIfDisplayable(EngineField.values().asList(), vehicle)
            CHARACTERISTICS -> addFieldsIfDisplayable(CharacteristicField.getFields(vehicle), vehicle)
            BEACON -> addFieldsIfDisplayable(BeaconField.values().asList(), vehicle)
            BLUETOOTH -> addFieldsIfDisplayable(BluetoothField.values().asList(), vehicle)
        }
        getCustomFields(vehicle)?.let {
            fields.addAll(it)
        }
        return fields
    }

    private fun addFieldsIfDisplayable(fields: List<Field>, vehicle: Vehicle) : MutableList<Field> {
        val filteredFields = mutableListOf<Field>()
        for (item in fields){
            if (item.alwaysDisplayable(vehicle)){
                filteredFields.add(item)
            }
        }
        return filteredFields
    }

    private fun getCustomFields(vehicle: Vehicle): List<Field>?{
        return DriveKitVehicleUI.customFields[this]?.let {
            val filtered = mutableListOf<Field>()
            for (field in it){
                if (field.alwaysDisplayable(vehicle)){
                    filtered.add(field)
                }
            }
            filtered
        }?: run {
            null
        }
    }

    fun isDisplayable(vehicle: Vehicle): Boolean {
        return getFields(vehicle).isNotEmpty()
    }
}