package com.drivekit.demoapp.simulator.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.tripsimulator.DriveKitTripSimulator

class TripSimulatorViewModel {
    private val items = PresetTripType.values()
    private var selectedItemIndex = 0

    fun shouldShowWarningMessage() = DriveKitTripSimulator.isDeveloperModeEnabled() && DriveKitTripSimulator.isMockLocationEnabled()

    fun getSelectedItem() = if (selectedItemIndex >= 0 && selectedItemIndex < items.size) {
        items[selectedItemIndex]
    } else {
        items.first()
    }

    fun selectItem(index: Int) {
        if (index in 0..items.size) return
        selectedItemIndex = index
    }

    fun getTripSimulatorItems(): List<FilterItem> {
        val filterItems = mutableListOf<FilterItem>()
        for (item in items) {
            val presetItem = object : FilterItem {
                override fun getItemId() = items.indexOf(item)

                override fun getImage(context: Context): Drawable? = null

                override fun getTitle(context: Context): String = context.getString(item.getTitle())
            }
            filterItems.add(presetItem)
        }
        return filterItems
    }
}