package com.drivequant.drivekit.tripanalysis.workinghours.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.tripanalysis.workinghours.view.WorkingHoursSpinnerSettings
import com.drivequant.drivekit.common.ui.component.SwitchSettings
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKDay
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKWorkingHoursTimeSlotStatus
import com.drivequant.drivekit.tripanalysis.workinghours.view.WorkingHoursDayCard
import com.drivequant.drivekit.tripanalysis.workinghours.viewmodel.WorkingHoursViewModel
import kotlinx.android.synthetic.main.dk_layout_activity_working_hours.*

class WorkingHoursActivity : AppCompatActivity() {

    private lateinit var viewModel: WorkingHoursViewModel
    private lateinit var insideHours: WorkingHoursSpinnerSettings
    private lateinit var outsideHours: WorkingHoursSpinnerSettings
    private lateinit var menu: Menu
    private val days: MutableList<WorkingHoursDayCard> = mutableListOf()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(this, "dk_tag_trip_analysis_working_hours"), javaClass.simpleName
        )

        setContentView(R.layout.dk_layout_activity_working_hours)
        insideHours = findViewById(R.id.inside_hours_container)
        outsideHours = findViewById(R.id.outside_hours_container)
        setToolbar()
        setContent()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dk_working_hours_menu_bar, menu)
        menu?.let {
            this.menu = it
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save){
            updateConfig(false)
        }
        return super.onOptionsItemSelected(item)
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

        separator_enable.setBackgroundColor(DriveKitUI.colors.neutralColor())
        separator_inside.setBackgroundColor(DriveKitUI.colors.neutralColor())
        separator_outside.setBackgroundColor(DriveKitUI.colors.neutralColor())

        switch_enable.apply {
            isEnabled = false
            setTitle(DKResource.convertToString(context, "dk_working_hours_enable_title"))
            setDescription(DKResource.convertToString(context, "dk_working_hours_enable_desc"))
        }

        insideHours.setTitle(DKResource.convertToString(this, "dk_working_hours_slot_inside_title"))
        insideHours.setListener(object : WorkingHoursSpinnerSettings.SpinnerListener {
            override fun onItemSelected(item: DKWorkingHoursTimeSlotStatus) {
                dataUpdated(true)
            }
        })

        outsideHours.setTitle(DKResource.convertToString(this, "dk_working_hours_slot_outside_title"))
        outsideHours.setListener(object : WorkingHoursSpinnerSettings.SpinnerListener {
            override fun onItemSelected(item: DKWorkingHoursTimeSlotStatus) {
                dataUpdated(true)
            }
        })

        viewModel.syncDataStatus.observe(this, { success ->
            if (!success) {
                Toast.makeText(
                    this,
                    DKResource.convertToString(this, "dk_working_hours_sync_failed"),
                    Toast.LENGTH_SHORT
                ).show()
            }
            viewModel.config?.let {
                if (viewModel.dataChanged) {
                    dataUpdated(true)
                }
                switch_enable.apply {
                    isEnabled = true
                    setChecked(it.enable)
                    setListener(object : SwitchSettings.SwitchListener {
                        override fun onSwitchChanged(isChecked: Boolean) {
                            manageLabelsVisibility()
                            dataUpdated(true)
                        }
                    })
                }
                manageLabelsVisibility()
                insideHours.selectItem(it.insideHours)
                outsideHours.selectItem(it.outsideHours)
                configureDays()
            }
            updateProgressVisibility(false)
        })
        viewModel.updateDataStatus.observe(this, { response ->
            if (response.status) {
                dataUpdated(false)
            }
            updateProgressVisibility(false)
            val toastMessage = if (response.status) {
                "dk_working_hours_update_succeed"
            } else {
                "dk_working_hours_update_failed"
            }
            Toast.makeText(this, DKResource.convertToString(this, toastMessage), Toast.LENGTH_SHORT).show()
            if (response.fromBackButton && response.status) {
                finish()
            }
        })
        updateProgressVisibility(true)
        viewModel.synchronizeData()
    }

    private fun configureDays() {
        DKDay.values().forEachIndexed { index, _ ->
            viewModel.config?.dayConfiguration?.get(index)?.let {
                val day = WorkingHoursDayCard(this, it)
                days.add(day)
                day.setListener(object : WorkingHoursDayCard.WorkingHoursDayListener {
                    override fun onDayChecked(checked: Boolean) {
                        dataUpdated(true)
                    }

                    override fun onHoursUpdated(start: Double, end: Double) {
                        dataUpdated(true)
                    }
                })
                days_container.addView(
                    day,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    private fun manageLabelsVisibility() {
        if (switch_enable.isChecked()) {
            scrollview.visibility = View.VISIBLE
        } else {
            scrollview.visibility = View.GONE
        }
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

    private fun dataUpdated(changed: Boolean) {
        viewModel.dataChanged = changed
        menu.findItem(R.id.action_save)?.isVisible = changed
    }

    override fun onBackPressed() {
        if (viewModel.dataChanged) {
            val alert = DKAlertDialog.LayoutBuilder().init(this)
                .layout(R.layout.template_alert_dialog_layout)
                .cancelable(false)
                .positiveButton(getString(R.string.dk_common_confirm)) { _, _ ->
                    updateConfig(true)
                }
                .negativeButton(negativeListener = { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    super.onBackPressed()
                })
                .show()
            val title = alert.findViewById<TextView>(R.id.text_view_alert_title)
            val description = alert.findViewById<TextView>(R.id.text_view_alert_description)
            title?.text = getString(R.string.app_name)
            description?.text = DKResource.convertToString(this, "dk_working_hours_back_save_alert")
        } else {
            super.onBackPressed()
        }
    }

    private fun updateConfig(onBackPressed: Boolean) {
        updateProgressVisibility(true)
        viewModel.updateConfig(
            switch_enable.isChecked(),
            insideHours.getSelectedTimeSlotStatus(),
            outsideHours.getSelectedTimeSlotStatus(),
            days.map { it.getConfig() },
            onBackPressed
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}