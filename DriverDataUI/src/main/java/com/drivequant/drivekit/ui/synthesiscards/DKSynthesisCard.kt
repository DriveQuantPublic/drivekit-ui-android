package com.drivequant.drivekit.ui.synthesiscards

import android.content.Context
import android.text.Spannable
import android.text.style.AbsoluteSizeSpan
import com.drivequant.drivekit.common.ui.component.DKGaugeConfiguration
import com.drivequant.drivekit.common.ui.component.GaugeConfiguration
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.access.AccessType
import com.drivequant.drivekit.core.access.DriveKitAccess
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.commons.enums.DKRoadCondition
import com.drivequant.drivekit.ui.extension.computeAverageScore
import java.io.Serializable
import kotlin.math.roundToInt

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
            SPEEDING -> SynthesisCard.SPEEDING(trips, showBottomText)
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

    override fun getTitle(context: Context): String =
        when (this) {
            is SAFETY -> R.string.dk_driverdata_my_weekly_score_safety
            is ECODRIVING -> R.string.dk_driverdata_my_weekly_score_ecodriving
            is DISTRACTION -> R.string.dk_driverdata_my_weekly_score_distraction
            is SPEEDING -> R.string.dk_driverdata_my_weekly_score_speeding
        }.let {
            context.getString(it)
        }

    override fun getExplanationContent(context: Context): String =
        when (this) {
            is DISTRACTION -> R.string.dk_driverdata_synthesis_info_distraction
            is ECODRIVING -> R.string.dk_driverdata_synthesis_info_ecodriving
            is SAFETY -> R.string.dk_driverdata_synthesis_info_safety
            is SPEEDING -> R.string.dk_driverdata_synthesis_info_speeding
        }.let {
            context.getString(it)
        }

    override fun getGaugeConfiguration(): DKGaugeConfiguration {
        val scoredTrips = trips.filterNot { it.unscored }
        return when (this) {
            is SAFETY -> GaugeConfiguration.SAFETY(scoredTrips.computeAverageScore(DKScoreType.SAFETY))
            is ECODRIVING -> GaugeConfiguration.ECO_DRIVING(scoredTrips.computeAverageScore(DKScoreType.ECO_DRIVING))
            is DISTRACTION -> GaugeConfiguration.DISTRACTION(scoredTrips.computeAverageScore(DKScoreType.DISTRACTION))
            is SPEEDING -> GaugeConfiguration.SPEEDING(scoredTrips.computeAverageScore(DKScoreType.SPEEDING))
        }
    }

    override fun getTopSynthesisCardInfo(context: Context) = SynthesisCardInfo.TRIPS(trips)

    override fun getMiddleSynthesisCardInfo(context: Context) =
        SynthesisCardInfo.DISTANCE(trips)

    override fun getBottomSynthesisCardInfo(context: Context) =
        SynthesisCardInfo.DURATION(trips)

    override fun getBottomText(context: Context): Spannable? {
        return if (trips.isEmpty() || !showBottomText) {
            null
        } else {
            val pair = SynthesisCardsUtils.getMainRoadCondition(trips, SynthesisCardsUtils.RoadConditionType.SAFETY)
            val identifier = when (pair.first) {
                DKRoadCondition.TRAFFIC_JAM -> com.drivequant.drivekit.common.ui.R.string.dk_common_no_value // should not happen
                DKRoadCondition.HEAVY_URBAN_TRAFFIC -> R.string.dk_driverdata_dense_urban
                DKRoadCondition.CITY -> R.string.dk_driverdata_urban
                DKRoadCondition.SUBURBAN -> R.string.dk_driverdata_extra_urban
                DKRoadCondition.EXPRESSWAYS -> R.string.dk_driverdata_expressway
            }

            val spannable = DKResource.buildString(context,
                DKColors.complementaryFontColor,
                DKColors.primaryColor,
                identifier,
                "${pair.second.roundToInt()}%"
            )
            spannable.setSpan(AbsoluteSizeSpan(18, true), 0, spannable.length, 0)
            return spannable
        }
    }

    internal fun hasAccess(): Boolean =
        when (this) {
            is SAFETY -> AccessType.SAFETY
            is ECODRIVING -> AccessType.ECODRIVING
            is DISTRACTION -> AccessType.PHONE_DISTRACTION
            is SPEEDING -> AccessType.SPEEDING
        }.let {
            DriveKitAccess.hasAccess(it)
        }
}
