package com.drivequant.drivekit.ui.tripdetail.viewmodel

import com.drivequant.drivekit.databaseutils.entity.EcoDriving
import java.io.Serializable

class EcoDrivingViewModel(private val ecoDriving: EcoDriving) : Serializable{

    fun getScore() = ecoDriving.score

    fun getAccelMessage(): Int {
        return when {
            ecoDriving.scoreAccel < -4 -> com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving_accel_low
            ecoDriving.scoreAccel < -2 -> com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving_accel_weak
            ecoDriving.scoreAccel < 1 ->  com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving_accel_good
            ecoDriving.scoreAccel < 3 ->  com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving_accel_strong
            else -> com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving_accel_high
        }
    }

    fun getMaintainMessage(): Int {
        return when {
            ecoDriving.scoreMain < 1.5 -> com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving_speed_good_maintain
            ecoDriving.scoreMain < 3.5 -> com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving_speed_weak_maintain
            else -> com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving_speed_bad_maintain
        }
    }

    fun getDecelMessage(): Int {
        return when {
            ecoDriving.scoreDecel < -4 -> com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving_decel_low
            ecoDriving.scoreDecel < -2 -> com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving_decel_weak
            ecoDriving.scoreDecel < 1 -> com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving_decel_good
            ecoDriving.scoreDecel < 3 -> com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving_decel_strong
            else -> com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving_decel_high
        }
    }

    fun getGaugeTitle(): Int {
        return com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving
    }
}
