package com.drivequant.drivekit.common.ui.component.ranking.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRanking

class DKRankingViewModel : ViewModel() {

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
}