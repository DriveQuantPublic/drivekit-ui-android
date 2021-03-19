package com.drivequant.drivekit.ui.synthesiscards

import android.content.Context
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.component.DKGaugeType
import com.drivequant.drivekit.common.ui.component.GaugeType
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Trip

interface DKTripCard {
    val trips: List<Trip>
    fun getTitle(context: Context): String
    fun getExplanationContent(context: Context): String?
    fun getGaugeType(): DKGaugeType
    fun getTripCardInfo(context: Context): List<DKTripCardInfo>
    fun getBottomText(context: Context): SpannableString?
}

sealed class TripCard(override val trips: List<Trip>) : DKTripCard {
    data class SAFETY(override val trips: List<Trip> = listOf()) : TripCard(trips)
    data class ECODRIVING(override val trips: List<Trip> = listOf()) : TripCard(trips)
    data class DISTRACTION(override val trips: List<Trip> = listOf()) : TripCard(trips)
    data class SPEEDING(override val trips: List<Trip> = listOf()) : TripCard(trips)

    private var internalTrips: List<Trip>

    init {
        internalTrips = when (this){
            is SAFETY -> listOf()
            is ECODRIVING -> listOf()
            is DISTRACTION -> listOf()
            is SPEEDING -> listOf()
        }
    }

    override fun getTitle(context: Context): String {
        val identifier = when (this) {
            is SAFETY -> "dk_driverdata_my_weekly_score_safety"
            is ECODRIVING -> "dk_driverdata_my_weekly_score_ecodriving"
            is DISTRACTION -> "dk_driverdata_my_weekly_score_distraction"
            is SPEEDING -> "dk_driverdata_my_weekly_score_speeding"
        }
        return DKResource.convertToString(context, identifier)
    }

    override fun getExplanationContent(context: Context): String? {
        return DKResource.convertToString(context, "dk_driverdata_synthesis_card_explanation")
    }

    override fun getGaugeType(): DKGaugeType {
        return when (this){
            is SAFETY -> GaugeType.SAFETY
            is ECODRIVING -> GaugeType.ECO_DRIVING
            is DISTRACTION -> GaugeType.DISTRACTION
            is SPEEDING -> GaugeType.SPEEDING
        }
    }

    override fun getTripCardInfo(context: Context): List<DKTripCardInfo> {
        return listOf() // TODO WIP
    }

    override fun getBottomText(context: Context): SpannableString? {
        return null // TODO WIP
    }
}
