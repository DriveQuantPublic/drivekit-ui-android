package com.drivequant.drivekit.vehicle.ui.vehicledetail.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.databinding.ActivityVehicleDetailBinding
import com.drivequant.drivekit.vehicle.ui.vehicledetail.fragment.VehicleDetailFragment
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.VehicleDetailViewModel

class VehicleDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: VehicleDetailViewModel
    private lateinit var binding: ActivityVehicleDetailBinding

    companion object {
        private const val VEHICLE_ID_EXTRA = "vehicleId-extra"
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(binding.dkToolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA) as String

        viewModel = ViewModelProvider(this,
            VehicleDetailViewModel.VehicleDetailViewModelFactory(vehicleId)
        )[VehicleDetailViewModel::class.java]
        viewModel.init(this)

        viewModel.progressBarObserver.observe(this) {
            it?.let { displayProgressCircular ->
                if (displayProgressCircular) {
                    showProgressCircular()
                } else {
                    hideProgressCircular()
                }
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, VehicleDetailFragment.newInstance(viewModel, vehicleId), "vehicleDetailTag")
            .commit()
    }

    private fun hideProgressCircular() {
        binding.dkProgressCircular.dkProgressCircular.apply {
            animate()
                .alpha(0f)
                .setDuration(200L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.GONE
                    }
                })
        }
    }

    private fun showProgressCircular() {
        binding.dkProgressCircular.dkProgressCircular.apply {
            animate()
                .alpha(1f)
                .setDuration(200L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.VISIBLE
                    }
                })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag("vehicleDetailTag")
        if (fragment is VehicleDetailFragment){
            fragment.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragment = supportFragmentManager.findFragmentByTag("vehicleDetailTag")
        fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentByTag("vehicleDetailTag")
        fragment?.onActivityResult(requestCode, resultCode, data)
    }
}
