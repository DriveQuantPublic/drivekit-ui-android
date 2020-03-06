package com.drivequant.drivekit.ui.tripdetail.viewmodel

import com.drivequant.drivekit.databaseutils.entity.TripAdvice
import com.drivequant.drivekit.ui.R

enum class MapItem {
    ECO_DRIVING,
    SAFETY,
    INTERACTIVE_MAP,
    DISTRACTION,
    SYNTHESIS;

    fun getImageResource() : Int {
        return when (this) {
            ECO_DRIVING -> R.drawable.dk_leaf_tab_icon
            SAFETY -> R.drawable.dk_shield_tab_icon
            INTERACTIVE_MAP -> R.drawable.dk_trip_timeline_tab_icon
            DISTRACTION -> R.drawable.dk_distraction_tab_icon
            SYNTHESIS -> R.drawable.dk_synthesis_tab_icon
        }
    }

    fun getAdvice(advices: List<TripAdvice>): TripAdvice? {
        for (advice in advices){
            if (this == SAFETY && advice.theme.equals("SAFETY")) {
                return advice
            } else if (this == ECO_DRIVING && advice.theme.equals("ECODRIVING")) {
                return advice
            }
        }
        return null
    }
}