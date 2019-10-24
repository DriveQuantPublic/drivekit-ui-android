package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.drivequant.drivekit.databaseutils.entity.Safety

class SafetyViewModel(private val safety : Safety) : ViewModel() {

    fun getScore() = safety.safetyScore

    fun getAccelNumberEvent() = safety.nbAccel + safety.nbAccelCrit

    fun getBrakeNumberEvent() = safety.nbDecel + safety.nbDecelCrit

    fun getAdherenceNumberEvent() = safety.nbAdh + safety.nbAdhCrit

}

class SafetyViewModelFactory(private val safety : Safety) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SafetyViewModel(safety) as T
    }
}