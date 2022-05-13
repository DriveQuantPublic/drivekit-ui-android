package com.drivekit.demoapp.features.viewmodel

import androidx.lifecycle.ViewModel
import com.drivekit.demoapp.features.enum.FeatureType

internal class FeatureListViewModel : ViewModel() {

    val features = FeatureType.values().filterNot { it == FeatureType.ALL }
}