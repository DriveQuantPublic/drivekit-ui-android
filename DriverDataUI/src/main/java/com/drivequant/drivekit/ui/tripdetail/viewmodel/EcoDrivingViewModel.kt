package com.drivequant.drivekit.ui.tripdetail.viewmodel

import com.drivequant.drivekit.databaseutils.entity.EcoDriving
import com.drivequant.drivekit.ui.R
import java.io.Serializable

class EcoDrivingViewModel(private val ecoDriving: EcoDriving) : Serializable{

    fun getScore() = ecoDriving.score

    fun getAccelMessage() : Int {
        return when {
            ecoDriving.scoreAccel < -4 -> R.string.dk_common_ecodriving_accel_low
            ecoDriving.scoreAccel < -2 -> R.string.dk_common_ecodriving_accel_weak
            ecoDriving.scoreAccel < 1 ->  R.string.dk_common_ecodriving_accel_good
            ecoDriving.scoreAccel < 3 ->  R.string.dk_common_ecodriving_accel_strong
            else -> R.string.dk_common_ecodriving_accel_high
        }
    }

    fun getMaintainMessage() : Int {
        return when {
            ecoDriving.scoreMain < 1.5 -> R.string.dk_common_ecodriving_speed_good_maintain
            ecoDriving.scoreMain < 3.5 -> R.string.dk_common_ecodriving_speed_weak_maintain
            else -> R.string.dk_common_ecodriving_speed_bad_maintain
        }
    }

    fun getDecelMessage() : Int {
        return when {
            ecoDriving.scoreDecel < -4 -> R.string.dk_common_ecodriving_decel_low
            ecoDriving.scoreDecel < -2 -> R.string.dk_common_ecodriving_decel_weak
            ecoDriving.scoreDecel < 1 -> R.string.dk_common_ecodriving_decel_good
            ecoDriving.scoreDecel < 3 -> R.string.dk_common_ecodriving_decel_strong
            else -> R.string.dk_common_ecodriving_decel_high
        }
    }

    fun getGaugeTitle() : Int {
        return R.string.dk_common_ecodriving
    }
}
