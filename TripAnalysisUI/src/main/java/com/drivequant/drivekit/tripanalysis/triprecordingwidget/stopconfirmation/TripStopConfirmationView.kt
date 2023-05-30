package com.drivequant.drivekit.tripanalysis.triprecordingwidget.stopconfirmation

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import java.lang.ref.WeakReference

internal class TripStopConfirmationView(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {

    companion object {
        fun show(context: Context?) {
            if (context != null) {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(context)
                    .layout(R.layout.dk_tripanalysis_trip_recording_button_stop_confirmation)
                    .cancelable(false)
                    .show()
                val viewModel = TripStopConfirmationViewModel()
                val tripStopConfirmationView = alertDialog.findViewById<TripStopConfirmationView>(R.id.tripStopConfirmationView)
                tripStopConfirmationView?.configure(alertDialog, viewModel)
            }
        }
    }

    private var alertDialog: WeakReference<AlertDialog>? = null
    private lateinit var endTripButton: TripStopButton
    private lateinit var continueTripButton: TripStopButton
    private lateinit var cancelTripButton: TripStopButton

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.endTripButton = findViewById(R.id.endTrip)
        this.continueTripButton = findViewById(R.id.continueTrip)
        this.cancelTripButton = findViewById(R.id.cancelTrip)
    }

    fun configure(alertDialog: AlertDialog, viewModel: TripStopConfirmationViewModel) {
        this.alertDialog = WeakReference(alertDialog)
        this.endTripButton.configure(viewModel.endTripTitleId, viewModel.endTripSubtitleId)
        this.continueTripButton.configure(viewModel.continueTripTitleId, viewModel.continueTripSubtitleId)
        this.cancelTripButton.configure(viewModel.cancelTripTitleId, viewModel.cancelTripSubtitleId)

        this.endTripButton.setOnClickListener {
            viewModel.stopTrip()
            dismiss()
        }
        this.continueTripButton.setOnClickListener {
            dismiss()
        }
        this.cancelTripButton.setOnClickListener {
            viewModel.cancelTrip()
            dismiss()
        }
    }

    private fun dismiss() = this.alertDialog?.get()?.dismiss()

}
