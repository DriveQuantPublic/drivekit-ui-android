package com.drivequant.drivekit.ui.driverprofile.component.profilefeature

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.DriverProfileView

internal class DriverProfileFeatureView(context: Context, attrs: AttributeSet) : DriverProfileView<DriverProfileFeatureViewModel>(context, attrs) {
    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var iconView: ImageView

    override fun onFinishInflate() {
        super.onFinishInflate()
        this.titleView = findViewById(R.id.title)
        this.descriptionView = findViewById(R.id.description)
        this.iconView = findViewById(R.id.icon)

        this.titleView.headLine2()
        this.descriptionView.smallText(DriveKitUI.colors.complementaryFontColor())
    }

    override fun configure(viewModel: DriverProfileFeatureViewModel) {
        this.titleView.setText(viewModel.titleId)
        this.descriptionView.text = viewModel.getDescription(this.context)
        this.iconView.setImageResource(viewModel.iconId)
    }
}
