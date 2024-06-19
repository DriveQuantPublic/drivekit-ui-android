package com.drivequant.drivekit.ui.mysynthesis.component.communitycard

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.intColor
import com.drivequant.drivekit.common.ui.extension.smallTextWithColor
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKDrawableUtils
import com.drivequant.drivekit.common.ui.utils.DKRoundedCornerFrameLayout
import com.drivequant.drivekit.common.ui.utils.DKScoreTypeLevel
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.getScoreLevelDescription
import com.drivequant.drivekit.ui.mysynthesis.MySynthesisConstant
import com.drivequant.drivekit.ui.mysynthesis.component.scorelegend.MySynthesisScoreLegendDialog
import kotlin.math.abs

internal class MySynthesisGaugeView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private var viewModel: MySynthesisGaugeViewModel? = null
    private lateinit var mainGuideline: Guideline
    private lateinit var scoreDescriptionContainer: View
    private lateinit var scoreDescription: TextView
    private lateinit var scoreDescriptionIcon: ImageView
    private lateinit var scoreArrowIndicator: View
    private lateinit var scoreIndicator: View
    private lateinit var veryBadGaugeContainer: DKRoundedCornerFrameLayout
    private lateinit var veryBadGauge: View
    private lateinit var badGauge: View
    private lateinit var badMeanGauge: View
    private lateinit var meanGauge: View
    private lateinit var goodMeanGauge: View
    private lateinit var goodGauge: View
    private lateinit var excellentGaugeContainer: DKRoundedCornerFrameLayout
    private lateinit var excellentGauge: View
    private lateinit var level0TextView: TextView
    private lateinit var level1TextView: TextView
    private lateinit var level2TextView: TextView
    private lateinit var level3TextView: TextView
    private lateinit var level4TextView: TextView
    private lateinit var level5TextView: TextView
    private lateinit var level6TextView: TextView
    private lateinit var level7TextView: TextView
    private lateinit var levelTextViews: List<TextView>
    private lateinit var communityMinGuideline: Guideline
    private lateinit var communityMedianGuideline: Guideline
    private lateinit var communityMaxGuideline: Guideline
    private lateinit var communityLine: View
    private lateinit var communityMinValueTextView: TextView
    private lateinit var communityMedianValueTextView: TextView
    private lateinit var communityMaxValueTextView: TextView
    private lateinit var communityMinIndicator: View
    private lateinit var communityMedianIndicator: View
    private lateinit var communityMaxIndicator: View
    private lateinit var communityMinTextView: TextView
    private lateinit var communityMedianTextView: TextView
    private lateinit var communityMaxTextView: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.mainGuideline = findViewById(R.id.mainGuideline)
        this.scoreDescriptionContainer = findViewById(R.id.scoreDescriptionContainer)
        this.scoreDescription = findViewById(R.id.scoreDescription)
        this.scoreDescriptionIcon = findViewById(R.id.scoreDescriptionIcon)
        this.scoreArrowIndicator = findViewById(R.id.scoreArrowIndicator)
        this.veryBadGaugeContainer = findViewById(R.id.veryBadGaugeContainer)
        this.veryBadGauge = findViewById(R.id.veryBadGauge)
        this.badGauge = findViewById(R.id.badGauge)
        this.badMeanGauge = findViewById(R.id.badMeanGauge)
        this.meanGauge = findViewById(R.id.meanGauge)
        this.goodMeanGauge = findViewById(R.id.goodMeanGauge)
        this.goodGauge = findViewById(R.id.goodGauge)
        this.excellentGaugeContainer = findViewById(R.id.excellentGaugeContainer)
        this.excellentGauge = findViewById(R.id.excellentGauge)
        this.level0TextView = findViewById(R.id.level0)
        this.level1TextView = findViewById(R.id.level1)
        this.level2TextView = findViewById(R.id.level2)
        this.level3TextView = findViewById(R.id.level3)
        this.level4TextView = findViewById(R.id.level4)
        this.level5TextView = findViewById(R.id.level5)
        this.level6TextView = findViewById(R.id.level6)
        this.level7TextView = findViewById(R.id.level7)
        this.levelTextViews = listOf(
            this.level0TextView,
            this.level1TextView,
            this.level2TextView,
            this.level3TextView,
            this.level4TextView,
            this.level5TextView,
            this.level6TextView,
            this.level7TextView
        )
        this.scoreIndicator = findViewById(R.id.scoreIndicator)
        this.communityMinGuideline = findViewById(R.id.communityMinGuideline)
        this.communityMedianGuideline = findViewById(R.id.communityMedianGuideline)
        this.communityMaxGuideline = findViewById(R.id.communityMaxGuideline)
        this.communityLine = findViewById(R.id.communityLine)
        this.communityMinValueTextView = findViewById(R.id.communityMinValue)
        this.communityMedianValueTextView = findViewById(R.id.communityMedianValue)
        this.communityMaxValueTextView = findViewById(R.id.communityMaxValue)
        this.communityMinIndicator = findViewById(R.id.communityMinIndicator)
        this.communityMedianIndicator = findViewById(R.id.communityMedianIndicator)
        this.communityMaxIndicator = findViewById(R.id.communityMaxIndicator)
        this.communityMinTextView = findViewById(R.id.communityMinText)
        this.communityMedianTextView = findViewById(R.id.communityMedianText)
        this.communityMaxTextView = findViewById(R.id.communityMaxText)

        configure()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (this.scoreDescriptionContainer.left < 0) {
            alignScoreDescriptionContainer(ViewAlignment.START)
        } else if (this.scoreDescriptionContainer.right > right) {
            alignScoreDescriptionContainer(ViewAlignment.END)
        }

        val viewMargin = 2.convertDpToPx()
        if (this.communityMedianTextView.left + viewMargin <= this.communityMinTextView.right) {
            if (isMedianTextCentered()) {
                alignMedianText(ViewAlignment.START)
            } else {
                this.communityMedianTextView.visibility = View.INVISIBLE
            }
        } else if (this.communityMedianTextView.right + viewMargin >= this.communityMaxTextView.left) {
            if (isMedianTextCentered()) {
                alignMedianText(ViewAlignment.END)
            } else {
                this.communityMedianTextView.visibility = View.INVISIBLE
            }
        }

        // Hide overlapping TextViews.
        this.levelTextViews.forEach { it.visibility = View.VISIBLE }
        val size = this.levelTextViews.size
        for (i in 0 until size / 2) {
            val leftTextView = this.levelTextViews[i]
            if (leftTextView.visibility == View.VISIBLE) {
                for (j in i + 1 until size) {
                    val otherTextView = this.levelTextViews[j]
                    if (leftTextView.right + viewMargin >= otherTextView.left) {
                        otherTextView.visibility = View.INVISIBLE
                    } else {
                        break
                    }
                }
            }
            val rightTextView = this.levelTextViews[size - 1 - i]
            if (rightTextView.visibility == View.VISIBLE) {
                for (j in size - 1 - i - 1 downTo 0) {
                    val otherTextView = this.levelTextViews[j]
                    if (rightTextView.left - viewMargin <= otherTextView.right) {
                        otherTextView.visibility = View.INVISIBLE
                    } else {
                        break
                    }
                }
            }
        }
    }

    fun configure(viewModel: MySynthesisGaugeViewModel) {
        this.viewModel = viewModel
        viewModel.onUpdateCallback = this::update
        update()
    }

    private fun update() {
        this.scoreDescription.apply {
            this@MySynthesisGaugeView.viewModel?.scoreLevel?.getScoreLevelDescription()?.let {
                text = context.getString(it)
                smallTextWithColor(DKColors.primaryColor)
            } ?: run {
                text = context.getString(R.string.dk_driverdata_mysynthesis_can_not_be_evaluated)
                smallTextWithColor(DKColors.complementaryFontColor)
            }
        }
        this.level0TextView.apply {
            text = getFormattedLevelValue(0)
            setTextColor(DKColors.mainFontColor)
        }
        this.level1TextView.apply {
            text = getFormattedLevelValue(1)
            setTextColor(DKColors.mainFontColor)
        }
        this.level2TextView.apply {
            text = getFormattedLevelValue(2)
            setTextColor(DKColors.mainFontColor)
        }
        this.level3TextView.apply {
            text = getFormattedLevelValue(3)
            setTextColor(DKColors.mainFontColor)
        }
        this.level4TextView.apply {
            text = getFormattedLevelValue(4)
            setTextColor(DKColors.mainFontColor)
        }
        this.level5TextView.apply {
            text = getFormattedLevelValue(5)
            setTextColor(DKColors.mainFontColor)
        }
        this.level6TextView.apply {
            text = getFormattedLevelValue(6)
            setTextColor(DKColors.mainFontColor)
        }
        this.level7TextView.apply {
            text = getFormattedLevelValue(7)
            setTextColor(DKColors.mainFontColor)
        }

        this.communityMinValueTextView.apply {
            text = getFormattedValue(this@MySynthesisGaugeView.viewModel?.communityMinScore)
            setTextColor(DKColors.mainFontColor)
        }
        this.communityMedianValueTextView.apply {
            text = getFormattedValue(this@MySynthesisGaugeView.viewModel?.communityMedianScore)
            setTextColor(DKColors.mainFontColor)
        }
        this.communityMaxValueTextView.apply {
            text = getFormattedValue(this@MySynthesisGaugeView.viewModel?.communityMaxScore)
            setTextColor(DKColors.mainFontColor)
        }

        this.communityMinTextView.apply {
            text = context.getString(R.string.dk_driverdata_mysynthesis_minimum)
            setTextColor(DKColors.mainFontColor)
        }
        this.communityMedianTextView.apply {
            text = context.getString(R.string.dk_driverdata_mysynthesis_median)
            setTextColor(DKColors.mainFontColor)
        }
        this.communityMaxTextView.apply {
            text = context.getString(R.string.dk_driverdata_mysynthesis_maximum)
            setTextColor(DKColors.mainFontColor)
        }

        updateLayout()
    }

    private fun getLevelValue(index: Int): Double? = this.viewModel?.getLevelValue(index)
    private fun getFormattedLevelValue(index: Int): String? = getFormattedValue(getLevelValue(index))
    private fun getFormattedValue(value: Double?): String? = value?.format(1)

    private fun configure() {
        val synthesisColor = getColor(R.color.dkMySynthesisColor)
        this.scoreDescriptionIcon.imageTintList = ColorStateList.valueOf(DKColors.secondaryColor)
        this.scoreIndicator.background = DKDrawableUtils.circleDrawable(MySynthesisConstant.indicatorSize, synthesisColor)
        this.scoreDescriptionContainer.setOnClickListener { showScoreLegend() }
        // Texts color.
        val textColor = DKColors.primaryColor
        this.scoreDescription.setTextColor(textColor)
        for (levelTextView in this.levelTextViews) {
            levelTextView.setTextColor(textColor)
        }
        this.communityMinValueTextView.setTextColor(textColor)
        this.communityMedianValueTextView.setTextColor(textColor)
        this.communityMaxValueTextView.setTextColor(textColor)
        this.communityMinTextView.setTextColor(textColor)
        this.communityMedianTextView.setTextColor(textColor)
        this.communityMaxTextView.setTextColor(textColor)
        // Gauge corners.
        val cornerRadius = MySynthesisConstant.getGaugeCornerRadius(context)
        this.veryBadGaugeContainer.roundCorners(cornerRadius, 0f, 0f, cornerRadius)
        this.excellentGaugeContainer.roundCorners(0f, cornerRadius, cornerRadius, 0f)
        // Gauge colors.
        this.veryBadGauge.setBackgroundColor(getColor(DKScoreTypeLevel.VERY_BAD.getColorResId()))
        this.badGauge.setBackgroundColor(getColor(DKScoreTypeLevel.BAD.getColorResId()))
        this.badMeanGauge.setBackgroundColor(getColor(DKScoreTypeLevel.NOT_GOOD.getColorResId()))
        this.meanGauge.setBackgroundColor(getColor(DKScoreTypeLevel.MEDIUM.getColorResId()))
        this.goodMeanGauge.setBackgroundColor(getColor(DKScoreTypeLevel.GREAT.getColorResId()))
        this.goodGauge.setBackgroundColor(getColor(DKScoreTypeLevel.VERY_GOOD.getColorResId()))
        this.excellentGauge.setBackgroundColor(getColor(DKScoreTypeLevel.EXCELLENT.getColorResId()))
        // Community indicators.
        this.communityLine.setBackgroundColor(synthesisColor)
        getCommunityIndicator(synthesisColor).let {
            this.communityMinIndicator.background = it
            this.communityMedianIndicator.background = it
            this.communityMaxIndicator.background = it
        }
    }

    private fun getColor(@ColorRes colorId: Int) = colorId.intColor(context)

    private fun getCommunityIndicator(@ColorInt color: Int): Drawable = DKDrawableUtils.circleDrawable(MySynthesisConstant.indicatorSize, borderColor = color,
        borderWidth = MySynthesisConstant.INDICATOR_BORDER_WIDTH.convertDpToPx().toFloat())

    private fun updateLayout() {
        val level0 = getLevelValue(0)
        val level1 = getLevelValue(1)
        val level2 = getLevelValue(2)
        val level3 = getLevelValue(3)
        val level4 = getLevelValue(4)
        val level5 = getLevelValue(5)
        val level6 = getLevelValue(6)
        val level7 = getLevelValue(7)
        if (level0 != null && level1 != null && level2 != null && level3 != null && level4 != null && level5 != null && level6 != null && level7 != null) {
            val levelSize = level7 - level0
            this.veryBadGaugeContainer.setPercent(level1 - level0, levelSize)
            this.badGauge.setPercent(level2 - level1, levelSize)
            this.badMeanGauge.setPercent(level3 - level2, levelSize)
            this.meanGauge.setPercent(level4 - level3, levelSize)
            this.goodMeanGauge.setPercent(level5 - level4, levelSize)
            this.goodGauge.setPercent(level6 - level5, levelSize)

            val viewModel = this.viewModel
            if (viewModel != null) {
                val scoreValue = viewModel.score
                if (viewModel.hasScore && scoreValue != null) {
                    this.scoreIndicator.visibility = View.VISIBLE
                    this.scoreArrowIndicator.visibility = View.VISIBLE
                    viewModel.getPercent(scoreValue, level0, level7)?.let { percent ->
                        this.mainGuideline.setGuidelinePercent(percent)
                    }
                } else {
                    this.mainGuideline.setGuidelinePercent(0.5f)
                    this.scoreIndicator.visibility = View.INVISIBLE
                    this.scoreArrowIndicator.visibility = View.INVISIBLE
                }
                viewModel.communityMinScore?.let { communityMinScore ->
                    viewModel.getPercent(communityMinScore, level0, level7)?.let { percent ->
                        this.communityMinGuideline.setGuidelinePercent(percent)
                    }
                }
                viewModel.communityMedianScore?.let { communityMedianScore ->
                    viewModel.getPercent(communityMedianScore, level0, level7)?.let { percent ->
                        this.communityMedianGuideline.setGuidelinePercent(percent)
                    }
                }
                viewModel.communityMaxScore?.let { communityMaxScore ->
                    viewModel.getPercent(communityMaxScore, level0, level7)?.let { percent ->
                        this.communityMaxGuideline.setGuidelinePercent(percent)
                    }
                }
            }
        }
        alignScoreDescriptionContainer(ViewAlignment.CENTER)
        alignMedianText(ViewAlignment.CENTER)
        this.communityMedianTextView.visibility = View.VISIBLE
    }

    private fun showScoreLegend() {
        this.viewModel?.scoreType?.let {
            MySynthesisScoreLegendDialog().show(context, it)
        }
    }

    private fun View.setPercent(length: Double, totalLength: Double) {
        val percent = (length / totalLength).toFloat()
        val params = this.layoutParams as LayoutParams
        if (abs(percent - params.matchConstraintPercentWidth) > 0.01f) { // Need to check this to prevent infinite layout loop.
            params.matchConstraintPercentWidth = (length / totalLength).toFloat()
            this.layoutParams = params
        }
    }

    private fun alignScoreDescriptionContainer(alignment: ViewAlignment) {
        val params = this.scoreDescriptionContainer.layoutParams as LayoutParams
        params.startToStart = when (alignment) {
            ViewAlignment.START -> this.id
            ViewAlignment.CENTER -> this.mainGuideline.id
            ViewAlignment.END -> LayoutParams.UNSET
        }
        params.endToEnd = when (alignment) {
            ViewAlignment.START -> LayoutParams.UNSET
            ViewAlignment.CENTER -> this.mainGuideline.id
            ViewAlignment.END -> this.id
        }
        params.marginStart = when (alignment) {
            ViewAlignment.START -> - this.paddingStart + 4.convertDpToPx()
            ViewAlignment.CENTER -> 0
            ViewAlignment.END -> 0
        }
        params.marginEnd = when (alignment) {
            ViewAlignment.START -> 0
            ViewAlignment.CENTER -> 0
            ViewAlignment.END -> - this.paddingEnd + 4.convertDpToPx()
        }
        this.scoreDescriptionContainer.layoutParams = params
    }

    private fun alignMedianText(alignment: ViewAlignment) {
        val indicatorHalfSize = (resources.getDimension(R.dimen.dk_mysynthesis_community_indicator_size) / 2f).toInt()
        val params = this.communityMedianTextView.layoutParams as LayoutParams
        params.startToStart = when (alignment) {
            ViewAlignment.START -> this.communityMedianGuideline.id
            ViewAlignment.CENTER -> this.communityMedianGuideline.id
            ViewAlignment.END -> LayoutParams.UNSET
        }
        params.endToEnd = when (alignment) {
            ViewAlignment.START -> LayoutParams.UNSET
            ViewAlignment.CENTER -> this.communityMedianGuideline.id
            ViewAlignment.END -> this.communityMedianGuideline.id
        }
        params.marginStart = when (alignment) {
            ViewAlignment.START -> -indicatorHalfSize
            ViewAlignment.CENTER -> 0
            ViewAlignment.END -> 0
        }
        params.marginEnd = when (alignment) {
            ViewAlignment.START -> 0
            ViewAlignment.CENTER -> 0
            ViewAlignment.END -> -indicatorHalfSize
        }
        this.communityMedianTextView.layoutParams = params
    }

    private fun isMedianTextCentered(): Boolean {
        val params = this.communityMedianTextView.layoutParams as LayoutParams
        return params.startToStart == this.communityMedianGuideline.id && params.endToEnd == this.communityMedianGuideline.id
    }

    private enum class ViewAlignment {
        START, CENTER, END;
    }

}
