package com.drivequant.drivekit.ui.tripdetail.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.dbtripaccess.DbTripAccess
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.databinding.DkAlternativeTripFragmentBinding
import com.drivequant.drivekit.ui.transportationmode.activity.TransportationModeActivity
import com.drivequant.drivekit.ui.tripdetail.viewmodel.AlternativeTripViewModel

internal class AlternativeTripFragment : Fragment() {

    private lateinit var trip: Trip
    private lateinit var viewModel: AlternativeTripViewModel
    private var _binding: DkAlternativeTripFragmentBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

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
    ): View {
        _binding = DkAlternativeTripFragmentBinding.inflate(inflater, container, false)
        binding.root.setDKStyle(android.R.color.white)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.putString("itinId", viewModel.getItinId())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!this::trip.isInitialized) {
            (savedInstanceState?.getString("itinId"))?.let { itinId ->
                DbTripAccess.findTrip(itinId).executeOneTrip()?.toTrip()?.let {
                    trip = it
                }
            }
        }
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(
                this,
                AlternativeTripViewModel.AlternativeTripViewModelFactory(trip)
            )[AlternativeTripViewModel::class.java]
        }
        binding.buttonChange.setText(R.string.dk_driverdata_change_transportation_mode)
        binding.buttonChange.setOnClickListener { launchTransportationMode() }
        binding.itemCondition.setValueItem(viewModel.getConditionValue(requireContext()))
        binding.itemWeather.setValueItem(viewModel.getWeatherValue(requireContext()))
        binding.itemMeanSpeed.setValueItem(viewModel.getMeanSpeed(requireContext()))
        binding.itemCondition.setValueTypeFace()
        binding.itemMeanSpeed.setValueTypeFace()
        binding.itemWeather.setValueTypeFace()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateTrip()
        updateContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateContent() {
        binding.transportationModeIcon.setImageDrawable(viewModel.getIcon(requireContext()))
        binding.transportationModeAnalyzedText.text =
            viewModel.getAnalyzedTransportationModeTitle(requireContext())
        viewModel.getDeclaredTransportationModeTitle(requireContext())?.let {
            binding.transportationModeDeclaredText.text = it
            binding.transportationModeDeclaredText.visibility = View.VISIBLE
        }

        binding.transportationModeDescription.text = viewModel.getDescription(requireContext())
    }

    private fun launchTransportationMode() {
        TransportationModeActivity.launchActivity(context as Activity, trip.itinId)
    }
}
