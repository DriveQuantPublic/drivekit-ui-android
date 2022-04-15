package com.drivekit.demoapp.dashboard.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.drivekit.demoapp.dashboard.viewmodel.DashboardViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.SynthesisCardsViewListener
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardViewPagerFragment
import kotlinx.android.synthetic.main.fragment_dashboard.*

internal class DashboardFragment : Fragment() {

    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFeatureCard()
    }

    override fun onResume() {
        super.onResume()
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        }
        initSynthesisTripsCard()
        initLastTripsCard()
        initStartStopTripButton()
        initStartStopTripSimulatorButton()
    }

    private fun initSynthesisTripsCard() {
        viewModel.getSynthesisCardsView(object : SynthesisCardsViewListener {
            override fun onViewLoaded(fragment: DKSynthesisCardViewPagerFragment) {
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.container_synthesis, fragment)
                    .commit()
            }
        })
    }

    private fun initLastTripsCard() {
        DriverDataUI.getLastTripsView(HeaderDay.DURATION).let {
            childFragmentManager
                .beginTransaction()
                .replace(R.id.container_last_trips, viewModel.getLastTripsCardsView())
                .commit()
        }
    }

    private fun initFeatureCard() {
        card_features.apply {
            configureTitle(R.string.feature_list)
            configureDescription(R.string.feature_list_description)
            configureTextButton(R.string.button_see_features)
        }
    }

    private fun initStartStopTripButton() {
        button_start_stop_trip.findViewById<Button>(R.id.button_action).apply {
            setOnClickListener {
                viewModel.manageStartStopTripButton()
            }
            text = "" // TODO
        }
    }

    private fun initStartStopTripSimulatorButton() {
        button_start_stop_trip_simulator.findViewById<Button>(R.id.button_action).setOnClickListener {
            viewModel.manageStartStopTripSimulatorButton()
        }
    }
}