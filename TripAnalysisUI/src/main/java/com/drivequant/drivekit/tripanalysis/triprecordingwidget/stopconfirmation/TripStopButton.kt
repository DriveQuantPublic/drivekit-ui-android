package com.drivequant.drivekit.tripanalysis.triprecordingwidget.stopconfirmation

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.extension.tintDrawable

internal class TripStopButton(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private lateinit var buttonTitle: TextView
    private lateinit var buttonDescription: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.buttonTitle = findViewById(R.id.buttonTitle)
        this.buttonDescription = findViewById(R.id.buttonDescription)

        configure()
    }

    fun configure(@StringRes titleId: Int, @StringRes subtitleId: Int) {
        this.buttonTitle.setText(titleId)
        this.buttonDescription.setText(subtitleId)
    }

    private fun configure() {
        this.buttonTitle.apply {
            headLine1(DriveKitUI.colors.secondaryColor())
            isAllCaps = true
        }
        this.buttonDescription.smallText(DriveKitUI.colors.complementaryFontColor())
    }
}
