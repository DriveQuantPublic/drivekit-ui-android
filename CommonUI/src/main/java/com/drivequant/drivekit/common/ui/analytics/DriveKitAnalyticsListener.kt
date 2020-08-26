package com.drivequant.drivekit.common.ui.analytics

interface DriveKitAnalyticsListener {
    fun trackScreen(screen: String, className: String)
    fun trackEvent(event: String)
}