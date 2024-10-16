package com.drivekit.demoapp.settings.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.drivekit.demoapp.config.DriveKitConfig
import com.drivekit.demoapp.settings.viewmodel.DeleteAccountViewModel
import com.drivekit.demoapp.utils.restartApplication
import com.drivekit.drivekitdemoapp.R
import com.drivekit.drivekitdemoapp.databinding.ActivityDeleteAccountBinding
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager

internal class DeleteAccountActivity : AppCompatActivity() {

    private lateinit var viewModel: DeleteAccountViewModel
    private lateinit var binding: ActivityDeleteAccountBinding

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, DeleteAccountActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.root.findViewById(com.drivequant.drivekit.common.ui.R.id.dk_toolbar))
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setActivityTitle(getString(R.string.delete_account_header))
        checkViewModelInitialization()

        binding.buttonDeleteAccount.apply {
            normalText()
            setOnClickListener {
                displayAccountDeletionConfirmation()
            }
        }
        binding.buttonCancelAccountDeletion.setOnClickListener {
            finish()
        }

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
            DriveKitConfig.logout(this)
            restartApplication()
        }

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            update(binding.root) { view, insets ->
                addSystemStatusBarTopPadding(findViewById(R.id.toolbar), insets)
                addSystemNavigationBarBottomPadding(view, insets)
            }
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
        binding.progressCircular.visibility = if (displayProgress) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun displayAccountDeletionConfirmation() {
        val alert = DKAlertDialog.LayoutBuilder().init(this)
            .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
            .cancelable(false)
            .positiveButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_delete)) { _, _ -> deleteAccount() }
            .negativeButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_cancel)) { _, _ -> finish() }
            .show()

        val title = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
        val description = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)
        title?.text = getString(R.string.app_name)
        description?.text = getString(R.string.account_deletion_confirmation)
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(DKColors.criticalColor)
    }

    private fun displayAccountDeletionError(errorMessageResId: Int) {
        val alert = DKAlertDialog.LayoutBuilder().init(this)
            .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
            .cancelable(false)
            .positiveButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_ok)) { _, _ -> finish() }
            .show()

        val title = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
        val description = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)
        title?.text = getString(R.string.app_name)
        description?.text = getString(errorMessageResId)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
