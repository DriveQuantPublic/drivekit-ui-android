package com.drivequant.drivekit.ui.synthesiscards

import android.content.Context
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.component.DKGaugeType
import com.drivequant.drivekit.common.ui.component.GaugeType
import com.drivequant.drivekit.databaseutils.entity.Trip

interface DKTripCard {
    val context: Context
    val trips: List<Trip>
    fun getTitle(): String
    fun getExplanationContent(): String?
    fun getGaugeType(): DKGaugeType
    fun getTripCardInfo(): Set<DKTripCardInfo>
    fun getBottomText(): SpannableString?
}

sealed class TripCard(override val context: Context, override val trips: List<Trip>) : DKTripCard {
    data class SAFETY(override val context: Context, override val trips: List<Trip>) : TripCard(context, trips)
    data class ECODRIVING(override val context: Context, override val trips: List<Trip>) : TripCard(context, trips)

    override fun getTitle(): String {
        return when (this) {
            is SAFETY -> "mon score hebdo / sécu" // TODO WIP
            is ECODRIVING -> "mon score hebdo / écodriving"
        }
    }

    override fun getExplanationContent(): String? {
        return when (this) {
            is SAFETY -> "Explanation safety" // TODO WIP
            is ECODRIVING -> "Explanation ecodriving"
        }
    }

    override fun getGaugeType(): DKGaugeType {
        return when (this){
            is SAFETY -> GaugeType.SAFETY
            is ECODRIVING -> GaugeType.ECO_DRIVING
        }
    }

    override fun getTripCardInfo(): Set<DKTripCardInfo> {
        return setOf() // TODO WIP
    }

    override fun getBottomText(): SpannableString? {
        return null // TODO WIP
    }
}
