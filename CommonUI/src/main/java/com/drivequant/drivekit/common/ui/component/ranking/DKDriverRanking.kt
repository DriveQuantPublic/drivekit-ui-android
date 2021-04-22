package com.drivequant.drivekit.common.ui.component.ranking

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DriverProgression

interface DKDriverRanking {
    fun getHeaderDisplayType(): RankingHeaderDisplayType
    fun getTitle(): String
    fun getIcon(context: Context): Drawable?
    fun getProgression(previousRank: Int?): DriverProgression?
    fun getDriverGlobalRank(context: Context): Spannable
    fun getScoreTitle(): String
    fun getDriverRankingList(): List<DKDriverRankingItem>
    fun getJumpIndex(): Int
}

interface DKDriverRankingItem {
    fun getRank(): Int
    fun getRankResource(context: Context): Drawable?
    fun getNickname(context: Context): String
    fun getDistance(context: Context): String
    fun getScore(context: Context, textColor: Int): Spannable
    fun getUserId(): String
}

enum class RankingHeaderDisplayType {
    FULL, COMPACT
}