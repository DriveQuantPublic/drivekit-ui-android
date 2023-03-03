package com.drivequant.drivekit.ui.mysynthesis.component.community

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.utils.DKRoundedCornerFrameLayout
import com.drivequant.drivekit.common.ui.utils.DKDrawableUtils
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.core.extension.reduceAccuracy
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.mysynthesis.MySynthesisConstant
import kotlin.math.abs

internal class MySynthesisCommunityGaugeView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private var viewModel: MySynthesisCommunityGaugeViewModel? = null
    private lateinit var mainGuideline: Guideline
    private lateinit var titleTextView: TextView
    private lateinit var scoreDescription: View
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

        this.titleTextView = findViewById(R.id.title)
        this.mainGuideline = findViewById(R.id.mainGuideline)
        this.scoreDescription = findViewById(R.id.scoreDescription)
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

        this.level6TextView.visibility = if (this.level6TextView.right > this.level7TextView.left) View.INVISIBLE else View.VISIBLE
        this.level5TextView.visibility = if (this.level5TextView.right > this.level7TextView.left) View.INVISIBLE else View.VISIBLE
        this.level4TextView.visibility = if (this.level4TextView.right > this.level5TextView.left && this.level5TextView.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        this.level1TextView.visibility = if (this.level1TextView.left < this.level0TextView.right) View.INVISIBLE else View.VISIBLE
    }

    fun configure(viewModel: MySynthesisCommunityGaugeViewModel) {
        this.viewModel = viewModel
        viewModel.onUpdateCallback = this::update
        update()
    }

    private fun update() {
        this.level0TextView.text = getFormattedLevelValue(0)
        this.level1TextView.text = getFormattedLevelValue(1)
        this.level2TextView.text = getFormattedLevelValue(2)
        this.level3TextView.text = getFormattedLevelValue(3)
        this.level4TextView.text = getFormattedLevelValue(4)
        this.level5TextView.text = getFormattedLevelValue(5)
        this.level6TextView.text = getFormattedLevelValue(6)
        this.level7TextView.text = getFormattedLevelValue(7)

        this.communityMinValueTextView.text = getFormattedValue(this.viewModel?.getCommunityMinScore())
        this.communityMeanValueTextView.text = getFormattedValue(this.viewModel?.getCommunityMeanScore())
        this.communityMaxValueTextView.text = getFormattedValue(this.viewModel?.getCommunityMaxScore())

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
        // Score indicators.
        this.scoreIndicator.background = DKDrawableUtils.circleDrawable(MySynthesisConstant.indicatorSize, synthesisColor)
        // Gauge corners.
        val cornerRadius = 7.convertDpToPx().toFloat()
        this.veryBadGaugeContainer.roundCorners(cornerRadius, 0f, 0f, cornerRadius)
        this.excellentGaugeContainer.roundCorners(0f, cornerRadius, cornerRadius, 0f)
        // Gauge colors.
        this.veryBadGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkVeryBad))
        this.badGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkBad))
        this.badMeanGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkBadMean))
        this.meanGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkMean))
        this.goodMeanGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkGoodMean))
        this.goodGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkGood))
        this.excellentGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkExcellent))
        // Community indicators.
        this.communityLine.setBackgroundColor(synthesisColor)
        this.communityMinIndicator.background = getCommunityIndicator(synthesisColor)
        this.communityMeanIndicator.background = getCommunityIndicator(synthesisColor)
        this.communityMaxIndicator.background = getCommunityIndicator(synthesisColor)
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

            this.viewModel?.scoreValue?.let { scoreValue ->
                getPercent(scoreValue, level0, level7)?.let { percent ->
                    this.mainGuideline.setGuidelinePercent(percent)
                }
            }
            this.viewModel?.getCommunityMinScore()?.let { communityMinScore ->
                getPercent(communityMinScore, level0, level7)?.let { percent ->
                    this.communityMinGuideline.setGuidelinePercent(percent)
                }
            }
            this.viewModel?.getCommunityMeanScore()?.let { communityMeanScore ->
                getPercent(communityMeanScore, level0, level7)?.let { percent ->
                    this.communityMeanGuideline.setGuidelinePercent(percent)
                }
            }
            this.viewModel?.getCommunityMaxScore()?.let { communityMaxScore ->
                getPercent(communityMaxScore, level0, level7)?.let { percent ->
                    this.communityMaxGuideline.setGuidelinePercent(percent)
                }
            }
        }




//        val scoreDescriptionParams = this.scoreDescription.layoutParams as LayoutParams
//
//        println("===========================")
//        println("=== measuredWidth = $measuredWidth")
//        println("=== left = ${scoreDescription.left}")
//        println("=== right = ${scoreDescription.right}")
//        println("=== width = ${scoreDescription.width}")
//        println("=== horizontalBias = ${scoreDescriptionParams.horizontalBias}")
//        println("===========================")
//
//        val left = this.scoreDescription.left
//        if (left < 0) {
//            val width = this.scoreDescription.width
//            val percent = -left/width.toFloat()
//            scoreDescriptionParams.horizontalBias = percent
//            println("=== percent = $percent")
//        }
    }

    private fun getPercent(scoreValue: Double, level0: Double, level7: Double): Float? {
        return if ((level7 - level0) > 0) {
            ((scoreValue - level0) / (level7 - level0)).toFloat()
        } else {
            null
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

}
