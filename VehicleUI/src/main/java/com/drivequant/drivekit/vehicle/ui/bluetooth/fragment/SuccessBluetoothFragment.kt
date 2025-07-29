package com.drivequant.drivekit.vehicle.ui.bluetooth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.dbvehicleaccess.DbVehicleAccess
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.bluetooth.viewmodel.BluetoothViewModel
import com.drivequant.drivekit.vehicle.ui.databinding.FragmentBluetoothSuccessBinding

class SuccessBluetoothFragment : Fragment() {

    companion object {
        fun newInstance(viewModel: BluetoothViewModel, vehicle: Vehicle): SuccessBluetoothFragment {
            val fragment = SuccessBluetoothFragment()
            fragment.viewModel = viewModel
            fragment.vehicle = vehicle
            return fragment
        }
    }

    private lateinit var viewModel: BluetoothViewModel
    private lateinit var vehicleId: String
    private lateinit var vehicleName: String
    private lateinit var vehicle: Vehicle
    private var _binding: FragmentBluetoothSuccessBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.putString("vehicleId", viewModel.vehicleId)
            outState.putString("vehicleName", viewModel.vehicleName)
        }
        super.onSaveInstanceState(outState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBluetoothSuccessBinding.inflate(inflater, container, false)
        binding.root.setDKStyle()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (savedInstanceState?.getString("vehicleId"))?.let {
            vehicleId = it
            if (!this::vehicle.isInitialized) {
                DbVehicleAccess.findVehicle(it).executeOne()?.let { vehicle ->
                    this@SuccessBluetoothFragment.vehicle = vehicle
                }
            }
        }
        (savedInstanceState?.getString("vehicleName"))?.let {
            vehicleName = it
        }

        checkViewModelInitialization()

        val btDeviceName = vehicle.bluetooth?.name ?: ""

        binding.textViewCongratsTitle.headLine1()

        binding.textViewCongratsDescription.normalText()
        binding.textViewCongratsDescription.text = DKResource.buildString(
            view.context,
            DKColors.mainFontColor,
            DKColors.mainFontColor,
            R.string.dk_vehicle_bluetooth_congrats_desc,
            btDeviceName,
            viewModel.vehicleName
        )

        binding.textViewCongratsNotice.normalText()
        binding.textViewCongratsNotice.text = DKResource.buildString(
            view.context, DKColors.mainFontColor,
            DKColors.mainFontColor, R.string.dk_vehicle_bluetooth_congrats_notice, btDeviceName
        )

        binding.buttonFinish.apply {
            setText(com.drivequant.drivekit.common.ui.R.string.dk_common_finish)
            setOnClickListener {
                activity?.finish()
            }
        }
    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this, BluetoothViewModel.BluetoothViewModelFactory(vehicleId, vehicleName))[BluetoothViewModel::class.java]
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
