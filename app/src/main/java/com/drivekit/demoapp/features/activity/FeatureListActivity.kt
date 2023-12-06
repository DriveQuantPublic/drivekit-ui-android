package com.drivekit.demoapp.features.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drivekit.demoapp.features.adapter.FeatureListAdapter
import com.drivekit.demoapp.features.viewmodel.FeatureListViewModel
import com.drivekit.drivekitdemoapp.R

@SuppressLint("SourceLockedOrientationActivity")
internal class FeatureListActivity : AppCompatActivity() {
    private lateinit var viewModel: FeatureListViewModel
    private lateinit var adapter: FeatureListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_feature_list)

        val toolbar = findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.feature_list)

        if (!this::viewModel.isInitialized) {
            viewModel = FeatureListViewModel()
        }

        adapter = FeatureListAdapter(this, viewModel.features)
        findViewById<RecyclerView>(R.id.features_list).let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
