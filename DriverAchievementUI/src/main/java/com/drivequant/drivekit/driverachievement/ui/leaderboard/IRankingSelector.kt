package com.drivequant.drivekit.driverachievement.ui.leaderboard

interface IRankingSelector {
    fun getSelectorText(): String
    fun onSelectorClicked()
}