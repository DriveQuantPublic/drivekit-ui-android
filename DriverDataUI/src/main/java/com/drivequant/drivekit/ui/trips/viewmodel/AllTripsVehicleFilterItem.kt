package com.drivequant.drivekit.ui.trips.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.common.ui.utils.DKResource

internal class AllTripsVehicleFilterItem : FilterItem {
    override fun getItemId(): Any? {
        return null
    }

    override fun getImage(context: Context): Drawable? {
        return DKResource.convertToDrawable(context, "dk_my_trips")
    }

    override fun getTitle(context: Context): String {
        return DKResource.convertToString(context, "dk_driverdata_default_filter_item")
    }
}