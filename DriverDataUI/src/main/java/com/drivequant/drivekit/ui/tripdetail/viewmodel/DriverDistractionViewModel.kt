package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.databaseutils.entity.DriverDistraction
import java.io.Serializable

class DriverDistractionViewModel(private val distraction: DriverDistraction) : Serializable {

    fun getScore() = distraction.score

    fun getUnlockNumberEvent() = distraction.nbUnlock.toString()

    fun getUnlockDuration(context: Context): String = DKDataFormatter.formatDuration(context,distraction.durationUnlock)

    fun getUnlockDistance(context: Context) : String = DKDataFormatter.formatDistance(context,distraction.distanceUnlock)
}
