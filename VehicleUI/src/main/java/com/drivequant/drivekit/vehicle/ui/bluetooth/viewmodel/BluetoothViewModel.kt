package com.drivequant.drivekit.vehicle.ui.bluetooth.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v4.app.Fragment
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.dbvehicleaccess.DbVehicleAccess
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.bluetooth.BluetoothData
import com.drivequant.drivekit.vehicle.ui.bluetooth.fragment.ErrorBluetoothFragment
import java.io.Serializable

class BluetoothViewModel(val vehicleId: String): ViewModel(), Serializable {

    var vehicle: Vehicle?
    lateinit var bluetoothDevices: List<BluetoothData>

    var fragmentDispatcher = MutableLiveData<Fragment>()

    init {
        vehicle = fetchVehicle()
        fetchBluetoothDevices()
        if (bluetoothDevices.isNotEmpty()) {
            // TODO: WIP
        } else {
            fragmentDispatcher.postValue(ErrorBluetoothFragment())
        }
    }

    private fun fetchBluetoothDevices() {
        bluetoothDevices = DriveKitTripAnalysis.getBluetoothPairedDevices()
    }

    private fun fetchVehicle(): Vehicle? {
        return DbVehicleAccess.findVehicle(vehicleId).executeOne()
    }

    class BluetoothViewModelFactory(private val vehicleId: String) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BluetoothViewModel(vehicleId) as T
        }
    }
}