package com.drivequant.drivekit.ui.driverprofile.component.distanceestimation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.extension.tintFromHueOfColor
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKDrawableUtils
import com.drivequant.drivekit.common.ui.utils.DKRoundedCornerFrameLayout
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.DriverProfileView

internal class DriverDistanceEstimationView(context: Context, attrs: AttributeSet) : DriverProfileView<DriverDistanceEstimationViewModel>(context, attrs) {
    private lateinit var titleView: TextView
    private lateinit var separatorView: View
    private lateinit var estimationIconContainer: FrameLayout
    private lateinit var estimationCaptionView: TextView
    private lateinit var currentDistanceIconContainer: FrameLayout
    private lateinit var currentDistanceCaptionView: TextView
    private lateinit var gaugeStartIndicatorView: View
    private lateinit var estimationGaugeView: DKRoundedCornerFrameLayout
    private lateinit var currentDistanceGaugeView: DKRoundedCornerFrameLayout
    private lateinit var estimationGaugeColorView: View
    private lateinit var currentDistanceGaugeColorView: View
    private lateinit var distanceEstimationView: TextView
    private lateinit var currentDistanceView: TextView
    @ColorInt
    private val estimationDistanceColor: Int = com.drivequant.drivekit.common.ui.R.color.dkContextCardColor1.tintFromHueOfColor(context, com.drivequant.drivekit.common.ui.R.color.primaryColor)
    @ColorInt
    private val currentDistanceColor: Int = com.drivequant.drivekit.common.ui.R.color.dkContextCardColor5.tintFromHueOfColor(context, com.drivequant.drivekit.common.ui.R.color.primaryColor)

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.titleView = findViewById(R.id.title)
        this.separatorView = findViewById(R.id.separator)
        this.estimationIconContainer = findViewById(R.id.estimationIconContainer)
        this.estimationCaptionView = findViewById(R.id.estimationCaption)
        this.currentDistanceIconContainer = findViewById(R.id.currentDistanceIconContainer)
        this.currentDistanceCaptionView = findViewById(R.id.currentDistanceCaption)
        this.gaugeStartIndicatorView = findViewById(R.id.gaugeStartIndicator)
        this.estimationGaugeView = findViewById(R.id.estimationGauge)
        this.currentDistanceGaugeView = findViewById(R.id.currentDistanceGauge)
        this.estimationGaugeColorView = findViewById(R.id.estimationGaugeColor)
        this.currentDistanceGaugeColorView = findViewById(R.id.currentDistanceGaugeColor)
        this.distanceEstimationView = findViewById(R.id.distanceEstimation)
        this.currentDistanceView = findViewById(R.id.currentDistance)

        configureTitle()
        configureGauges()
        configureDistanceTextViews()
        configureCaption()
    }

    override fun configure(viewModel: DriverDistanceEstimationViewModel) {
        this.titleView.setText(viewModel.titleId)
        this.currentDistanceCaptionView.setText(viewModel.distanceCaptionId)

        setWidthPercent(this.estimationGaugeView, viewModel.getDistanceEstimationPercentage())
        setWidthPercent(this.currentDistanceGaugeView, viewModel.getDistancePercentage())

        this.distanceEstimationView.text = viewModel.getDistanceEstimationString()
        this.currentDistanceView.text = viewModel.getDistanceString()

        if (viewModel.hasValue) {
            this.estimationGaugeColorView.setBackgroundColor(estimationDistanceColor)
            this.currentDistanceGaugeColorView.setBackgroundColor(currentDistanceColor)

            this.estimationIconContainer.background = DKDrawableUtils.circleDrawable(16.convertDpToPx(), estimationDistanceColor)
            this.currentDistanceIconContainer.background = DKDrawableUtils.circleDrawable(16.convertDpToPx(), currentDistanceColor)
        } else {
            val color = DKColors.neutralColor
            this.estimationGaugeColorView.setBackgroundColor(color)
            this.currentDistanceGaugeColorView.setBackgroundColor(color)

            val background = DKDrawableUtils.circleDrawable(16.convertDpToPx(), color)
            this.estimationIconContainer.background = background
            this.currentDistanceIconContainer.background = background
        }
    }

    private fun setWidthPercent(view: View, percent: Float) {
        val layoutParams = view.layoutParams as? ConstraintLayout.LayoutParams
        layoutParams?.let {
            it.matchConstraintPercentWidth = percent
            view.layoutParams = it
        }
    }

    private fun configureTitle() {
        this.titleView.headLine2()
    }

    private fun configureGauges() {
        val roundedCornerSize = 60.convertDpToPx().toFloat()
        this.estimationGaugeView.roundCorners(0f, roundedCornerSize, roundedCornerSize, 0f)
        this.currentDistanceGaugeView.roundCorners(0f, roundedCornerSize, roundedCornerSize, 0f)
    }

    private fun configureDistanceTextViews() {
        this.distanceEstimationView.smallText()
        this.currentDistanceView.smallText()
    }

    private fun configureCaption() {
        this.estimationCaptionView.setText(R.string.dk_driverdata_distance_card_estimation)
        this.estimationCaptionView.smallText()
        this.currentDistanceCaptionView.smallText()
    }
}
