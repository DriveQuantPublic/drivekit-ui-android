package com.drivequant.drivekit.common.ui.component.ranking.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRanking

class DKRankingViewModel {

    lateinit var driverRanking: DKDriverRanking

    fun setDKDriverRanking(driverRanking: DKDriverRanking) {
        this.driverRanking = driverRanking
    }

    fun getTitle() = driverRanking.getTitle()
    fun getIcon(context: Context) = driverRanking.getIcon(context)
    fun getHeaderDisplayType() = driverRanking.getHeaderDisplayType()
    fun getProgression() = driverRanking.getProgression()
    fun getDriverGlobalRank(context: Context) = driverRanking.getDriverGlobalRank(context)
    fun getDriverRankingList() = driverRanking.getDriverRankingList()
    fun getBackgroundColor() = driverRanking.getBackgroundColor()
    fun getConditionTitle(context: Context) = driverRanking.getInfoPopupTitle(context)
    fun getConditionDescription(context: Context) = driverRanking.getInfoPopupMessage(context)
    fun getConditionVisibility() = driverRanking.hasInfoButton()
}