package com.drivequant.drivekit.ui.synthesiscards.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.ui.synthesiscards.DKSynthesisCard

internal class DKSynthesisCardViewModel(
    private var synthesisCard: DKSynthesisCard
) : ViewModel() {

    fun getTitle(context: Context) = synthesisCard.getTitle(context)

    fun getExplanationContent(context: Context) = synthesisCard.getExplanationContent(context)

    fun getScore() = synthesisCard.getGaugeConfiguration().getScore()

    @Suppress("UNCHECKED_CAST")
    class DKSynthesisCardViewModelFactory(
        private val synthesisCard: DKSynthesisCard
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DKSynthesisCardViewModel(synthesisCard) as T
        }
    }
}