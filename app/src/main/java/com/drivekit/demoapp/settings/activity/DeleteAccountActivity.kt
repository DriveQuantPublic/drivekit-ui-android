package com.drivekit.demoapp.settings.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.drivekit.demoapp.settings.viewmodel.DeleteAccountViewModel
import com.drivekit.demoapp.utils.restartApplication
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import kotlinx.android.synthetic.main.activity_delete_account.*

internal class DeleteAccountActivity : AppCompatActivity() {

    private lateinit var viewModel: DeleteAccountViewModel

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, DeleteAccountActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title =  getString(R.string.delete_account_header)
        checkViewModelInitialization()

        button_delete_account.setOnClickListener {
            displayAccountDeletionConfirmation()
        }
        button_cancel_account_deletion.setOnClickListener {
            finish()
        }

        text_view_explaining_text.setTextColor(DriveKitUI.colors.mainFontColor())
        text_view_warning_text.setTextColor(DriveKitUI.colors.mainFontColor())

        viewModel.accountDeletionError.observe(this) {
            updateProgressVisibility(false)
            Toast.makeText(this@DeleteAccountActivity, getString(it), Toast.LENGTH_SHORT).show()
        }
        viewModel.accountDeletionForbidden.observe(this) {
            updateProgressVisibility(false)
            displayAccountDeletionError(it)
        }
        viewModel.accountDeleted.observe(this) {
            updateProgressVisibility(false)
            restartApplication()
        }
    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this)[DeleteAccountViewModel::class.java]
        }
    }

    override fun onResume() {
        super.onResume()
        checkViewModelInitialization()
    }

    private fun deleteAccount() {
        updateProgressVisibility(true)
        viewModel.deleteAccount()
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        progress_circular?.apply {
            visibility = if (displayProgress) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun displayAccountDeletionConfirmation() {
        val alert = DKAlertDialog.LayoutBuilder().init(this)
            .layout(R.layout.template_alert_dialog_layout)
            .cancelable(false)
            .positiveButton(getString(R.string.dk_common_delete)) { _, _ -> deleteAccount() }
            .negativeButton(getString(R.string.dk_common_cancel)) { _, _ -> finish() }
            .show()

        val title = alert.findViewById<TextView>(R.id.text_view_alert_title)
        val description = alert.findViewById<TextView>(R.id.text_view_alert_description)
        title?.text = getString(R.string.app_name)
        description?.text = getString(R.string.account_deletion_confirmation)
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(DriveKitUI.colors.criticalColor())
    }

    private fun displayAccountDeletionError(errorMessageResId: Int) {
        val alert = DKAlertDialog.LayoutBuilder().init(this)
            .layout(R.layout.template_alert_dialog_layout)
            .cancelable(false)
            .positiveButton(getString(R.string.dk_common_ok)) { _, _ -> finish() }
            .show()

        val title = alert.findViewById<TextView>(R.id.text_view_alert_title)
        val description = alert.findViewById<TextView>(R.id.text_view_alert_description)
        title?.text = getString(R.string.app_name)
        description?.text = getString(errorMessageResId)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
