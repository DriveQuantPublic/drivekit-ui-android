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
        setStyle()
    }

    private fun setToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        title = DKResource.convertToString(this, "dk_activation_hours_title")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setContent() {
        switch_enable.apply {
            setTitle(DKResource.convertToString(context, "dk_activation_hours_enable_title"))
            setDescription(DKResource.convertToString(context, "dk_activation_hours_enable_description"))
            setListener(object : SwitchSettings.SwitchListener {
                override fun onSwitchChanged(isChecked: Boolean) {
                    manageEnableSwitchVisibility(isChecked)
                }
            })
        }
        switch_sorting.apply {
            setTitle(DKResource.convertToString(context, "dk_activation_hours_logbook_title"))
            setDescription(DKResource.convertToString(context, "dk_activation_hours_logbook_description"))
        }

        day_list.layoutManager = LinearLayoutManager(this)

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(ActivationHoursViewModel::class.java)
        }

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
                manageEnableSwitchVisibility(switch_enable.isChecked())
                switch_sorting.setChecked(it.outsideHours)
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
        viewModel.fetchData()
    }

    private fun manageEnableSwitchVisibility(isChecked: Boolean) {
        if (!isChecked) {
            day_list.visibility = View.GONE
        } else {
            day_list.visibility = View.VISIBLE
        }
    }

    private fun setStyle() {
        view_separator_description?.setBackgroundColor(DriveKitUI.colors.complementaryFontColor())
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