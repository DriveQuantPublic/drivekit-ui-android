package com.drivequant.drivekit.tripanalysis.workinghours.activity

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
import com.drivequant.drivekit.tripanalysis.workinghours.view.SpinnerSettings
import com.drivequant.drivekit.common.ui.component.SwitchSettings
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.tripanalysis.service.workinghours.TripStatus
import com.drivequant.drivekit.tripanalysis.workinghours.adapter.WorkingHoursListAdapter
import com.drivequant.drivekit.tripanalysis.workinghours.viewmodel.WorkingHoursViewModel
import kotlinx.android.synthetic.main.dk_activity_working_hours.*


class WorkingHoursActivity : AppCompatActivity() {

    private lateinit var viewModel: WorkingHoursViewModel
    private lateinit var insideHours: SpinnerSettings
    private lateinit var outsideHours: SpinnerSettings
    private var adapter: WorkingHoursListAdapter? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(this, "dk_tag_working_hours"), javaClass.simpleName
        )

        setContentView(R.layout.dk_activity_working_hours)
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
            setListener(object : SwitchSettings.SwitchListener {
                override fun onSwitchChanged(isChecked: Boolean) {
                    manageDaysVisibility(isChecked)
                }
            })
        }

        insideHours.setTitle(DKResource.convertToString(this, "dk_working_hours_slot_inside_title"))
        insideHours.setListener(object : SpinnerSettings.SpinnerListener {
            override fun onItemSelected(item: TripStatus) {
                // TODO update viewmodel ?
            }
        })

        outsideHours.setTitle(DKResource.convertToString(this, "dk_working_hours_slot_outside_title"))
        outsideHours.setListener(object : SpinnerSettings.SpinnerListener {
            override fun onItemSelected(item: TripStatus) {
                // TODO update viewmodel ?
            }
        })

        day_list.layoutManager = LinearLayoutManager(this)

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
                switch_enable.setChecked(it.enable)
                insideHours.selectItem(it.insideHours)
                outsideHours.selectItem(it.outsideHours)
                manageDaysVisibility(switch_enable.isChecked())
                adapter = WorkingHoursListAdapter(this, viewModel)
                day_list.adapter = adapter
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

    private fun manageDaysVisibility(isEnabledChecked: Boolean) {
        if (isEnabledChecked) {
            scrollview.visibility = View.VISIBLE
        } else {
            scrollview.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        // TODO MOCK
        viewModel.updateConfig(
            switch_enable.isChecked(),
            insideHours.getSelectedTripStatus(),
            outsideHours.getSelectedTripStatus()
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}