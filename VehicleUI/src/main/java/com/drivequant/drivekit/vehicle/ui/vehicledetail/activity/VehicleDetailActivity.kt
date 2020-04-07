package com.drivequant.drivekit.vehicle.ui.vehicledetail.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.manager.VehicleManagerStatus.*
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicledetail.fragment.VehicleDetailFragment
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.VehicleDetailViewModel
import kotlinx.android.synthetic.main.activity_vehicle_picker.*

class VehicleDetailActivity : AppCompatActivity() {

    private lateinit var viewModel : VehicleDetailViewModel

    companion object {
        private const val VEHICLE_ID_EXTRA = "vehicleId-extra"
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA) as String

        viewModel = ViewModelProviders.of(this,
            VehicleDetailViewModel.VehicleDetailViewModelFactory(vehicleId)
        ).get(VehicleDetailViewModel::class.java)
        viewModel.init(this)

        viewModel.progressBarObserver.observe(this, Observer {
            it?.let { displayProgressCircular ->
                if (displayProgressCircular){
                    showProgressCircular()
                } else {
                    hideProgressCircular()
                }
            }
        })

        viewModel.renameObserver.observe(this, Observer {
            it?.let {status ->
                val message = when (status){
                    SUCCESS -> "dk_change_success"
                    else -> "dk_fields_not_valid"
                }
                Toast.makeText(this, DKResource.convertToString(this, message), Toast.LENGTH_SHORT).show()
            }
        })

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, VehicleDetailFragment.newInstance(viewModel, vehicleId), "vehicleDetailTag")
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.vehicle_menu_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save){
            val fragment = supportFragmentManager.findFragmentByTag("vehicleDetailTag")
            if (fragment is VehicleDetailFragment){
                fragment.updateInformations()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun hideProgressCircular() {
        progress_circular.animate()
            .alpha(0f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    progress_circular?.visibility = View.GONE
                }
            })
    }

    private fun showProgressCircular() {
        progress_circular.animate()
            .alpha(255f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    progress_circular?.visibility = View.VISIBLE
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentByTag("vehicleDetailTag")
        fragment?.onActivityResult(requestCode, resultCode, data)
    }
}