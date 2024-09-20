package com.drivekit.demoapp.features.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drivekit.demoapp.features.adapter.FeatureListAdapter
import com.drivekit.demoapp.features.viewmodel.FeatureListViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager

@SuppressLint("SourceLockedOrientationActivity")
internal class FeatureListActivity : AppCompatActivity() {
    private lateinit var viewModel: FeatureListViewModel
    private lateinit var adapter: FeatureListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_feature_list)

        setSupportActionBar(findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar))
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setActivityTitle(getString(R.string.feature_list))

        if (!this::viewModel.isInitialized) {
            viewModel = FeatureListViewModel()
        }

        adapter = FeatureListAdapter(this, viewModel.features)
        val featureList = findViewById<RecyclerView>(R.id.features_list)
        featureList.let {
            it.layoutManager = LinearLayoutManager(this@FeatureListActivity)
            it.adapter = adapter
        }

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundDarkColor(window)
            addSystemStatusBarTopPadding(findViewById(com.drivequant.drivekit.ui.R.id.toolbar))
            addSystemNavigationBarBottomMargin(featureList)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
