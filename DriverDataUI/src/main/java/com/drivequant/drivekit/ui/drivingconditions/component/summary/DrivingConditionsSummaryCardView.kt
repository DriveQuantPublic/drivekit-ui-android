package com.drivequant.drivekit.ui.drivingconditions.component.summary

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.ui.R

internal class DrivingConditionsSummaryCardView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private var viewModel: DrivingConditionsSummaryCardViewModel? = null
    private lateinit var emptyView: ConstraintLayout
    private lateinit var emptyViewTitle: TextView
    private lateinit var emptyViewDescription: TextView

    private lateinit var container: ConstraintLayout
    private lateinit var tripsValue: TextView
    private lateinit var tripsLabel: TextView
    private lateinit var distanceValue: TextView
    private lateinit var distanceLabel: TextView
    private lateinit var separator: View

    override fun onFinishInflate() {
        super.onFinishInflate()
        this.emptyView = findViewById(R.id.empty_view)
        this.emptyViewTitle = findViewById(R.id.empty_view_title)
        this.emptyViewDescription = findViewById(R.id.empty_view_description)

        this.container = findViewById(R.id.container)
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
        val hasData = this.viewModel?.hasData() ?: false
        if (hasData) {
            displayData()
        } else {
            displayNoData()
        }
    }

    private fun displayData() {
        this.emptyView.visibility = View.GONE
        this.container.visibility = View.VISIBLE

        this.tripsValue.apply {
            text = this@DrivingConditionsSummaryCardView.viewModel?.formatTripsCount()
            highlightMedium(DriveKitUI.colors.primaryColor())
            setTypeface(DriveKitUI.secondaryFont(context), Typeface.BOLD)
        }
        this.tripsLabel.apply {
            text = context.resources.getQuantityString(
                R.plurals.trip_plural,
                this@DrivingConditionsSummaryCardView.viewModel?.tripCount ?: 0
            )
            smallText(DriveKitUI.colors.complementaryFontColor())
        }
        this.distanceValue.apply {
            text = this@DrivingConditionsSummaryCardView.viewModel?.formatDistanceKm()
            highlightMedium(DriveKitUI.colors.primaryColor())
            setTypeface(DriveKitUI.secondaryFont(context), Typeface.BOLD)
        }
        this.distanceLabel.apply {
            text = context.resources.getQuantityString(R.plurals.dk_driverdata_drivingconditions_distance, viewModel?.tripCount ?: 0)
            smallText(DriveKitUI.colors.complementaryFontColor())
        }
        this.separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }

    private fun displayNoData() {
        this.emptyView.visibility = View.VISIBLE
        this.container.visibility = View.GONE

        this.emptyViewTitle.apply {
            setText(R.string.dk_driverdata_drivingconditions_title_empty_data)
            headLine2(DriveKitUI.colors.primaryColor())
        }
        this.emptyViewDescription.apply {
            setText(R.string.dk_driverdata_drivingconditions_empty_data)
            smallText(DriveKitUI.colors.complementaryFontColor())
        }
    }
}
