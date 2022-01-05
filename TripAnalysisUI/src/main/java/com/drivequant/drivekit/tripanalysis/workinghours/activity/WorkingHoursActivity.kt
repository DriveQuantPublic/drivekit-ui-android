package com.drivequant.drivekit.tripanalysis.workinghours.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.tripanalysis.workinghours.view.WorkingHoursSpinnerSettings
import com.drivequant.drivekit.common.ui.component.SwitchSettings
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKDay
import com.drivequant.drivekit.tripanalysis.service.workinghours.TripStatus
import com.drivequant.drivekit.tripanalysis.workinghours.view.WorkingHoursDayCard
import com.drivequant.drivekit.tripanalysis.workinghours.viewmodel.WorkingHoursViewModel
import kotlinx.android.synthetic.main.dk_layout_activity_working_hours.*

class WorkingHoursActivity : AppCompatActivity() {

    private lateinit var viewModel: WorkingHoursViewModel
    private lateinit var insideHours: WorkingHoursSpinnerSettings
    private lateinit var outsideHours: WorkingHoursSpinnerSettings
    private val days: MutableList<WorkingHoursDayCard> = mutableListOf()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(this, "dk_tag_working_hours"), javaClass.simpleName
        )

        setContentView(R.layout.dk_layout_activity_working_hours)
        insideHours = findViewById(R.id.inside_hours_container)
        outsideHours = findViewById(R.id.outside_hours_container)
        setToolbar()
        setContent()
    }

    private fun setToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        title = DKResource.convertToString(this, "dk_working_hours_title")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setContent() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(WorkingHoursViewModel::class.java)
        }

        switch_enable.apply {
            setTitle(DKResource.convertToString(context, "dk_working_hours_enable_title"))
            setDescription(DKResource.convertToString(context, "dk_working_hours_enable_desc"))
        }

        insideHours.setTitle(DKResource.convertToString(this, "dk_working_hours_slot_inside_title"))
        insideHours.setListener(object : WorkingHoursSpinnerSettings.SpinnerListener {
            override fun onItemSelected(item: TripStatus) {
                viewModel.dataChanged = true
            }
        })

        outsideHours.setTitle(DKResource.convertToString(this, "dk_working_hours_slot_outside_title"))
        outsideHours.setListener(object : WorkingHoursSpinnerSettings.SpinnerListener {
            override fun onItemSelected(item: TripStatus) {
                viewModel.dataChanged = true
            }
        })

        viewModel.syncDataStatus.observe(this, { success ->
            if (!success) {
                if (applicationContext != null) {
                    Toast.makeText(
                        applicationContext,
                        DKResource.convertToString(this, "dk_working_hours_sync_failed"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            viewModel.config?.let {
                switch_enable.apply {
                    setChecked(it.enable)
                    setListener(object : SwitchSettings.SwitchListener {
                        override fun onSwitchChanged(isChecked: Boolean) {
                            manageDaysVisibility(isChecked)
                            viewModel.dataChanged = true
                        }
                    })
                }
                manageDaysVisibility(switch_enable.isChecked())
                insideHours.selectItem(it.insideHours)
                outsideHours.selectItem(it.outsideHours)
                configureDays()
            }
        })
        viewModel.updateDataStatus.observe(this, { success ->
            val toastMessage = if (success) {
                "dk_working_hours_update_succeed"
            } else {
                "dk_working_hours_update_failed"
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

    private fun configureDays() {
        DKDay.values().forEachIndexed { index, dkDay ->
            // TODO might not work first time when user has no config yet
            viewModel.config?.dayConfiguration?.get(index)?.let {
                val day = WorkingHoursDayCard(this, it)
                days.add(day)
                day.setListener(object : WorkingHoursDayCard.WorkingHoursDayListener {
                    override fun onDayChecked(checked: Boolean) {
                        viewModel.dataChanged = true
                    }

                    override fun onHoursUpdated(start: Float, end: Float) {
                        viewModel.dataChanged = true
                    }
                })
                days_container.addView(
                    day,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    private fun manageDaysVisibility(isEnabledChecked: Boolean) {
        if (isEnabledChecked) {
            scrollview.visibility = View.VISIBLE
        } else {
            scrollview.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.dataChanged) {
            // TODO show alertdialog
            viewModel.updateConfig(
                switch_enable.isChecked(),
                insideHours.getSelectedTripStatus(),
                outsideHours.getSelectedTripStatus(),
                days.map { it.getConfig() }
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}