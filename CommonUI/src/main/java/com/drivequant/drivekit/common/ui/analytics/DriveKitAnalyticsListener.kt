package com.drivequant.drivekit.common.ui.analytics

interface DriveKitAnalyticsListener {
    fun trackScreen(screen: String, className: String)
    fun trackEvent(event: DKAnalyticsEvent, parameters: Map<DKAnalyticsEventKey, Any>?) { }
}

enum class DKAnalyticsEvent {
    TRIP_OPEN
}

enum class DKAnalyticsEventKey {
    ITIN_ID
}
