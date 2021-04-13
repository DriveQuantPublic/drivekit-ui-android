package com.drivequant.drivekit.ui.synthesiscards.viewmodel

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.FormatType
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.synthesiscards.DKSynthesisCard

internal class DKSynthesisCardViewModel(
    private var synthesisCard: DKSynthesisCard
) : ViewModel() {

    fun getTitle(context: Context) = synthesisCard.getTitle(context)

    fun getExplanationContent(context: Context) = synthesisCard.getExplanationContent(context)

    fun getScore() = synthesisCard.getGaugeConfiguration().getScore()

    fun getBottomText(context: Context) = synthesisCard.getBottomText(context)

    fun getTopSynthesisCardInfo(context: Context) = buildFormatTypeData(
        context,
        synthesisCard.getTopSynthesisCardInfo(context).getText(context)
    )

    fun getTopSynthesisCardIcon(context: Context) =
        synthesisCard.getTopSynthesisCardInfo(context).getIcon(context)

    fun getMiddleSynthesisCardInfo(context: Context) = buildFormatTypeData(
        context,
        synthesisCard.getMiddleSynthesisCardInfo(context).getText(context)
    )

    fun getMiddleSynthesisCardIcon(context: Context) =
        synthesisCard.getMiddleSynthesisCardInfo(context).getIcon(context)

    fun getBottomSynthesisCardInfo(context: Context) = buildFormatTypeData(
        context,
        synthesisCard.getBottomSynthesisCardInfo(context).getText(context)
    )

    fun getBottomSynthesisCardIcon(context: Context) =
        synthesisCard.getBottomSynthesisCardInfo(context).getIcon(context)

    private fun buildFormatTypeData(context: Context, data: List<FormatType>): Spannable {
        val spannable = DKSpannable()
        data.forEach {
            when (it) {
                is FormatType.VALUE -> spannable.append(it.value, context.resSpans {
                    color(DriveKitUI.colors.primaryColor())
                    typeface(Typeface.BOLD)
                    size(R.dimen.dk_text_medium)
                })
                is FormatType.UNIT -> spannable.append(it.value, context.resSpans {
                    color(DriveKitUI.colors.complementaryFontColor())
                    size(R.dimen.dk_text_medium)
                })
                is FormatType.SEPARATOR -> spannable.append(it.value)
            }
        }
        return spannable.toSpannable()
    }

    @Suppress("UNCHECKED_CAST")
    class DKSynthesisCardViewModelFactory(
        private val synthesisCard: DKSynthesisCard
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DKSynthesisCardViewModel(synthesisCard) as T
        }
    }
}