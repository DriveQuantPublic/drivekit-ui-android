package com.drivekit.demoapp.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.component.DKGaugeConfiguration
import com.drivequant.drivekit.common.ui.component.DKGaugeType
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.ui.DriverDataSynthesisCardsUI
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.SynthesisCardsViewListener
import com.drivequant.drivekit.ui.synthesiscards.*
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

        /*val lastTrips = SynthesisCardsUtils.getLastWeekTrips()
        val customCard = object : DKSynthesisCard {
            override fun getTitle(context: Context): String = "Titre carte custom"
            override fun getExplanationContent(context: Context): String? = "Mock explanation content"
            override fun getGaugeConfiguration(): DKGaugeConfiguration {
                return object : DKGaugeConfiguration {
                    override fun getTitle(context: Context) = DKSpannable().append("33%").toSpannable()
                    override fun getScore() = 666.6667
                    override fun getColor(value: Double) = R.color.dkVeryBad
                    override fun getMaxScore() = 2000.0
                    override fun getIcon() = null
                    override fun getGaugeConfiguration() = DKGaugeType.CLOSE
                }
            }
            override fun getTopSynthesisCardInfo(context: Context) = SynthesisCardInfo.TRIPS(lastTrips)
            override fun getMiddleSynthesisCardInfo(context: Context) = SynthesisCardInfo.DISTANCE(lastTrips)
            override fun getBottomSynthesisCardInfo(context: Context) = SynthesisCardInfo.DURATION(lastTrips)
            override fun getBottomText(context: Context) = null
        }

        DriverDataSynthesisCardsUI.getSynthesisCardsView(listOf(customCard), object : SynthesisCardsViewListener {
            override fun onViewLoaded(fragment: DKSynthesisCardViewPagerFragment) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit()
            }
        })*/


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}