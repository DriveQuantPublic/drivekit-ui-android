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
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKRoundedCornerFrameLayout
import com.drivequant.drivekit.common.ui.utils.DKDrawableUtils
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
    private lateinit var communityMeanGuideline: Guideline
    private lateinit var communityMaxGuideline: Guideline
    private lateinit var communityLine: View
    private lateinit var communityMinValueTextView: TextView
    private lateinit var communityMeanValueTextView: TextView
    private lateinit var communityMaxValueTextView: TextView
    private lateinit var communityMinIndicator: View
    private lateinit var communityMeanIndicator: View
    private lateinit var communityMaxIndicator: View
    private lateinit var communityMinTextView: TextView
    private lateinit var communityMeanTextView: TextView
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
        this.communityMeanGuideline = findViewById(R.id.communityMeanGuideline)
        this.communityMaxGuideline = findViewById(R.id.communityMaxGuideline)
        this.communityLine = findViewById(R.id.communityLine)
        this.communityMinValueTextView = findViewById(R.id.communityMinValue)
        this.communityMeanValueTextView = findViewById(R.id.communityMeanValue)
        this.communityMaxValueTextView = findViewById(R.id.communityMaxValue)
        this.communityMinIndicator = findViewById(R.id.communityMinIndicator)
        this.communityMeanIndicator = findViewById(R.id.communityMeanIndicator)
        this.communityMaxIndicator = findViewById(R.id.communityMaxIndicator)
        this.communityMinTextView = findViewById(R.id.communityMinText)
        this.communityMeanTextView = findViewById(R.id.communityMeanText)
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
        if (this.communityMeanTextView.left + viewMargin <= this.communityMinTextView.right) {
            if (isMeanTextCentered()) {
                alignMeanText(ViewAlignment.START)
            } else {
                this.communityMeanTextView.visibility = View.INVISIBLE
            }
        } else if (this.communityMeanTextView.right + viewMargin >= this.communityMaxTextView.left) {
            if (isMeanTextCentered()) {
                alignMeanText(ViewAlignment.END)
            } else {
                this.communityMeanTextView.visibility = View.INVISIBLE
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
                smallText(DriveKitUI.colors.primaryColor())
            } ?: run {
                text = context.getString(R.string.dk_driverdata_mysynthesis_can_not_be_evaluated)
                smallText(DriveKitUI.colors.complementaryFontColor())
            }
        }
        this.level0TextView.text = getFormattedLevelValue(0)
        this.level1TextView.text = getFormattedLevelValue(1)
        this.level2TextView.text = getFormattedLevelValue(2)
        this.level3TextView.text = getFormattedLevelValue(3)
        this.level4TextView.text = getFormattedLevelValue(4)
        this.level5TextView.text = getFormattedLevelValue(5)
        this.level6TextView.text = getFormattedLevelValue(6)
        this.level7TextView.text = getFormattedLevelValue(7)

        this.communityMinValueTextView.text = getFormattedValue(this.viewModel?.communityMinScore)
        this.communityMeanValueTextView.text = getFormattedValue(this.viewModel?.communityMeanScore)
        this.communityMaxValueTextView.text = getFormattedValue(this.viewModel?.communityMaxScore)

        this.communityMinTextView.text = context.getString(R.string.dk_driverdata_mysynthesis_minimum)
        this.communityMeanTextView.text = context.getString(R.string.dk_driverdata_mysynthesis_average)
        this.communityMaxTextView.text = context.getString(R.string.dk_driverdata_mysynthesis_maximum)

        updateLayout()
    }

    private fun getLevelValue(index: Int): Double? = this.viewModel?.getLevelValue(index)
    private fun getFormattedLevelValue(index: Int): String? = getFormattedValue(getLevelValue(index))
    private fun getFormattedValue(value: Double?): String? = value?.format(1)

    private fun configure() {
        val synthesisColor = getColor(R.color.dkMySynthesisColor)
        this.scoreDescriptionIcon.imageTintList = ColorStateList.valueOf(DriveKitUI.colors.secondaryColor())
        this.scoreIndicator.background = DKDrawableUtils.circleDrawable(MySynthesisConstant.indicatorSize, synthesisColor)
        this.scoreDescriptionContainer.setOnClickListener { showScoreLegend() }
        // Texts color.
        val textColor = DriveKitUI.colors.primaryColor()
        this.scoreDescription.setTextColor(textColor)
        for (levelTextView in this.levelTextViews) {
            levelTextView.setTextColor(textColor)
        }
        this.communityMinValueTextView.setTextColor(textColor)
        this.communityMeanValueTextView.setTextColor(textColor)
        this.communityMaxValueTextView.setTextColor(textColor)
        this.communityMinTextView.setTextColor(textColor)
        this.communityMeanTextView.setTextColor(textColor)
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
            this.communityMeanIndicator.background = it
            this.communityMaxIndicator.background = it
        }
    }

    private fun getColor(@ColorRes colorId: Int) = ContextCompat.getColor(context, colorId)

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
                viewModel.communityMeanScore?.let { communityMeanScore ->
                    viewModel.getPercent(communityMeanScore, level0, level7)?.let { percent ->
                        this.communityMeanGuideline.setGuidelinePercent(percent)
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
        alignMeanText(ViewAlignment.CENTER)
        this.communityMeanTextView.visibility = View.VISIBLE
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

    private fun alignMeanText(alignment: ViewAlignment) {
        val indicatorHalfSize = (resources.getDimension(R.dimen.dk_mysynthesis_community_indicator_size) / 2f).toInt()
        val params = this.communityMeanTextView.layoutParams as LayoutParams
        params.startToStart = when (alignment) {
            ViewAlignment.START -> this.communityMeanGuideline.id
            ViewAlignment.CENTER -> this.communityMeanGuideline.id
            ViewAlignment.END -> LayoutParams.UNSET
        }
        params.endToEnd = when (alignment) {
            ViewAlignment.START -> LayoutParams.UNSET
            ViewAlignment.CENTER -> this.communityMeanGuideline.id
            ViewAlignment.END -> this.communityMeanGuideline.id
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
        this.communityMeanTextView.layoutParams = params
    }

    private fun isMeanTextCentered(): Boolean {
        val params = this.communityMeanTextView.layoutParams as LayoutParams
        return params.startToStart == this.communityMeanGuideline.id && params.endToEnd == this.communityMeanGuideline.id
    }

    private enum class ViewAlignment {
        START, CENTER, END;
    }

}
