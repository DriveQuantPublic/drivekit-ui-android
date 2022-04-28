package com.drivekit.demoapp.onboarding.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.vehicle.ui.listener.VehiclePickerCompleteListener
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity
import kotlinx.android.synthetic.main.activity_vehicles.*

class VehiclesActivity : AppCompatActivity() {

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, VehiclesActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicles)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        title = getString(R.string.vehicle_intro_header)

        text_view_title.apply {
            text = DKSpannable().append(
                getString(R.string.vehicle_intro_title), resSpans {
                    color(DriveKitUI.colors.mainFontColor())
                    size(R.dimen.dk_text_medium)
                }).append(" ").append("ⓘ", context.resSpans {
                color(DriveKitUI.colors.secondaryColor())
                size(R.dimen.dk_text_medium)
            }).toSpannable()

            setOnClickListener {
                openDriveKitVehiclesDoc()
            }
        }
        text_view_description.text = getString(R.string.vehicle_intro_description)
        button_add_vehicle.apply {
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                VehiclePickerActivity.launchActivity(
                    this@VehiclesActivity,
                    null,
                    object : VehiclePickerCompleteListener {
                        override fun onVehiclePickerFinished(vehicleId: String) {
                            DashboardActivity.launchActivity(this@VehiclesActivity)
                        }
                    })
            }
        }
    }

    private fun openDriveKitVehiclesDoc() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.drivekit_doc_android_vehicle))
            )
        )
    }

    override fun onBackPressed() {
        //Do nothing
    }
}