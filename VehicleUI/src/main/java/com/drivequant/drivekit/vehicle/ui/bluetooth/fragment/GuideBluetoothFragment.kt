package com.drivequant.drivekit.vehicle.ui.bluetooth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.bluetooth.viewmodel.BluetoothViewModel
import com.drivequant.drivekit.vehicle.ui.databinding.FragmentBluetoothGuideBinding

class GuideBluetoothFragment : Fragment() {

    companion object {
        fun newInstance(viewModel: BluetoothViewModel, vehicleId: String): GuideBluetoothFragment {
            val fragment = GuideBluetoothFragment()
            fragment.viewModel = viewModel
            fragment.vehicleId = vehicleId
            return fragment
        }
    }

    private lateinit var viewModel: BluetoothViewModel
    private lateinit var vehicleId: String
    private var _binding: FragmentBluetoothGuideBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBluetoothGuideBinding.inflate(inflater, container, false)
        binding.root.setDKStyle()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewGuideTitle.headLine1()
        binding.textViewGuideTitle.setText(R.string.dk_vehicle_bluetooth_guide_header)

        binding.textViewGuideDesc1.normalText()
        binding.textViewGuideDesc1.setText(R.string.dk_vehicle_bluetooth_guide_desc1_android)

        binding.textViewGuideDesc2.normalText()
        binding.textViewGuideDesc2.setText(R.string.dk_vehicle_bluetooth_guide_desc2_android)

        binding.textViewGuideDesc3.normalText()
        binding.textViewGuideDesc3.setText(R.string.dk_vehicle_bluetooth_guide_desc3_android)

        binding.buttonStart.apply {
            button()
            setText(R.string.dk_vehicle_begin)
            setOnClickListener {
                viewModel.onStartButtonClicked()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
