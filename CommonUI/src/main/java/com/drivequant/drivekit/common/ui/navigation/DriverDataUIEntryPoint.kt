package com.drivequant.drivekit.common.ui.navigation

import android.content.Context
import androidx.fragment.app.Fragment

interface DriverDataUIEntryPoint {
    fun startTripListActivity(context: Context)
    fun startTripDetailActivity(context: Context, tripId: String)
    fun createTripListFragment(): Fragment
    fun createTripDetailFragment(tripId: String): Fragment
}