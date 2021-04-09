package com.drivequant.drivekit.ui

import com.drivequant.drivekit.ui.synthesiscards.DKSynthesisCard
import com.drivequant.drivekit.ui.synthesiscards.LastTripsSynthesisCard
import com.drivequant.drivekit.ui.synthesiscards.SynthesisCard
import com.drivequant.drivekit.ui.synthesiscards.SynthesisCardsUtils
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardFragment
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardViewPagerFragment

object DriverDataSynthesisCardsUI {

    fun getFragments(cards: List<DKSynthesisCard>, listener: SynthesisCardsFragmentsListener) {
        val fragments = mutableListOf<DKSynthesisCardFragment>()
        cards.forEach {
            fragments.add(DKSynthesisCardFragment(it))
        }
        listener.onFragmentsLoaded(fragments)
    }

    @JvmOverloads
    fun getLastTripsSynthesisCardView(
        synthesisCards: List<LastTripsSynthesisCard> =
            listOf(
                LastTripsSynthesisCard.SAFETY,
                LastTripsSynthesisCard.DISTRACTION,
                LastTripsSynthesisCard.ECO_DRIVING,
                LastTripsSynthesisCard.SPEEDING
            ),
        listener: SynthesisCardsViewListener
    ) {
        val lastTrips = SynthesisCardsUtils.getLastWeekTrips()
        val fragments = synthesisCards.map {
            when (it) {
                LastTripsSynthesisCard.SAFETY -> SynthesisCard.SAFETY(lastTrips)
                LastTripsSynthesisCard.ECO_DRIVING -> SynthesisCard.ECODRIVING(lastTrips)
                LastTripsSynthesisCard.DISTRACTION -> SynthesisCard.DISTRACTION(lastTrips)
                LastTripsSynthesisCard.SPEEDING -> SynthesisCard.SPEEDING(lastTrips)
            }
        }.filter { it.hasAccess() }
        getSynthesisCardsView(fragments, listener)
    }

    fun getSynthesisCardsView(cards: List<DKSynthesisCard>, listener: SynthesisCardsViewListener) {
        getFragments(cards, object : SynthesisCardsFragmentsListener {
            override fun onFragmentsLoaded(fragments: List<DKSynthesisCardFragment>) {
                val fragment = DKSynthesisCardViewPagerFragment(fragments)
                listener.onViewLoaded(fragment)
            }
        })
    }
}

interface SynthesisCardsFragmentsListener {
    fun onFragmentsLoaded(fragments: List<DKSynthesisCardFragment>)
}

interface SynthesisCardsViewListener {
    fun onViewLoaded(fragment: DKSynthesisCardViewPagerFragment)
}