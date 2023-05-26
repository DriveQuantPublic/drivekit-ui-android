package com.drivequant.drivekit.tripanalysis.triprecordingwidget

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKRoundedCornerFrameLayout
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysisUI

class DKTripRecordingButton : Fragment() {

    private lateinit var view: DKRoundedCornerFrameLayout
    private lateinit var titleTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var stateImageView: ImageView
    private lateinit var stateImageBackground: View
    private lateinit var stateImageGroup: Group
    private lateinit var viewModel: DKTripRecordingButtonViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = (inflater.inflate(
        R.layout.dk_tripanalysis_trip_recording_button,
        container
    ) as? DKRoundedCornerFrameLayout)?.also {
        this.view = it
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cornerRadius = view.resources.getDimension(R.dimen.dk_margin_half)
        this.view.roundCorners(cornerRadius, cornerRadius, cornerRadius, cornerRadius)

        this.titleTextView = this.view.findViewById(R.id.title)
        this.distanceTextView = this.view.findViewById(R.id.distanceSubtitle)
        this.durationTextView = this.view.findViewById(R.id.durationSubtitle)
        this.stateImageView = this.view.findViewById(R.id.icon)
        this.stateImageBackground = this.view.findViewById(R.id.iconBackground)
        this.stateImageGroup = this.view.findViewById(R.id.iconGroup)

        (this.view.background as GradientDrawable).setColor(DriveKitUI.colors.secondaryColor())
        (this.stateImageBackground.background as GradientDrawable).setColor(DriveKitUI.colors.fontColorOnSecondaryColor())
        this.stateImageView.setColorFilter(DriveKitUI.colors.secondaryColor())
        this.distanceTextView.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
        this.durationTextView.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())

        configure()
    }

    fun showConfirmationDialog() {

    }

    private fun configure() {
        this.viewModel = ViewModelProvider(
            this,
            DKTripRecordingButtonViewModel.DKTripRecordingButtonViewModelFactory(
                DriveKitTripAnalysisUI.tripRecordingUserMode
            )
        )[DKTripRecordingButtonViewModel::class.java]
        this.viewModel.onViewModelUpdate = this::update
        this.view.setOnClickListener {
            viewModel.toggleRecordingState()
        }
//        this.stateImageBackground = TODO()
        update()
    }

    private fun update() {
        activity?.let {
            this.titleTextView.text = this.viewModel.title(it)
            if (this.viewModel.hasSubtitle) {
                this.titleTextView.headLine2(DriveKitUI.colors.fontColorOnSecondaryColor())
                this.titleTextView.isAllCaps = false
                this.distanceTextView.text = this.viewModel.distanceSubtitle(it)
                this.durationTextView.text = this.viewModel.durationSubtitle()
                this.distanceTextView.visibility = View.VISIBLE
                this.durationTextView.visibility = View.VISIBLE
            } else {
                this.titleTextView.headLine1(DriveKitUI.colors.fontColorOnSecondaryColor())
                this.titleTextView.isAllCaps = true
                this.distanceTextView.visibility = View.GONE
                this.durationTextView.visibility = View.GONE
            }
            this.viewModel.iconResId?.let { iconResId ->
                this.stateImageView.setImageResource(iconResId)
                this.stateImageGroup.visibility = View.VISIBLE
            } ?: run {
                this.stateImageGroup.visibility = View.GONE
            }
        }
        this.view.visibility = if (this.viewModel.isHidden()) View.GONE else View.VISIBLE
    }

}
