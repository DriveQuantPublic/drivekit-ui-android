package com.drivequant.drivekit.ui.trips.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.ui.R

internal class AllTripsTransportationModeFilterItem : FilterItem {
    override fun getItemId(): Any? {
        return null
    }

    override fun getImage(context: Context): Drawable? {
        return ContextCompat.getDrawable(context, R.drawable.dk_transportation_all)
    }

    override fun getTitle(context: Context): String {
        return context.getString(R.string.dk_driverdata_default_filter_item)
    }
}
