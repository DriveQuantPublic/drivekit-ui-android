package com.drivekit.demoapp.onboarding.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.demoapp.utils.addInfoIconAtTheEnd
import com.drivekit.drivekitdemoapp.R
import com.drivekit.drivekitdemoapp.databinding.ActivityVehiclesBinding
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.vehicle.ui.listener.VehiclePickerCompleteListener
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity

internal class VehiclesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVehiclesBinding

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, VehiclesActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehiclesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.root.findViewById(com.drivequant.drivekit.common.ui.R.id.dk_toolbar))
        title = getString(R.string.vehicle_intro_header)

        binding.textViewTitle.apply {
            text = getString(R.string.vehicle_intro_title)
            headLine1()
            this.addInfoIconAtTheEnd(this@VehiclesActivity)

            setOnClickListener {
                openDriveKitVehiclesDoc()
            }
        }
        binding.textViewDescription.apply {
            text = getString(R.string.vehicle_intro_description)
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
        binding.buttonAddVehicle.apply {
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
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.drivekit_doc_android_vehicle))))
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onBackPressed() {
        //Do nothing
    }
}
