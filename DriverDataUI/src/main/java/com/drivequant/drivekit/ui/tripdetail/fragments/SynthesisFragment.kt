package com.drivequant.drivekit.ui.tripdetail.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.TripDetailViewConfig
import com.drivequant.drivekit.ui.TripsViewConfig
import com.drivequant.drivekit.ui.tripdetail.viewmodel.SynthesisViewModel
import kotlinx.android.synthetic.main.trip_synthesis_fragment.*

class SynthesisFragment : Fragment() {

    companion object {
        fun newInstance(trip: Trip,
                        tripsViewConfig: TripsViewConfig,
                        detailViewConfig: TripDetailViewConfig
        ) : SynthesisFragment {
            val fragment = SynthesisFragment()
            fragment.viewModel = SynthesisViewModel(trip)
            fragment.tripsViewConfig = tripsViewConfig
            fragment.detailViewConfig = detailViewConfig
            return fragment
        }
    }

    private lateinit var viewModel: SynthesisViewModel
    private lateinit var tripsViewConfig: TripsViewConfig
    private lateinit var detailViewConfig: TripDetailViewConfig

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.trip_synthesis_fragment, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("config", tripsViewConfig)
        outState.putSerializable("detailConfig", detailViewConfig)
        outState.putSerializable("viewModel", viewModel)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getSerializable("config") as TripsViewConfig?)?.let{
            tripsViewConfig = it
        }
        (savedInstanceState?.getSerializable("detailConfig") as TripDetailViewConfig?)?.let{
            detailViewConfig = it
        }
        (savedInstanceState?.getSerializable("viewModel") as SynthesisViewModel?)?.let{
            viewModel = it
        }

        item_vehicle_used.setValueItem(viewModel.getVehicleDisplayName())
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
