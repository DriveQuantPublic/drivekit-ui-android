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
        val distanceValue = if (viewModel.hasData && viewModel.distance != null) {
            DKResource.buildString(
                context,
                "%s ${context.getString(viewModel.distanceUnitId)}",
                DriveKitUI.colors.mainFontColor(),
                TextArg(
                    viewModel.distance.toDouble().format(0),
                    DriveKitUI.colors.primaryColor(),
                    DKStyle.HIGHLIGHT_SMALL
                )
            )
        } else {
            DKResource.buildString(
                context,
                "%s ${context.getString(viewModel.distanceUnitId)}",
                noDataColor,
                TextArg(
                    "-",
                    noDataColor,
                    DKStyle.HIGHLIGHT_SMALL
                )
            )
        }
        this.distanceView.text = distanceValue
        // Duration.
        val spannable = DKSpannable()
        if (viewModel.hasData && viewModel.duration != null) {
            val durationFormats = DKDataFormatter.formatDuration(context, viewModel.duration * 60.0)
            durationFormats.forEach {
                when (it) {
                    is FormatType.VALUE -> spannable.append(context, it.value, DriveKitUI.colors.primaryColor(), DKStyle.HIGHLIGHT_SMALL)
                    is FormatType.UNIT -> spannable.append(context, it.value, DriveKitUI.colors.mainFontColor(), DKStyle.NORMAL_TEXT)
                    is FormatType.SEPARATOR -> spannable.space()
                }
            }
        } else {
            spannable.append(context, "-", noDataColor, DKStyle.HIGHLIGHT_SMALL)
            spannable.space()
            spannable.append(context, context.getString(R.string.dk_common_unit_minute), noDataColor, DKStyle.NORMAL_TEXT)
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
