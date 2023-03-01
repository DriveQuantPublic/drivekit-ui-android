package com.drivequant.drivekit.ui.mysynthesis.component.community

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.utils.circleDrawable
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.mysynthesis.MySynthesisConstant

internal class MySynthesisCommunityGauge(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private lateinit var mainGuideline: Guideline
    private lateinit var titleTextView: TextView
    private lateinit var scoreDescription: View
    private lateinit var scoreArrowIndicator: View
    private lateinit var gaugeContainer: View
    private lateinit var veryBadGauge: View
    private lateinit var badGauge: View
    private lateinit var badMeanGauge: View
    private lateinit var meanGauge: View
    private lateinit var goodMeanGauge: View
    private lateinit var goodGauge: View
    private lateinit var excellentGauge: View
    private lateinit var level0TextView: TextView
    private lateinit var level1TextView: TextView
    private lateinit var level2TextView: TextView
    private lateinit var level3TextView: TextView
    private lateinit var level4TextView: TextView
    private lateinit var level5TextView: TextView
    private lateinit var level6TextView: TextView
    private lateinit var level7TextView: TextView
    private lateinit var scoreIndicator: View
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
        this.gaugeContainer = findViewById(R.id.gauge)
        this.veryBadGauge = findViewById(R.id.veryBadGauge)
        this.badGauge = findViewById(R.id.badGauge)
        this.badMeanGauge = findViewById(R.id.badMeanGauge)
        this.meanGauge = findViewById(R.id.meanGauge)
        this.goodMeanGauge = findViewById(R.id.goodMeanGauge)
        this.goodGauge = findViewById(R.id.goodGauge)
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
//        this.mainGuideline.setGuidelinePercent(0.5f)
    }

    private fun configure() {
        // Gauge colors.
        this.veryBadGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkVeryBad))
        this.badGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkBad))
        this.badMeanGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkBadMean))
        this.meanGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkMean))
        this.goodMeanGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkGoodMean))
        this.goodGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkGood))
        this.excellentGauge.setBackgroundColor(getColor(com.drivequant.drivekit.common.ui.R.color.dkExcellent))
        // Community indicators.
        this.communityMinIndicator.background = getCommunityIndicator()
        this.communityMeanIndicator.background = getCommunityIndicator()
        this.communityMaxIndicator.background = getCommunityIndicator()
    }

    private fun getColor(@ColorRes colorId: Int) = ContextCompat.getColor(context, colorId)

    private fun getCommunityIndicator(): Drawable = circleDrawable(MySynthesisConstant.indicatorSize, borderColor = getColor(R.color.dkMySynthesisCommunityColor),
        borderWidth = MySynthesisConstant.INDICATOR_BORDER_WIDTH.convertDpToPx().toFloat())

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val scoreDescriptionParams = this.scoreDescription.layoutParams as LayoutParams

        println("===========================")
        println("=== measuredWidth = $measuredWidth")
        println("=== left = ${scoreDescription.left}")
        println("=== right = ${scoreDescription.right}")
        println("=== width = ${scoreDescription.width}")
        println("=== horizontalBias = ${scoreDescriptionParams.horizontalBias}")
        println("===========================")

        val left = this.scoreDescription.left
        if (left < 0) {
            val width = this.scoreDescription.width
            val percent = -left/width.toFloat()
            scoreDescriptionParams.horizontalBias = percent
            println("=== percent = $percent")
        }
    }

}
