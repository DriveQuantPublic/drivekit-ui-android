package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.databinding.TripSynthesisFragmentBinding
import com.drivequant.drivekit.ui.tripdetail.viewmodel.SynthesisViewModel

class SynthesisFragment : Fragment() {

    private var _binding: TripSynthesisFragmentBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    companion object {
        fun newInstance(trip: Trip): SynthesisFragment {
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
    ): View {
        _binding = TripSynthesisFragmentBinding.inflate(inflater, container, false)
        binding.root.setDKStyle(Color.WHITE)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("trip", trip)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.getSerializableCompat("trip", Trip::class.java)?.let {
            trip = it
        }

        if (!this::viewModel.isInitialized){
            viewModel = ViewModelProvider(this,
                SynthesisViewModel.SynthesisViewModelFactory(trip)
            )[SynthesisViewModel::class.java]
        }

        viewModel.init(requireContext())

        binding.itemVehicleUsed.setValueItem(viewModel.getVehicleDisplayName())
        viewModel.getVehicleId()?.let {
            binding.itemVehicleUsed.setValueTypeFace()
            binding.itemVehicleUsed.setValueColor()

            binding.itemVehicleUsed.setOnClickListener {
                binding.itemVehicleUsed.onTripItemSynthesisClick(requireContext(), viewModel.getVehicleId(), viewModel.liteConfig)
            }
        }

        binding.itemMeanSpeed.setValueItem(viewModel.getMeanSpeed(requireContext()))
        binding.itemIdlingDuration.setValueItem(viewModel.getIdlingDuration(requireContext()))

        binding.itemConsumption.apply {
            setTitleItem(viewModel.getConsumptionTitle(requireContext()))
            setValueItem(viewModel.getConsumptionValue(requireContext()))
        }
        binding.itemCo2Emission.setValueItem(viewModel.getCo2Emission(requireContext()))
        binding.itemCo2Mass.setValueItem(viewModel.getCO2Mass(requireContext()))

        binding.itemCondition.setValueItem(viewModel.getCondition(requireContext()))
        binding.itemWeather.setValueItem(viewModel.getWeatherValue(requireContext()))
        binding.itemRoadContext.setValueItem(viewModel.getRoadContextValue(requireContext()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
