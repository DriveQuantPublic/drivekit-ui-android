package com.drivekit.demoapp.splashscreen.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.drivekit.demoapp.manager.*

@SuppressLint("CustomSplashScreen")
class SplashScreenViewModel {

    val syncFinished: MutableLiveData<Any> = MutableLiveData()

    fun syncDriveKitModules() {
        SyncModuleManager.syncModules(
            mutableListOf(
                DKModule.TRIPS,
                DKModule.CHALLENGE,
                DKModule.USER_INFO,
                DKModule.VEHICLE), listener = object : ModulesSyncListener {
                override fun onModulesSyncResult(results: MutableList<SyncStatus>) {
                    syncFinished.postValue(Any())
                }
            }
        )
    }
}