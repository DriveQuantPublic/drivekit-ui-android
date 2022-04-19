package com.drivekit.demoapp.featurelist.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivekit.drivekitdemoapp.R

internal class FeatureListActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_feature_list)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar) // TODO in BaseActivity
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = getString(R.string.feature_list)

    }

    override fun onResume() {
        super.onResume()
        // TODO check viewmodel isInitialized

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}