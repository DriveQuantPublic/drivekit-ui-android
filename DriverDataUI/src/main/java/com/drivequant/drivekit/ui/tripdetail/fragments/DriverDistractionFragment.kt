package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeConfiguration
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.databinding.DriverDistractionFragmentBinding
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKTripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapTraceType
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModelFactory
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType

internal class DriverDistractionFragment : Fragment(), View.OnClickListener {

    private var _binding: DriverDistractionFragmentBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    companion object {
        fun newInstance(tripDetailViewModel: DKTripDetailViewModel): DriverDistractionFragment {
            val fragment = DriverDistractionFragment()
            fragment.viewModel = tripDetailViewModel
            return fragment
        }
    }

    private lateinit var viewModel: DKTripDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DriverDistractionFragmentBinding.inflate(inflater, container, false)
        binding.root.setDKStyle(Color.WHITE)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.putString("itinId", viewModel.getItinId())
            outState.putSerializable("tripListConfigurationType", viewModel.getTripListConfigurationType())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            val itinId = it.getString("itinId")
            val tripListConfigurationType = it.getSerializableCompat("tripListConfigurationType", TripListConfigurationType::class.java)

            if (itinId != null && tripListConfigurationType != null) {
                viewModel = ViewModelProvider(
                    this,
                    TripDetailViewModelFactory(itinId, tripListConfigurationType.getTripListConfiguration())
                )[TripDetailViewModel::class.java]
            }
        }

        binding.phoneCallSelector.apply {
            setOnClickListener(this@DriverDistractionFragment)
            setSelectorContent(viewModel.getPhoneCallsDuration(requireContext()))
        }
        binding.screenUnlockSelector.apply {
            setOnClickListener(this@DriverDistractionFragment)
            setSelectorContent("${viewModel.getUnlockNumberEvent()}")
        }

        viewModel.getSelectedTraceType().value?.let {
            onSelectorChanged(it)
        } ?: run {
            onSelectorChanged(MapTraceType.UNLOCK_SCREEN)
        }

        binding.scoreGauge.configure(viewModel.getDistractionScore(), GaugeConfiguration.DISTRACTION(viewModel.getDistractionScore()), Typeface.BOLD)
        binding.scoreInfo.init(GaugeConfiguration.DISTRACTION(viewModel.getDistractionScore()))
        binding.gaugeTypeTitle.text = requireContext().getString(com.drivequant.drivekit.common.ui.R.string.dk_common_distraction)
        binding.gaugeTypeTitle.normalText()

        binding.phoneCallItem.apply {
            setDistractionEventContent(
                viewModel.getPhoneCallsNumber(requireContext()).first,
                viewModel.getPhoneCallsNumber(requireContext()).second
            )
        }

        val unlockEvent = DKResource.convertToString(requireContext(), "dk_driverdata_unlock_events")
        val unlockContent =
            if (viewModel.hasScreenUnlocking()) {
                DKResource.buildString(
                    requireContext(),
                    DriveKitUI.colors.secondaryColor(),
                    DriveKitUI.colors.secondaryColor(),
                    "dk_driverdata_unlock_screen_content",
                    viewModel.getUnlockDuration(requireContext()),
                    viewModel.getUnlockDistance(requireContext())
                ).toString()
            } else {
                DKResource.convertToString(requireContext(), "dk_driverdata_no_screen_unlocking")
            }
        binding.screenUnlockItem.setDistractionEventContent(
            unlockEvent,
            unlockContent
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onSelectorChanged(mapTraceType: MapTraceType) {
        when(mapTraceType) {
            MapTraceType.UNLOCK_SCREEN -> {
                binding.screenUnlockSelector.setSelection(true)
                binding.phoneCallSelector.setSelection(false)
                binding.screenUnlockItem.setDistractionContentColor(true)
                binding.phoneCallItem.setDistractionContentColor(false)
            }
            MapTraceType.PHONE_CALL ->{
                binding.phoneCallSelector.setSelection(true)
                binding.screenUnlockSelector.setSelection(false)
                binding.screenUnlockItem.setDistractionContentColor(false)
                binding.phoneCallItem.setDistractionContentColor(true)
            }
            else -> {
                //DO NOTHING
            }
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.screen_unlock_selector -> {
                    MapTraceType.UNLOCK_SCREEN.let { mapTraceType ->
                        viewModel.getSelectedTraceType().postValue(mapTraceType)
                        onSelectorChanged(mapTraceType)
                    }
                }
                R.id.phone_call_selector -> {
                    MapTraceType.PHONE_CALL.let { mapTraceType ->
                        viewModel.getSelectedTraceType().postValue(mapTraceType)
                        onSelectorChanged(mapTraceType)
                    }
                }
            }
        }
    }
}
