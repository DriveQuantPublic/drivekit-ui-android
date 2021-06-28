package com.drivekit.demoapp.activity

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.drivekit.drivekitdemoapp.R
import com.drivequant.beaconutils.BeaconData
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.vehicle.DriveKitVehicle


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings,
                SettingsFragment()
            )
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            initPreferences()
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
            updateUserIdSummary()
        }

        override fun onPause() {
            super.onPause()
            preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
            when(key){
                "stop_timeout" -> DriveKitTripAnalysis.setStopTimeOut(sharedPreferences.getInt(key, 4) * 60)
                "beacon_required" -> DriveKitTripAnalysis.setBeaconRequired(sharedPreferences.getBoolean(key, false))
                "enable_share_position" -> DriveKitTripAnalysis.enableSharePosition(sharedPreferences.getBoolean(key, false))
                "sandbox_mode" -> DriveKit.enableSandboxMode(sharedPreferences.getBoolean(key, false))
                "user_id" -> {
                    reconfigureDriveKit(sharedPreferences.getString(key, "")!!)
                    updateUserIdSummary()
                }
                "autostart" -> DriveKitTripAnalysis.activateAutoStart(sharedPreferences.getBoolean(key, false))
                "add_beacon" -> manageBeacon(sharedPreferences.getBoolean(key, false))
            }
        }

        @SuppressLint("ApplySharedPref")
        private fun initPreferences(){
            preferenceManager.sharedPreferences.edit().putBoolean("autostart", DriveKitTripAnalysis.getConfig().autostart).commit()
            preferenceManager.sharedPreferences.edit().putInt("stop_timeout", DriveKitTripAnalysis.getConfig().stopTimeOut/60).commit()
        }

        private fun manageBeacon(addBeacon: Boolean){
            if (addBeacon)
                DriveKitTripAnalysis.setBeacons(listOf(BeaconData("699ebc80-e1f3-11e3-9a0f-0cf3ee3bc012", -1, -1)))
            else
                DriveKitTripAnalysis.setBeacons(listOf())
        }

        private fun reconfigureDriveKit(userId: String) {
            val apiKey = DriveKit.config.apiKey
            DriveKit.reset()
            DriveKitDriverData.reset()
            DriveKitVehicle.reset()
            DriveKitDriverAchievement.reset()
            DriveKitChallenge.reset()
            apiKey?.let {
                DriveKit.setApiKey(it)
            }
            DriveKit.setUserId(userId)
        }

        private fun updateUserIdSummary(){
            findPreference("user_id")?.let {
                val userId = preferenceManager.sharedPreferences.getString("user_id", "")!!
                if (userId.isBlank()) {
                    it.summary = getString(R.string.user_id_description)
                } else {
                    it.summary = userId
                }
            }
        }
    }
}