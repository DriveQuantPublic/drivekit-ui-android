package com.drivequant.drivekit.vehicle.ui.bluetooth.fragment

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.DKPrimaryButton
import com.drivequant.drivekit.common.ui.component.DKSecondaryButton
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.vehicle.ui.databinding.FragmentBluetoothErrorBinding

class ErrorBluetoothFragment : Fragment() {

    companion object {
        private const val REQUEST_ENABLE_BT = 1

        fun newInstance(vehicleId: String): ErrorBluetoothFragment {
            val fragment = ErrorBluetoothFragment()
            fragment.vehicleId = vehicleId
            return fragment
        }
    }

    private lateinit var vehicleId: String
    private var _binding: FragmentBluetoothErrorBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBluetoothErrorBinding.inflate(inflater, container, false)
        binding.root.setDKStyle()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewBluetoothFailed.normalText()
        binding.buttonCancel.setContent {
            DKPrimaryButton(getString(R.string.dk_common_cancel)) {
                activity?.finish()
            }

        }
        binding.textViewOpenSettings.setContent {
            DKSecondaryButton(getString(com.drivequant.drivekit.vehicle.ui.R.string.dk_vehicle_open_bluetooth_settings)) {
                startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            activity?.finish()
        }
    }
}
