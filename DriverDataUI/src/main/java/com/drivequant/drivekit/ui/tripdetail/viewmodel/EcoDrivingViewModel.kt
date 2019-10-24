package com.drivequant.drivekit.ui.tripdetail.viewmodel

import com.drivequant.drivekit.databaseutils.entity.EcoDriving
import com.drivequant.drivekit.ui.TripDetailViewConfig
import java.io.Serializable

class EcoDrivingViewModel(private val ecoDriving: EcoDriving, private val tripDetailViewConfig: TripDetailViewConfig) : Serializable{

    fun getScore() = ecoDriving.score

    fun getAccelMessage() : String {
        return when {
            ecoDriving.scoreAccel < -4 -> tripDetailViewConfig.lowAccelText
            ecoDriving.scoreAccel < -2 -> tripDetailViewConfig.weakAccelText
            ecoDriving.scoreAccel < 1 -> tripDetailViewConfig.goodAccelText
            ecoDriving.scoreAccel < 3 -> tripDetailViewConfig.strongAccelText
            else -> tripDetailViewConfig.highAccelText
        }
    }

    fun getMaintainMessage() : String{
        return when {
            ecoDriving.scoreMain < 1.5 -> tripDetailViewConfig.goodMaintainText
            ecoDriving.scoreMain < 3.5 -> tripDetailViewConfig.weakMaintainText
            else -> tripDetailViewConfig.badMaintainText
        }
    }

    fun getDecelMessage() : String{
        return when {
            ecoDriving.scoreDecel < -4 -> tripDetailViewConfig.lowDecelText
            ecoDriving.scoreDecel < -2 -> tripDetailViewConfig.weakDecelText
            ecoDriving.scoreDecel < 1 -> tripDetailViewConfig.goodDecelText
            ecoDriving.scoreDecel < 3 -> tripDetailViewConfig.strongDecelText
            else -> tripDetailViewConfig.highDecelText
        }
    }

    fun getGaugeTitle() : String {
        return tripDetailViewConfig.ecodrivingGaugeTitle
    }
}
