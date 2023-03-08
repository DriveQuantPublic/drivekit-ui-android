package com.drivequant.drivekit.ui.mysynthesis.component.scorelegend

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKScoreTypeLevel
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.getScoreLevelDescription
import com.drivequant.drivekit.ui.extension.getScoreLevelTitle

internal class MySynthesisScoreLegendDialog {

    fun show(context: Context, scoreType: DKScoreType) {
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(context)
            .layout(R.layout.dk_my_synthesis_scores_legend_alert_dialog)
            .positiveButton(context.getString(R.string.dk_common_close)) { dialog, _ ->
                dialog.dismiss()
            }
            .cancelable(true)
            .show()

        alertDialog.findViewById<TextView>(R.id.my_synthesis_score_legend_title)?.apply {
            text = context.getString(scoreType.getTitle())
            headLine2(DriveKitUI.colors.primaryColor())
        }

        alertDialog.findViewById<TextView>(R.id.my_synthesis_score_legend_description)?.apply {
            text = context.getString(scoreType.getDescription())
            smallText()
        }

        alertDialog.findViewById<LinearLayout>(R.id.container_score_item)?.let { scoreItemContainer ->
            DKScoreTypeLevel.values().forEach {
                configureLegendScoreItem(context, scoreItemContainer, scoreType, it)
            }
        }
    }

    private fun configureLegendScoreItem(
        context: Context,
        container: LinearLayout,
        dkScoreType: DKScoreType,
        scoreLevel: DKScoreTypeLevel
    ) {
        val view = View.inflate(context, R.layout.dk_my_synthesis_scores_legend_item, null)
        view.findViewById<View>(R.id.score_color)?.let { scoreColor ->
            DrawableCompat.setTint(
                scoreColor.background,
                ContextCompat.getColor(scoreColor.context, scoreLevel.getColorResId())
            )
        }
        view.findViewById<TextView>(R.id.score_description)?.apply {
            val scoreLevels = scoreLevel.getScoreLevel(dkScoreType)
            val scoreValuesText: String = scoreLevel.getScoreLevelTitle().let {
                context.getString(
                    it,
                    scoreLevels.first.format(1),
                    scoreLevels.second.format(1)
                )
            }
            this.text = DKSpannable().append(scoreValuesText, context.resSpans {
                color(DriveKitUI.colors.primaryColor())
                typeface(Typeface.NORMAL)
                size(R.dimen.dk_text_normal)
            }).space()
                .append(
                    context.getString(scoreLevel.getScoreLevelDescription(dkScoreType)),
                    context.resSpans {
                        color(DriveKitUI.colors.complementaryFontColor())
                        typeface(Typeface.NORMAL)
                        size(R.dimen.dk_text_small)
                    }).toSpannable()
        }
        container.addView(view)
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(
            params.leftMargin,
            view.resources.getDimension(R.dimen.dk_margin_half).toInt(),
            params.rightMargin,
            view.resources.getDimension(R.dimen.dk_margin_half).toInt()
        )
        view.layoutParams = params
    }

    @StringRes
    private fun DKScoreType.getTitle() = when (this) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_safety_score
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_eco_score
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_distraction_score
        DKScoreType.SPEEDING -> R.string.dk_driverdata_speeding_score
    }

    @StringRes
    private fun DKScoreType.getDescription() = when (this) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_score_info
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_score_info
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_score_info
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_score_info
    }
}