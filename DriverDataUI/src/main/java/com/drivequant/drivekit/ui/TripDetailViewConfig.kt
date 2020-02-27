package com.drivequant.drivekit.ui

import android.content.Context
import android.support.v4.content.ContextCompat
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapItem
import java.io.Serializable

class TripDetailViewConfig(
    context: Context,
    val mapItems: List<MapItem> = listOf(MapItem.SAFETY, MapItem.ECO_DRIVING, MapItem.DISTRACTION, MapItem.INTERACTIVE_MAP, MapItem.SYNTHESIS),
    val displayAdvices: Boolean = true,
    val mapTraceMainColor: Int = ContextCompat.getColor(context, R.color.dkMapTraceMainColor),
    val mapTraceWarningColor: Int = ContextCompat.getColor(context, R.color.dkMapTraceWarningColor),
    val enableDeleteTrip: Boolean = true
) : Serializable
