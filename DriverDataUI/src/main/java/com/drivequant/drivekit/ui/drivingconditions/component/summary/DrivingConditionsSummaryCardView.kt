package com.drivequant.drivekit.ui.drivingconditions.component.summary

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.ui.R

internal class DrivingConditionsSummaryCardView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private var viewModel: DrivingConditionsSummaryCardViewModel? = null
    private lateinit var tripsValue: TextView
    private lateinit var tripsLabel: TextView
    private lateinit var distanceValue: TextView
    private lateinit var distanceLabel: TextView
    private lateinit var separator: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        this.tripsValue = findViewById(R.id.textView_tripscountvalue)
        this.tripsLabel = findViewById(R.id.textView_tripscountlabel)
        this.distanceValue = findViewById(R.id.textView_distancevalue)
        this.distanceLabel = findViewById(R.id.textView_distancelabel)
        this.separator = findViewById(R.id.line_separator)
    }

    fun configure(viewModel: DrivingConditionsSummaryCardViewModel) {
        this.viewModel = viewModel
        viewModel.onViewModelUpdated = this::update
        update()
    }

    private fun update() {
        this.tripsValue.apply {
            text = this@DrivingConditionsSummaryCardView.viewModel?.getTripsCount()
            highlightMedium(DriveKitUI.colors.primaryColor())
        }
        this.tripsLabel.smallText()
        this.distanceValue.apply {
            text = this@DrivingConditionsSummaryCardView.viewModel?.getDistanceKm()
            highlightMedium(DriveKitUI.colors.primaryColor())
        }
        this.distanceLabel.smallText()
        this.separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}