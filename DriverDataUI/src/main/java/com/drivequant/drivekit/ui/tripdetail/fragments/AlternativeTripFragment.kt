package com.drivequant.drivekit.ui.tripdetail.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.commons.views.TripSynthesisItem
import com.drivequant.drivekit.ui.tripdetail.viewmodel.AlternativeTripViewModel
import kotlinx.android.synthetic.main.dk_alternative_trip_fragment.*

internal class AlternativeTripFragment : Fragment() {

    private lateinit var trip: Trip
    private lateinit var viewModel: AlternativeTripViewModel
    private lateinit var conditionItem: TripSynthesisItem
    private lateinit var weatherItem: TripSynthesisItem
    private lateinit var meanSpeedItem: TripSynthesisItem

    companion object {
        fun newInstance(trip: Trip): AlternativeTripFragment {
            val fragment = AlternativeTripFragment()
            fragment.trip = trip
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dk_alternative_trip_fragment, container, false)
        view.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
        conditionItem = view.findViewById(R.id.item_condition)
        weatherItem = view.findViewById(R.id.item_weather)
        meanSpeedItem = view.findViewById(R.id.item_mean_speed)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(
            this,
            AlternativeTripViewModel.AlternativeTripViewModelFactory(trip)
        ).get(AlternativeTripViewModel::class.java)
        updateContent()
    }

    private fun updateContent() {
        transportation_mode_icon.setImageDrawable(viewModel.getIcon(requireContext()))
        transportation_mode_analyzed_text.text =
            viewModel.getAnalyzedTransportationModeTitle(requireContext())
        viewModel.getDeclaredTransportationModeTitle(requireContext())?.let {
            transportation_mode_declared_text.text = it
            transportation_mode_declared_text.visibility = View.VISIBLE
        }

        transportation_mode_description.text = viewModel.getDescription(requireContext())
        conditionItem.setValueItem(viewModel.getConditionValue(requireContext()))
        weatherItem.setValueItem(viewModel.getWeatherValue(requireContext()))
        meanSpeedItem.setValueItem(viewModel.getMeanSpeed(requireContext()))
    }
}