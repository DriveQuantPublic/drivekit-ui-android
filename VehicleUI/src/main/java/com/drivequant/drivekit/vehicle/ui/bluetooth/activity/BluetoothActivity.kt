package com.drivequant.drivekit.vehicle.ui.bluetooth.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.bluetooth.viewmodel.BluetoothViewModel

class BluetoothActivity : AppCompatActivity() {

    private lateinit var viewModel : BluetoothViewModel

    companion object {
        private const val VEHICLE_ID_EXTRA = "vehicleId-extra"

        fun launchActivity(context: Context, vehicleId: String) {
            val intent = Intent(context, BluetoothActivity::class.java)
            intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.setBackgroundDrawable(ColorDrawable(DriveKitUI.colors.primaryColor()))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel = ViewModelProviders.of(this, BluetoothViewModel.BluetoothViewModelFactory("")).get(BluetoothViewModel::class.java)

        viewModel.fragmentDispatcher.observe(this, Observer {
            val fragment = it
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, it)
                    .commit()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}