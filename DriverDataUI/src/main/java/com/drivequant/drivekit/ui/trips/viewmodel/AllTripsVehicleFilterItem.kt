package com.drivequant.drivekit.ui.trips.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.ui.R

internal class AllTripsVehicleFilterItem : FilterItem {
    override fun getItemId(): Any? {
        return null
    }

    override fun getImage(context: Context): Drawable? = ContextCompat.getDrawable(context, com.drivequant.drivekit.common.ui.R.drawable.dk_my_trips)

    override fun getTitle(context: Context): String = context.getString(R.string.dk_driverdata_default_filter_item)
}
