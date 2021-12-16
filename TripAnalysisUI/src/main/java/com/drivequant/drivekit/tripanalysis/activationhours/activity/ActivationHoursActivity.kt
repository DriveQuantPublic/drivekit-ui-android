package com.drivequant.drivekit.tripanalysis.activationhours.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.tripanalysis.activationhours.adapter.ActivationHoursListAdapter
import com.drivequant.drivekit.tripanalysis.activationhours.viewmodel.ActivationHoursViewModel
import com.google.android.material.slider.LabelFormatter.LABEL_WITHIN_BOUNDS
import kotlinx.android.synthetic.main.dk_activation_hours_day_item.*
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
            setDescription(DKResource.convertToString(context, "dk_activation_hours_enable_description")
            )
        }
        switch_sorting.apply {
            setTitle(DKResource.convertToString(context, "dk_activation_hours_logbook_title"))
            setDescription(DKResource.convertToString(context, "dk_activation_hours_logbook_description"))
        }

        val linearLayoutManager = LinearLayoutManager(this)
        day_list.layoutManager = linearLayoutManager

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(ActivationHoursViewModel::class.java)
        }
        viewModel.fetchData()

        //TODO

        adapter = ActivationHoursListAdapter(this, viewModel)
        day_list.adapter = adapter
    }

    private fun setStyle() {
        view_separator_description?.setBackgroundColor(DriveKitUI.colors.complementaryFontColor())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}