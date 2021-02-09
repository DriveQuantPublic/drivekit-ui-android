package com.drivequant.drivekit.vehicle.ui.bluetooth.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.bluetooth.viewmodel.BluetoothViewModel
import kotlinx.android.synthetic.main.activity_bluetooth.*

class BluetoothActivity : AppCompatActivity() {
    private lateinit var viewModel : BluetoothViewModel
    private lateinit var vehicleId: String
    private lateinit var vehicleName: String

    companion object {
        private const val VEHICLE_ID_EXTRA = "vehicleId-extra"
        private const val VEHICLE_NAME_EXTRA = "vehicleName-extra"

        fun launchActivity(context: Context, vehicleId: String, vehicleName: String) {
            val intent = Intent(context, BluetoothActivity::class.java)
            intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
            intent.putExtra(VEHICLE_NAME_EXTRA, vehicleName)
            context.startActivity(intent)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(this, "dk_tag_vehicles_bluetooth_add"), javaClass.simpleName)

        setContentView(R.layout.activity_bluetooth)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(DriveKitUI.colors.primaryColor()))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA) as String
        vehicleName = intent.getStringExtra(VEHICLE_NAME_EXTRA) as String

        viewModel = ViewModelProviders.of(this, BluetoothViewModel.BluetoothViewModelFactory(vehicleId, vehicleName)).get(BluetoothViewModel::class.java)

        viewModel.fragmentDispatcher.observe(this, Observer { fragment ->
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_left, R.animator.slide_out_right)
                    .addToBackStack(fragment.javaClass.name)
                    .add(R.id.container, it)
                    .commit()
            }
        })

        viewModel.progressBarObserver.observe(this, Observer {
            it?.let {displayProgressCircular ->
                if (displayProgressCircular){
                    showProgressCircular()
                } else {
                    hideProgressCircular()
                }
            }
        })
        updateTitle(DKResource.convertToString(this, "dk_vehicle_bluetooth_combination_view_title"))
    }

    private fun hideProgressCircular() {
        dk_progress_circular.animate()
            .alpha(0f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dk_progress_circular?.visibility = View.GONE
                }
            })
    }

    private fun showProgressCircular() {
        dk_progress_circular.animate()
            .alpha(255f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dk_progress_circular?.visibility = View.VISIBLE
                }
            })
    }

    private fun updateTitle(title: String){
        this.title = title
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1){
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}