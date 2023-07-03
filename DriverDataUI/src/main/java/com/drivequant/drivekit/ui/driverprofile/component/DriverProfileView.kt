package com.drivequant.drivekit.ui.driverprofile.component

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView

internal abstract class DriverProfileView<ViewModel>(context: Context, attrs: AttributeSet): CardView(context, attrs) {
    abstract fun configure(viewModel: ViewModel)
}
