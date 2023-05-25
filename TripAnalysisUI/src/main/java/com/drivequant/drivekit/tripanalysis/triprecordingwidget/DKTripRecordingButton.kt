package com.drivequant.drivekit.tripanalysis.triprecordingwidget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKRoundedCornerFrameLayout

class DKTripRecordingButton(context: Context, attrs: AttributeSet?) :
    DKRoundedCornerFrameLayout(context, attrs) {

    private lateinit var titleTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var stateImageView: ImageView
    private lateinit var stateImageBackground: View
    private lateinit var stateImageGroup: Group
    private var viewModel: DKTripRecordingButtonViewModel? = null

    override fun onFinishInflate() {
        super.onFinishInflate()

        val cornerRadius = context.resources.getDimension(R.dimen.dk_margin_half)
        this.roundCorners(cornerRadius, cornerRadius, cornerRadius, cornerRadius)

        this.titleTextView = findViewById(R.id.title)
        this.distanceTextView = findViewById(R.id.distanceSubtitle)
        this.durationTextView = findViewById(R.id.durationSubtitle)
        this.stateImageView = findViewById(R.id.icon)
        this.stateImageBackground = findViewById(R.id.iconBackground)
        this.stateImageGroup = findViewById(R.id.iconGroup)

        (this.background as GradientDrawable).setColor(DriveKitUI.colors.secondaryColor())
        (this.stateImageBackground.background as GradientDrawable).setColor(DriveKitUI.colors.fontColorOnSecondaryColor())
        this.stateImageView.setColorFilter(DriveKitUI.colors.secondaryColor())
        this.distanceTextView.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
        this.durationTextView.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
    }

    fun showConfirmationDialog() {

    }

    internal fun configure(viewModel: DKTripRecordingButtonViewModel) {
        this.viewModel = viewModel
        this.setOnClickListener {
            viewModel.toggleRecordingState()
        }
//        this.stateImageBackground = TODO()
        viewModel.onViewModelUpdate = this::update
        update()
    }

    private fun update() {
        this.titleTextView.text = this.viewModel?.title(context)
        if (this.viewModel?.hasSubtitle == true) {
            this.titleTextView.headLine2(DriveKitUI.colors.fontColorOnSecondaryColor())
            this.titleTextView.isAllCaps = false
            this.distanceTextView.text = this.viewModel?.distanceSubtitle(context)
            this.durationTextView.text = this.viewModel?.durationSubtitle(context)
            this.distanceTextView.visibility = View.VISIBLE
            this.durationTextView.visibility = View.VISIBLE
        } else {
            this.titleTextView.headLine1(DriveKitUI.colors.fontColorOnSecondaryColor())
            this.titleTextView.isAllCaps = true
            this.distanceTextView.visibility = View.GONE
            this.durationTextView.visibility = View.GONE
        }
        this.viewModel?.iconResId?.let {
            this.stateImageView.setImageResource(it)
            this.stateImageGroup.visibility = View.VISIBLE
        } ?: run {
            this.stateImageGroup.visibility = View.GONE
        }
    }

}
