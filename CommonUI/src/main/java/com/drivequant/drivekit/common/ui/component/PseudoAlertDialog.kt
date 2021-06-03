package com.drivequant.drivekit.common.ui.component

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource

class PseudoAlertDialog {

    fun show(context: Context, listener: PseudoChangedListener) {
        // TODO check locally if has pseudo
        var progressBar: ProgressBar? = null
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(context)
            .layout(R.layout.dk_alert_dialog_pseudo)
            .positiveButton(DKResource.convertToString(context, "dk_common_validate"), DialogInterface.OnClickListener { dialogInterface, _ ->
                // TODO loading
                // call webservicee
                // if ok
                //    dialogInterface.dismiss()
                //    hideProgressCircular
                // else
                //    show toast error
                showProgressCircular(progressBar)
            })
            .negativeButton(negativeListener = DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
                hideProgressCircular(progressBar)
                listener.onCancelled()
            })
            .cancelable(false)
            .show()

        val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView =
            alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
        progressBar = alertDialog.findViewById(R.id.dk_progress_circular)

        titleTextView?.text = DKResource.convertToString(context, "app_name")
        descriptionTextView?.text = DKResource.convertToString(context, "dk_common_no_pseudo_set")

        titleTextView?.headLine1()
        descriptionTextView?.normalText()
    }

    private fun hideProgressCircular(progressBar: ProgressBar?) {
        progressBar?.let {
            progressBar.animate()
                .alpha(0f)
                .setDuration(200L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        progressBar.visibility = View.GONE
                    }
                })
        }
    }

    private fun showProgressCircular(progressBar: ProgressBar?) {
        progressBar?.let {
            progressBar.animate()
                .alpha(255f)
                .setDuration(200L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        progressBar.visibility = View.VISIBLE
                    }
                })
        }
    }
}

interface PseudoChangedListener {
    fun onPseudoChanged(success: Boolean)
    fun onCancelled()
}