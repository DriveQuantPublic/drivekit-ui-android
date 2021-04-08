package com.drivequant.drivekit.ui

import androidx.fragment.app.Fragment
import com.drivequant.drivekit.ui.synthesiscards.DKSynthesisCard
import com.drivequant.drivekit.ui.synthesiscards.SynthesisCard
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardFragment
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardViewPagerFragment

object DriverDataSynthesisCardsUI {
    private var cards: List<DKSynthesisCard> = listOf() // TODO default values ?

    fun configure(cards: List<DKSynthesisCard>) {
        this.cards = cards
    }

    private fun sanitize(): List<DKSynthesisCard> {
        val computedList = mutableListOf<DKSynthesisCard>()
        cards.forEach {
            if (it is SynthesisCard) {
                if (it.hasAccess()){
                    computedList.add(it)
                }
            } else {
                computedList.add(it)
            }
        }
        return computedList
    }

    fun getFragments(listener: SynthesisCardsFragmentsListener) {
        val fragments = mutableListOf<DKSynthesisCardFragment>()
        sanitize().forEach {
            fragments.add(DKSynthesisCardFragment(it))
        }
        listener.onFragmentsLoaded(fragments)
    }

    fun getView(listener: SynthesisCardsViewListener) {
        getFragments(object : SynthesisCardsFragmentsListener {
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