package com.drivequant.drivekit.vehicle.ui.vehicledetail.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicledetail.fragment.VehicleDetailFragment

class VehicleDetailActivity : AppCompatActivity() {

    companion object {
        private const val VEHICLEID_EXTRA = "vehicleId-extra"

        fun launchActivity(context: Context, vehicleId: String) {
            val intent = Intent(context, VehicleDetailActivity::class.java)
            intent.putExtra(VEHICLEID_EXTRA, vehicleId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val vehicleId = intent.getStringExtra(VEHICLEID_EXTRA) as String
        val fragment = VehicleDetailFragment.newInstance(vehicleId)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, "vehicleDetail")
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentByTag("vehicleDetail")
        fragment?.let {
            it.onActivityResult(requestCode, resultCode, intent)
        }
    }
}