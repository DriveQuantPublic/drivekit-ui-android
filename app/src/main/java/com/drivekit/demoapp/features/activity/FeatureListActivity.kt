package com.drivekit.demoapp.features.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivekit.demoapp.features.adapter.FeatureListAdapter
import com.drivekit.demoapp.features.viewmodel.FeatureListViewModel
import com.drivekit.drivekitdemoapp.R
import kotlinx.android.synthetic.main.activity_feature_list.*

internal class FeatureListActivity : AppCompatActivity() {
    private lateinit var viewModel: FeatureListViewModel
    private lateinit var adapter: FeatureListAdapter

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_list)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar) // TODO in BaseActivity

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = getString(R.string.feature_list)

        if (!this::viewModel.isInitialized) {
            viewModel = FeatureListViewModel()
        }

        adapter = FeatureListAdapter(this, viewModel.features)
        features_list.layoutManager = LinearLayoutManager(this)
        features_list.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}