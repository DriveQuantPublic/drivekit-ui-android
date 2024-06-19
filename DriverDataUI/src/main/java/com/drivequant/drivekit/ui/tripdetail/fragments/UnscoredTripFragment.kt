package com.drivequant.drivekit.ui.tripdetail.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.tint
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.databinding.UnscoredTripFragmentBinding
import com.drivequant.drivekit.ui.tripdetail.viewmodel.UnscoredTripViewModel

class UnscoredTripFragment : Fragment() {
    companion object {
        fun newInstance(
            trip: Trip?
        ): UnscoredTripFragment {
            val fragment = UnscoredTripFragment()
            fragment.viewModel = UnscoredTripViewModel(trip)
            return fragment
        }
    }

    private lateinit var viewModel: UnscoredTripViewModel
    private var _binding: UnscoredTripFragmentBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UnscoredTripFragmentBinding.inflate(inflater, container, false)
        FontUtils.overrideFonts(context, binding.root)
        binding.root.setDKStyle(android.R.color.white)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.putSerializable("viewModel", viewModel)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.getSerializableCompat("viewModel", UnscoredTripViewModel::class.java)?.let {
            viewModel = it
        }

        if (!this::viewModel.isInitialized) {
            activity?.finish()
            return
        }

        binding.tripDuration.text = DKDataFormatter.formatDuration(requireContext(), viewModel.getDuration()!!).convertToString()
        binding.tripStartEnd.text = viewModel.getStartDate()?.formatDate(DKDatePattern.HOUR_MINUTE_LETTER)
            .plus(" - ")
            .plus(viewModel.getEndDate()?.formatDate(DKDatePattern.HOUR_MINUTE_LETTER))

        binding.tripDuration.highlightMedium()
        binding.imageViewUnscoredTripInfo.background.tint(view.context, com.drivequant.drivekit.common.ui.R.color.warningColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
