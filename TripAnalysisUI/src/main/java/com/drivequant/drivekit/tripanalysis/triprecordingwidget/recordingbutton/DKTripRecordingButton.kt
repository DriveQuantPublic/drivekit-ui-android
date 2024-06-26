package com.drivequant.drivekit.tripanalysis.triprecordingwidget.recordingbutton

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.tintFromHueOfColor
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKRoundedCornerFrameLayout
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysisUI
import com.drivequant.drivekit.tripanalysis.triprecordingwidget.stopconfirmation.TripStopConfirmationView

class DKTripRecordingButton : Fragment() {

    private lateinit var view: DKRoundedCornerFrameLayout
    private lateinit var titleTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var stateImageView: ImageView
    private lateinit var stateImageBackground: View
    private lateinit var stateImageBorder: View
    private lateinit var stateImageGroup: Group
    private lateinit var viewModel: DKTripRecordingButtonViewModel
    private lateinit var container: ConstraintLayout

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

        val cornerRadius = view.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_button_corner)
        this.view.roundCorners(cornerRadius, cornerRadius, cornerRadius, cornerRadius)

        this.titleTextView = this.view.findViewById(R.id.title)
        this.distanceTextView = this.view.findViewById(R.id.distanceSubtitle)
        this.durationTextView = this.view.findViewById(R.id.durationSubtitle)
        this.stateImageView = this.view.findViewById(R.id.icon)
        this.stateImageBackground = this.view.findViewById(R.id.iconBackground)
        this.stateImageBorder = this.view.findViewById(R.id.iconBorder)
        this.stateImageGroup = this.view.findViewById(R.id.iconGroup)
        this.container = this.view.findViewById(R.id.container)

        (this.stateImageBackground.background as GradientDrawable).setColor(DKColors.fontColorOnSecondaryColor)
        this.stateImageView.setColorFilter(DKColors.secondaryColor)
        (this.stateImageBorder.background as GradientDrawable).setStroke(1.5F.convertDpToPx(), R.color.dkTripRecordingButtonBorder.tintFromHueOfColor(view.context, com.drivequant.drivekit.common.ui.R.color.secondaryColor))
        this.distanceTextView.normalText()
        this.durationTextView.normalText()

        configure()
    }

    fun showConfirmationDialog() {
        if (this.viewModel.canShowTripStopConfirmationDialog()) {
            TripStopConfirmationView.show(context)
        }
    }

    private fun configure() {
        this.viewModel = ViewModelProvider(
            this,
            DKTripRecordingButtonViewModel.DKTripRecordingButtonViewModelFactory(
                DriveKitTripAnalysisUI.tripRecordingUserMode
            )
        )[DKTripRecordingButtonViewModel::class.java]
        this.viewModel.onViewModelUpdate = this::update
        update()
    }

    private fun update() {
        activity?.let {
            this.titleTextView.text = this.viewModel.title(it)
            if (this.viewModel.hasSubtitle) {
                this.titleTextView.headLine2()
                this.titleTextView.isAllCaps = false
                this.distanceTextView.text = this.viewModel.distanceSubtitle(it)
                this.durationTextView.text = this.viewModel.durationSubtitle()
                this.distanceTextView.visibility = View.VISIBLE
                this.durationTextView.visibility = View.VISIBLE
            } else {
                this.titleTextView.headLine1()
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
            this.view.visibility = if (this.viewModel.isHidden()) View.GONE else View.VISIBLE
            if (this.viewModel.canClick()) {
                this.view.setOnClickListener {
                    if (viewModel.toggleRecordingState()) {
                        showConfirmationDialog()
                    }
                }
            } else {
                this.view.setOnClickListener(null)
                this.view.isClickable = false
            }
        }
    }

}
