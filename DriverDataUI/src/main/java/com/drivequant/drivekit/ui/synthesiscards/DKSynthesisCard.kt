package com.drivequant.drivekit.ui.synthesiscards

import android.content.Context
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.component.DKGaugeType
import com.drivequant.drivekit.common.ui.component.GaugeType
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Trip

interface DKSynthesisCard {
    fun getTitle(context: Context): String
    fun getExplanationContent(context: Context): String?
    fun getGaugeType(): DKGaugeType
    fun getTopSynthesisCardInfo(context: Context): DKSynthesisCardInfo
    fun getMiddleSynthesisCardInfo(context: Context): DKSynthesisCardInfo
    fun getBottomSynthesisCardInfo(context: Context): DKSynthesisCardInfo
    fun getBottomText(context: Context): SpannableString?
}

sealed class SynthesisCard : DKSynthesisCard {
    data class SAFETY(val trips: List<Trip> = listOf()) : SynthesisCard()
    data class ECODRIVING(val trips: List<Trip> = listOf()) : SynthesisCard()
    data class DISTRACTION(val trips: List<Trip> = listOf()) : SynthesisCard()
    data class SPEEDING(val trips: List<Trip> = listOf()) : SynthesisCard()

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
        return when (this) {
            is SAFETY -> GaugeType.SAFETY
            is ECODRIVING -> GaugeType.ECO_DRIVING
            is DISTRACTION -> GaugeType.DISTRACTION
            is SPEEDING -> GaugeType.SPEEDING
        }
    }

    override fun getTopSynthesisCardInfo(context: Context) = SynthesisCardInfo.TRIPS(context)

    override fun getMiddleSynthesisCardInfo(context: Context) = SynthesisCardInfo.DISTANCE(context)

    override fun getBottomSynthesisCardInfo(context: Context) = SynthesisCardInfo.DURATION(context)

    override fun getBottomText(context: Context): SpannableString? {
        return null // TODO WIP
    }
}
