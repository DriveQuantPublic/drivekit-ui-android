package com.drivequant.drivekit.ui.synthesiscards

import android.content.Context
import android.text.Spannable
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.DKGaugeConfiguration
import com.drivequant.drivekit.ui.commons.enums.GaugeConfiguration
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.access.AccessType
import com.drivequant.drivekit.core.access.DriveKitAccess
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.commons.enums.DKRoadCondition
import com.drivequant.drivekit.ui.extension.computeDistractionScoreAverage
import com.drivequant.drivekit.ui.extension.computeEcodrivingScoreAverage
import com.drivequant.drivekit.ui.extension.computeSafetyScoreAverage
import com.drivequant.drivekit.ui.extension.computeSpeedingScoreAverage
import java.io.Serializable

interface DKSynthesisCard : Serializable {
    fun getTitle(context: Context): String
    fun getExplanationContent(context: Context): String?
    fun getGaugeConfiguration(): DKGaugeConfiguration
    fun getTopSynthesisCardInfo(context: Context): DKSynthesisCardInfo
    fun getMiddleSynthesisCardInfo(context: Context): DKSynthesisCardInfo
    fun getBottomSynthesisCardInfo(context: Context): DKSynthesisCardInfo
    fun getBottomText(context: Context): Spannable?
}

enum class LastTripsSynthesisCard {
    SAFETY,
    ECO_DRIVING,
    DISTRACTION,
    SPEEDING;

    @JvmOverloads
    fun getDKSynthesisCard(trips: List<Trip>, showBottomText: Boolean  = true) : DKSynthesisCard {
        return when (this){
            SAFETY -> SynthesisCard.SAFETY(trips, showBottomText)
            ECO_DRIVING -> SynthesisCard.ECODRIVING(trips, showBottomText)
            DISTRACTION -> SynthesisCard.DISTRACTION(trips, showBottomText)
            SPEEDING -> SynthesisCard.DISTRACTION(trips, showBottomText)
        }
    }
}

sealed class SynthesisCard(open var trips: List<Trip>, open var showBottomText: Boolean = true) : DKSynthesisCard {
    data class SAFETY(
        override var trips: List<Trip> = SynthesisCardsUtils.getLastWeekTrips(),
        override var showBottomText: Boolean = true
    ) : SynthesisCard(trips)

    data class ECODRIVING(
        override var trips: List<Trip> = SynthesisCardsUtils.getLastWeekTrips(),
        override var showBottomText: Boolean = true
    ) : SynthesisCard(trips)

    data class DISTRACTION(
        override var trips: List<Trip> = SynthesisCardsUtils.getLastWeekTrips(),
        override var showBottomText: Boolean = true
    ) : SynthesisCard(trips)

    data class SPEEDING(
        override var trips: List<Trip> = SynthesisCardsUtils.getLastWeekTrips(),
        override var showBottomText: Boolean = true
    ) : SynthesisCard(trips)

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
            is SAFETY -> GaugeConfiguration.SAFETY(trips.computeSafetyScoreAverage())
            is ECODRIVING -> GaugeConfiguration.ECO_DRIVING(trips.computeEcodrivingScoreAverage())
            is DISTRACTION -> GaugeConfiguration.DISTRACTION(trips.computeDistractionScoreAverage())
            is SPEEDING -> GaugeConfiguration.SPEEDING(trips.computeSpeedingScoreAverage())
        }
    }

    override fun getTopSynthesisCardInfo(context: Context) = SynthesisCardInfo.TRIPS(trips)

    override fun getMiddleSynthesisCardInfo(context: Context) =
        SynthesisCardInfo.DISTANCE(trips)

    override fun getBottomSynthesisCardInfo(context: Context) =
        SynthesisCardInfo.DURATION(trips)

    override fun getBottomText(context: Context): Spannable? {
        return if (trips.isEmpty() || !showBottomText){
            null
        } else {
            val pair = SynthesisCardsUtils.getMainRoadCondition(trips, SynthesisCardsUtils.RoadConditionType.SAFETY)
            val identifier = when (pair.first) {
                DKRoadCondition.TRAFFIC_JAM -> "" // should not happen
                DKRoadCondition.HEAVY_URBAN_TRAFFIC -> "dk_driverdata_dense_urban"
                DKRoadCondition.CITY -> "dk_driverdata_urban"
                DKRoadCondition.SUBURBAN -> "dk_driverdata_extra_urban"
                DKRoadCondition.EXPRESSWAYS -> "dk_driverdata_expressway"
            }

            DKResource.buildString(context,
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.primaryColor(),
                identifier,
                "${pair.second.toInt()}%"
            )
        }
    }

    internal fun hasAccess(): Boolean {
        val accessType = when (this){
            is SAFETY -> AccessType.SAFETY
            is ECODRIVING -> AccessType.ECODRIVING
            is DISTRACTION -> AccessType.PHONE_DISTRACTION
            is SPEEDING -> AccessType.SPEEDING
        }
        return DriveKitAccess.hasAccess(accessType)
    }
}
