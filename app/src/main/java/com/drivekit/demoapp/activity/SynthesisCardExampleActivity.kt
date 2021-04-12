package com.drivekit.demoapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.SynthesisCardsViewListener
import com.drivequant.drivekit.ui.synthesiscards.LastTripsSynthesisCard
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardViewPagerFragment

class SynthesisCardExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_data_synthesis_cards_activity)

        DriverDataUI.getLastTripsSynthesisCardsView(
            listOf(
                LastTripsSynthesisCard.SAFETY,
                LastTripsSynthesisCard.ECO_DRIVING,
                LastTripsSynthesisCard.DISTRACTION,
                LastTripsSynthesisCard.SPEEDING
            ), object : SynthesisCardsViewListener {
                override fun onViewLoaded(fragment: DKSynthesisCardViewPagerFragment) {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit()
                }
            })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}