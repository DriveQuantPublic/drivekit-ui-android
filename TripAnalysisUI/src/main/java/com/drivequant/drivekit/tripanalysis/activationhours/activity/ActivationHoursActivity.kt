package com.drivequant.drivekit.tripanalysis.activationhours.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.SwitchSettings
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.tripanalysis.activationhours.adapter.ActivationHoursListAdapter
import com.drivequant.drivekit.tripanalysis.activationhours.viewmodel.ActivationHoursViewModel
import kotlinx.android.synthetic.main.dk_activity_activation_hours.*


class ActivationHoursActivity : AppCompatActivity() {
    // TODO manage when activity process is killed

    private lateinit var viewModel: ActivationHoursViewModel
    private var adapter: ActivationHoursListAdapter? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(this, "dk_tag_activations_hours"), javaClass.simpleName
        )

        setContentView(R.layout.dk_activity_activation_hours)
        setToolbar()
        setContent()
    }

    private fun setToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        title = DKResource.convertToString(this, "dk_activation_hours_title")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setContent() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(ActivationHoursViewModel::class.java)
        }

        switch_enable.apply {
            setTitle(DKResource.convertToString(context, "dk_activation_hours_enable_title"))
            setDescription(DKResource.convertToString(context, "dk_activation_hours_enable_description"))
            setListener(object : SwitchSettings.SwitchListener {
                override fun onSwitchChanged(isChecked: Boolean) {
                    manageDaysVisibility(isChecked, switch_sorting.isChecked())
                }
            })
        }

        if (viewModel.displayLogbook()) {
            switch_sorting.apply {
                visibility = View.VISIBLE
                setTitle(DKResource.convertToString(context, "dk_activation_hours_logbook_title"))
                setDescription(DKResource.convertToString(context, "dk_activation_hours_logbook_description"))
                setListener(object : SwitchSettings.SwitchListener {
                    override fun onSwitchChanged(isChecked: Boolean) {
                        manageDaysVisibility(switch_enable.isChecked(), isChecked)
                    }
                })
            }
            view_separator_before_logbook?.apply {
                setBackgroundColor(DriveKitUI.colors.neutralColor())
                visibility = View.VISIBLE
            }
            view_separator_after_logbook?.apply {
                setBackgroundColor(DriveKitUI.colors.neutralColor())
                visibility = View.VISIBLE
            }
        } else {
            switch_sorting.visibility = View.GONE
        }

        day_list.layoutManager = LinearLayoutManager(this)

        viewModel.syncDataStatus.observe(this, { success ->
            if (!success) {
                if (applicationContext != null) {
                    Toast.makeText(
                        applicationContext,
                        DKResource.convertToString(this, "dk_activation_hours_sync_failed"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            viewModel.config?.let {
                switch_enable.setChecked(it.enable)
                switch_sorting.setChecked(it.outsideHours)
                manageDaysVisibility(switch_enable.isChecked(), switch_sorting.isChecked())
                adapter = ActivationHoursListAdapter(this, viewModel)
                day_list.adapter = adapter
            }
        })
        viewModel.updateDataStatus.observe(this, { success ->
            val toastMessage = if (success) {
                "dk_activation_hours_update_succeed"
            } else {
                "dk_activation_hours_update_failed"
            }
            if (applicationContext != null) {
                Toast.makeText(
                    applicationContext,
                    DKResource.convertToString(this, toastMessage),
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
        viewModel.synchronizeData()
    }

    private fun manageDaysVisibility(isEnabledChecked: Boolean, isLogbookSortingEnabled: Boolean) {
        if (isEnabledChecked || (isLogbookSortingEnabled && viewModel.displayLogbook())) {
            day_list.visibility = View.VISIBLE
        } else {
            day_list.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateConfig(switch_enable.isChecked(), switch_sorting.isChecked())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}