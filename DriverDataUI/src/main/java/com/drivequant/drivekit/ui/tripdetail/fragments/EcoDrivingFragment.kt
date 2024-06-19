package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.component.GaugeConfiguration
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.databaseutils.entity.EcoDriving
import com.drivequant.drivekit.ui.databinding.EcoDrivingFragmentBinding
import com.drivequant.drivekit.ui.tripdetail.viewmodel.EcoDrivingViewModel

class EcoDrivingFragment : Fragment() {

    companion object {
        fun newInstance(ecoDriving: EcoDriving) : EcoDrivingFragment{
            val fragment = EcoDrivingFragment()
            fragment.viewModel = EcoDrivingViewModel(ecoDriving)
            return fragment
        }
    }

    private lateinit var viewModel: EcoDrivingViewModel
    private var _binding: EcoDrivingFragmentBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EcoDrivingFragmentBinding.inflate(inflater, container, false)
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
        savedInstanceState?.getSerializableCompat("viewModel", EcoDrivingViewModel::class.java)?.let {
            viewModel = it
        }

        if (!this::viewModel.isInitialized) {
            activity?.finish()
            return
        }

        binding.scoreGauge.configure(viewModel.getScore(), GaugeConfiguration.ECO_DRIVING(viewModel.getScore()), Typeface.BOLD)
        binding.scoreInfo.init(GaugeConfiguration.ECO_DRIVING(viewModel.getScore()))

        binding.accelAdvice.text = context?.getString(viewModel.getAccelMessage())
        binding.mainAdvice.text = context?.getString(viewModel.getMaintainMessage())
        binding.decelAdvice.text = context?.getString(viewModel.getDecelMessage())
        binding.gaugeTypeTitle.text = context?.getString(viewModel.getGaugeTitle())

        val mainFontColor = DKColors.mainFontColor

        binding.imageAccelAdvice.setColorFilter(mainFontColor)
        binding.imageDecelAdvice.setColorFilter(mainFontColor)
        binding.imageMainAdvice.setColorFilter(mainFontColor)

        binding.accelAdvice.normalText()
        binding.mainAdvice.normalText()
        binding.decelAdvice.normalText()
        binding.gaugeTypeTitle.normalText()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
