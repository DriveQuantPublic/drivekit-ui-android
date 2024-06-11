package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.component.GaugeConfiguration
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.highlightSmall
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.databaseutils.entity.Safety
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.databinding.SafetyFragmentBinding
import com.drivequant.drivekit.ui.tripdetail.viewmodel.SafetyViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.SafetyViewModelFactory

class SafetyFragment : Fragment() {

    companion object {
        fun newInstance(safety: Safety): SafetyFragment {
            val fragment = SafetyFragment()
            fragment.safety = safety
            return fragment
        }
    }

    private lateinit var safety: Safety
    private lateinit var viewModel: SafetyViewModel
    private var _binding: SafetyFragmentBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SafetyFragmentBinding.inflate(inflater, container, false)
        binding.root.setDKStyle(android.R.color.white)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::safety.isInitialized) {
            outState.putSerializable("safety", safety)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.getSerializableCompat("safety", Safety::class.java)?.let{
            safety = it
        }
        viewModel = ViewModelProvider(this,
            SafetyViewModelFactory(safety)
        )[SafetyViewModel::class.java]

        binding.gaugeTypeTitle.text = context?.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_safety)
        binding.accelDescription.text = context?.getString(R.string.dk_driverdata_safety_accel)
        binding.brakeDescription.text = context?.getString(R.string.dk_driverdata_safety_decel)
        binding.adherenceDescription.text = context?.getString(R.string.dk_driverdata_safety_adherence)

        val mainFontColor = DKColors.mainFontColor

        binding.gaugeTypeTitle.normalText()
        binding.accelDescription.normalText()
        binding.brakeDescription.normalText()
        binding.adherenceDescription.normalText()

        binding.accelImage.setColorFilter(mainFontColor)
        binding.decelImage.setColorFilter(mainFontColor)
        binding.adherenceImage.setColorFilter(mainFontColor)

        binding.accelNumberEvent.highlightSmall()
        binding.brakeNumberEvent.highlightSmall()
        binding.adherenceNumberEvent.highlightSmall()

        binding.scoreGauge.configure(viewModel.getScore(), GaugeConfiguration.SAFETY(viewModel.getScore()), Typeface.BOLD)
        binding.scoreInfo.init(GaugeConfiguration.SAFETY(viewModel.getScore()))

        binding.accelNumberEvent.text = viewModel.getAccelNumberEvent().toString()
        binding.brakeNumberEvent.text = viewModel.getBrakeNumberEvent().toString()
        binding.adherenceNumberEvent.text = viewModel.getAdherenceNumberEvent().toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
