package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.tintDrawable
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
        binding.root.setDKStyle(Color.WHITE)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("viewModel", viewModel)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.getSerializableCompat("viewModel", UnscoredTripViewModel::class.java)?.let {
            viewModel = it
        }

        binding.tripDuration.text = DKDataFormatter.formatDuration(requireContext(), viewModel.getDuration()!!).convertToString()
        binding.tripStartEnd.text = viewModel.getStartDate()?.formatDate(DKDatePattern.HOUR_MINUTE_LETTER)
            .plus(" - ")
            .plus(viewModel.getEndDate()?.formatDate(DKDatePattern.HOUR_MINUTE_LETTER))
        binding.tripMessage.text = context?.getString(viewModel.getNoScoreTripMessage())

        binding.tripMessage.setTextColor(DriveKitUI.colors.fontColorOnSecondaryColor())
        binding.tripMessage.setBackgroundColor(DriveKitUI.colors.warningColor())
        binding.tripStartEnd.setTextColor(DriveKitUI.colors.primaryColor())
        binding.tripDuration.highlightMedium(DriveKitUI.colors.primaryColor())
        binding.imageViewUnscoredTripInfo.background.tintDrawable(DriveKitUI.colors.warningColor())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
