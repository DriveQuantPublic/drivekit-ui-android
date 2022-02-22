package com.drivequant.drivekit.tripanalysis.crashfeedback.activity

import android.app.KeyguardManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.utils.DKResource

open class BaseCrashFeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar()

    }

    private fun setToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        title = "Titre"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    fun dismissKeyguard() {
        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager?.requestDismissKeyguard(this, null)
        }
    }

    fun launchPhoneCall() {
        val intentTel = Intent(Intent.ACTION_VIEW)
        val dataTel = Uri.parse("tel:+33600112233") // TODO
        intentTel.data = dataTel
        startActivity(intentTel)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}