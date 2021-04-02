package com.drivequant.drivekit.ui.synthesiscards

import android.content.Context
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.component.DKGaugeConfiguration
import com.drivequant.drivekit.common.ui.component.GaugeConfiguration
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.TripWithRelations

interface DKSynthesisCard {
    fun getTitle(context: Context): String
    fun getExplanationContent(context: Context): String?
    fun getGaugeConfiguration(): DKGaugeConfiguration
    fun getTopSynthesisCardInfo(context: Context): DKSynthesisCardInfo
    fun getMiddleSynthesisCardInfo(context: Context): DKSynthesisCardInfo
    fun getBottomSynthesisCardInfo(context: Context): DKSynthesisCardInfo
    fun getBottomText(context: Context): SpannableString?
}

sealed class SynthesisCard(open val trips: List<TripWithRelations>) : DKSynthesisCard {
    data class SAFETY(override val trips: List<TripWithRelations> = SynthesisCardsUtils.getLastWeekTrips()) : SynthesisCard(trips)
    data class ECODRIVING(override val trips: List<TripWithRelations> = SynthesisCardsUtils.getLastWeekTrips()) : SynthesisCard(trips)
    data class DISTRACTION(override val trips: List<TripWithRelations> = SynthesisCardsUtils.getLastWeekTrips()) : SynthesisCard(trips)
    data class SPEEDING(override val trips: List<TripWithRelations> = SynthesisCardsUtils.getLastWeekTrips()) : SynthesisCard(trips)

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
        val identifier = when (this) {
            is DISTRACTION -> "dk_driverdata_synthesis_info_distraction"
            is ECODRIVING -> "dk_driverdata_synthesis_info_ecodriving"
            is SAFETY -> "dk_driverdata_synthesis_info_safety"
            is SPEEDING -> "dk_driverdata_synthesis_info_speeding"
        }
        return DKResource.convertToString(context, identifier)
    }

    override fun getGaugeConfiguration(): DKGaugeConfiguration {
        return when (this) {
            is SAFETY -> GaugeConfiguration.SAFETY
            is ECODRIVING -> GaugeConfiguration.ECO_DRIVING
            is DISTRACTION -> GaugeConfiguration.DISTRACTION
            is SPEEDING -> GaugeConfiguration.SPEEDING
        }
    }

    override fun getTopSynthesisCardInfo(context: Context) = SynthesisCardInfo.TRIPS(context, trips)

    override fun getMiddleSynthesisCardInfo(context: Context) = SynthesisCardInfo.DISTANCE(context, trips)

    override fun getBottomSynthesisCardInfo(context: Context) = SynthesisCardInfo.DURATION(context, trips)

    override fun getBottomText(context: Context): SpannableString? {
        return null // TODO WIP
    }
}
