package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import com.drivequant.drivekit.databaseutils.entity.Vehicle

enum class GroupField{
    GENERAL,
    ENGINE,
    CHARACTERISTICS,
    BEACON,
    BLUETOOTH;

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
        return fields
    }

    fun isDisplayable(vehicle: Vehicle): Boolean {
        return getFields(vehicle).isNotEmpty()
    }
}