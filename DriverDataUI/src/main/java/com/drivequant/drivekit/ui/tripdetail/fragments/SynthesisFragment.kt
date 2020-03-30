package com.drivequant.drivekit.ui.tripdetail.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.SynthesisViewModel
import kotlinx.android.synthetic.main.trip_synthesis_fragment.*

class SynthesisFragment : Fragment() {

    companion object {
        fun newInstance(trip: Trip) : SynthesisFragment {
            val fragment = SynthesisFragment()
            fragment.trip = trip
            fragment.viewModel = SynthesisViewModel(trip)
            return fragment
        }
    }

    private lateinit var trip: Trip
    private lateinit var viewModel: SynthesisViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.trip_synthesis_fragment, container, false)
        FontUtils.overrideFonts(context, view)
        view.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("trip", trip)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getSerializable("trip") as Trip?)?.let{
            trip = it
        }

        if (!this::viewModel.isInitialized){
            viewModel = ViewModelProviders.of(this,
                SynthesisViewModel.SynthesisViewModelFactory(trip)
            ).get(SynthesisViewModel::class.java)
        }

        viewModel.init(requireContext())

        item_vehicle_used.setValueItem(viewModel.vehicleName)
        item_vehicle_used.setOnClickListener {
            item_vehicle_used.onTripItemSynthesisClick(requireContext(), viewModel.getVehicleId(), viewModel.liteConfig)
        }

        item_speed_mean.setValueItem(viewModel.getMeanSpeed(requireContext()))
        item_idling_duration.setValueItem(viewModel.getIdlingDuration(requireContext()))

        item_fuel_consumption.setValueItem(viewModel.getFuelConsumption(requireContext()))
        item_co2_emission.setValueItem(viewModel.getCo2Emission(requireContext()))
        item_co2_mass.setValueItem(viewModel.getCO2Mass(requireContext()))

        item_condition.setValueItem(viewModel.getCondition(requireContext()))
        item_weather.setValueItem(viewModel.getWeatherValue(requireContext()))
        item_road_context.setValueItem(viewModel.getRoadContextValue(requireContext()))
    }
}
