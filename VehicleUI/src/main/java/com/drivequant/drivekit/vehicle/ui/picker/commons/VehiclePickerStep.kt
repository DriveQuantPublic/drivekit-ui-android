package com.drivequant.drivekit.vehicle.ui.picker.commons

import java.lang.IllegalArgumentException


enum class VehiclePickerStep(val value: Int) {
    TYPE(0),
    CATEGORY(1),
    CATEGORY_DESCRIPTION(2),
    BRANDS_ICONS(3),
    BRANDS_FULL(4),
    ENGINE(5),
    MODELS(6),
    YEARS(7),
    VERSIONS(8),
    NAME(9);

    companion object {
        fun getEnumByValue(value: Int): VehiclePickerStep {
            for (x in values()) {
                if (x.value == value) return x
            }
            throw IllegalArgumentException("No value")
        }

        fun getValueByEnum(vehiclePickerStep: VehiclePickerStep): Int {
            var value = -1
            for (x in values()) {
                if (x.value == vehiclePickerStep.value)
                    value = x.value
            }
            return value
        }
    }
}