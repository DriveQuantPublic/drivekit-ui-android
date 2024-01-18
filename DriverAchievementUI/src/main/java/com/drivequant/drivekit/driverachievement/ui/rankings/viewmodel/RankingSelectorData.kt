package com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel

import androidx.annotation.StringRes
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod

class RankingSelectorData(
    val index: Int,
    @StringRes val titleId: Int,
    var rankingPeriod: RankingPeriod
)
