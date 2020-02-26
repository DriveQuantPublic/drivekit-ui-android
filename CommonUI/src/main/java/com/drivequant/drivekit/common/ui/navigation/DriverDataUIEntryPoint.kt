package com.drivequant.drivekit.common.ui.navigation

import android.content.Context
import android.support.v4.app.Fragment

interface DriverDataUIEntryPoint {
    fun startTripListActiviyt(context: Context)
    fun startTripDetailActivity(context: Context, tripId: String)
    fun createTripListFragment(): Fragment
    fun createTripDetailFragment(tripId: String): Fragment
}