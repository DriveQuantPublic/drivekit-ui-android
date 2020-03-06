package com.drivequant.drivekit.ui.tripdetail.viewmodel

import com.drivequant.drivekit.databaseutils.entity.EcoDriving
import com.drivequant.drivekit.ui.R
import java.io.Serializable

class EcoDrivingViewModel(private val ecoDriving: EcoDriving) : Serializable{

    fun getScore() = ecoDriving.score

    fun getAccelMessage() : Int {
        return when {
            ecoDriving.scoreAccel < -4 -> R.string.dk_driverdata_low_accel
            ecoDriving.scoreAccel < -2 -> R.string.dk_driverdata_weak_accel
            ecoDriving.scoreAccel < 1 ->  R.string.dk_driverdata_good_accel
            ecoDriving.scoreAccel < 3 ->  R.string.dk_driverdata_strong_accel
            else ->  R.string.dk_driverdata_high_accel
        }
    }

    fun getMaintainMessage() : Int {
        return when {
            ecoDriving.scoreMain < 1.5 -> R.string.dk_driverdata_good_maintain
            ecoDriving.scoreMain < 3.5 -> R.string.dk_driverdata_weak_maintain
            else -> R.string.dk_driverdata_bad_maintain
        }
    }

    fun getDecelMessage() : Int {
        return when {
            ecoDriving.scoreDecel < -4 -> R.string.dk_driverdata_low_decel
            ecoDriving.scoreDecel < -2 -> R.string.dk_driverdata_weak_decel
            ecoDriving.scoreDecel < 1 -> R.string.dk_driverdata_good_decel
            ecoDriving.scoreDecel < 3 -> R.string.dk_driverdata_strong_decel
            else -> R.string.dk_driverdata_high_decel
        }
    }

    fun getGaugeTitle() : Int {
        return R.string.dk_common_ecodriving
    }
}
