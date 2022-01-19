package com.drivekit.demoapp.activity

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.component.DKGaugeConfiguration
import com.drivequant.drivekit.common.ui.component.DKGaugeType
import com.drivequant.drivekit.common.ui.utils.FormatType
import com.drivequant.drivekit.ui.synthesiscards.DKSynthesisCard
import com.drivequant.drivekit.ui.synthesiscards.DKSynthesisCardInfo

class CustomSynthesisCard : DKSynthesisCard {

    override fun getExplanationContent(context: Context): String = "Explication Distraction"

    override fun getGaugeConfiguration() =
        object : DKGaugeConfiguration {
            override fun getTitle(context: Context): Spannable {
                val spannableString = SpannableString("9")
                spannableString.setSpan(AbsoluteSizeSpan(100), 0, spannableString.length, 0)
                spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString.length, 0)
                return spannableString
            }
            override fun getScore(): Double = 9.0
            override fun getColor(value: Double): Int = R.color.colorPrimary
            override fun getMaxScore() = 10.0
            override fun getIcon(): Int = R.drawable.dk_common_distraction
            override fun getGaugeType(): DKGaugeType = DKGaugeType.OPEN
        }

    override fun getTitle(context: Context): String = "Mon score annuel | Distraction"

    override fun getTopSynthesisCardInfo(context: Context): DKSynthesisCardInfo =
        object : DKSynthesisCardInfo {
            override fun getIcon(context: Context): Drawable? = null
            override fun getText(context: Context): List<FormatType> = listOf()
        }

    override fun getMiddleSynthesisCardInfo(context: Context): DKSynthesisCardInfo =
        object : DKSynthesisCardInfo {
            override fun getIcon(context: Context): Drawable? = null
            override fun getText(context: Context): List<FormatType> = listOf()
        }

    override fun getBottomSynthesisCardInfo(context: Context): DKSynthesisCardInfo =
        object : DKSynthesisCardInfo {
            override fun getIcon(context: Context): Drawable? = null
            override fun getText(context: Context): List<FormatType> = listOf()
        }

    override fun getBottomText(context: Context): Spannable? = null
}