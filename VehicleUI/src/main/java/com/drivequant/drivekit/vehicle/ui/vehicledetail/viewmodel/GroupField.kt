package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import com.drivequant.drivekit.databaseutils.entity.Vehicle

enum class GroupField{
    GENERAL,
    ENGINE,
    CHARACTERISTICS,
    BEACON,
    BLUETOOTH;

    fun isDisplayable(): Boolean {
        return true
    }

    fun getFields(vehicle: Vehicle): List<Field> {
        // TODO: verify if isDisplayable before
        return when (this){
            GENERAL -> {
                listOf(
                    GeneralField.NAME,
                    GeneralField.CATEGORY
                )
            }

            BEACON -> {
                listOf(
                    BeaconField.UNIQUE_CODE,
                    BeaconField.MAJOR,
                    BeaconField.MINOR
                )
            }

            else -> { listOf() }
        }
    }
}