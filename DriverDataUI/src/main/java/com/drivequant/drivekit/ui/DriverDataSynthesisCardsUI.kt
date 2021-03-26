package com.drivequant.drivekit.ui

import androidx.fragment.app.Fragment
import com.drivequant.drivekit.ui.synthesiscards.DKSynthesisCard

object DriverDataSynthesisCardsUI {
    internal var cards: List<DKSynthesisCard> = listOf()
        private set

    fun configure(cards: List<DKSynthesisCard>) {
        this.cards = cards
    }

    fun getFragments(listener: SynthesisCardsFragmentsListener) {
        listener.onFragmentsLoaded(listOf()) // TODO WIP
    }

    fun getView(listener: SynthesisCardsViewListener) {
        listener.onViewLoaded(Fragment()) // TODO WIP
    }
}

interface SynthesisCardsFragmentsListener {
    fun onFragmentsLoaded(fragments: List<Fragment>)
}

interface SynthesisCardsViewListener {
    fun onViewLoaded(fragment: Fragment)
}