package com.drivequant.drivekit.ui.driverprofile.component.commontripfeature

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.graphical.DKStyle
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.FormatType
import com.drivequant.drivekit.common.ui.utils.TextArg
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.DriverProfileView

internal class DriverCommonTripFeatureView(context: Context, attrs: AttributeSet) : DriverProfileView<DriverCommonTripFeatureViewModel>(context, attrs) {
    private lateinit var titleView: TextView
    private lateinit var distanceView: TextView
    private lateinit var durationView: TextView
    private lateinit var roadContextView: TextView
    private lateinit var leftSeparator: View
    private lateinit var rightSeparator: View

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.titleView = findViewById(R.id.title)
        this.distanceView = findViewById(R.id.distance)
        this.durationView = findViewById(R.id.duration)
        this.roadContextView = findViewById(R.id.roadContext)
        this.leftSeparator = findViewById(R.id.leftSeparator)
        this.rightSeparator = findViewById(R.id.rightSeparator)

        configureUi()
    }

    override fun configure(viewModel: DriverCommonTripFeatureViewModel) {
        val noDataColor = DriveKitUI.colors.complementaryFontColor()
        // Title.
        this.titleView.setText(viewModel.titleId)
        // Distance.
        this.distanceView.text = DKResource.buildString(
            context,
            "%s ${context.getString(viewModel.distanceUnitId)}",
            if (viewModel.hasData) DriveKitUI.colors.mainFontColor() else noDataColor,
            TextArg(
                viewModel.distance.toDouble().format(0),
                if (viewModel.hasData) DriveKitUI.colors.primaryColor() else noDataColor,
                DKStyle.HIGHLIGHT_SMALL
            )
        )
        // Duration.
        val spannable = DKSpannable()
        val durationFormats = DKDataFormatter.formatDuration(context, viewModel.duration * 60.0)
        durationFormats.forEach {
            when (it) {
                is FormatType.VALUE -> spannable.append(
                    context,
                    it.value,
                    if (viewModel.hasData) DriveKitUI.colors.primaryColor() else noDataColor,
                    DKStyle.HIGHLIGHT_SMALL
                )

                is FormatType.UNIT -> spannable.append(
                    context,
                    it.value,
                    if (viewModel.hasData) DriveKitUI.colors.mainFontColor() else noDataColor,
                    DKStyle.NORMAL_TEXT
                )

                is FormatType.SEPARATOR -> spannable.space()
            }
        }
        this.durationView.text = spannable.toSpannable()
        // Road context.
        this.roadContextView.setText(viewModel.roadContextId)
        if (viewModel.hasData) {
            this.roadContextView.setTextColor(DriveKitUI.colors.mainFontColor())
        } else {
            this.roadContextView.setTextColor(noDataColor)
        }
    }

    private fun configureUi() {
        this.titleView.headLine2()
        this.roadContextView.normalText()
        this.distanceView.normalText()
        this.leftSeparator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        this.rightSeparator.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}
