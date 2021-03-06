package com.drivequant.drivekit.vehicle.ui.bluetooth.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.databaseutils.entity.Bluetooth
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.bluetooth.BluetoothData
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.bluetooth.VehicleAddBluetoothQueryListener
import com.drivequant.drivekit.vehicle.manager.bluetooth.VehicleBluetoothStatus
import com.drivequant.drivekit.vehicle.manager.bluetooth.VehicleBluetoothStatus.*
import com.drivequant.drivekit.vehicle.ui.bluetooth.fragment.ErrorBluetoothFragment
import com.drivequant.drivekit.vehicle.ui.bluetooth.fragment.GuideBluetoothFragment
import com.drivequant.drivekit.vehicle.ui.bluetooth.fragment.SelectBluetoothFragment
import com.drivequant.drivekit.vehicle.ui.bluetooth.fragment.SuccessBluetoothFragment
import java.io.Serializable

class BluetoothViewModel(
    val vehicleId: String,
    val vehicleName: String
): ViewModel(), Serializable {
    var vehicle: Vehicle? = null
    var bluetoothDevices: List<BluetoothData>

    var fragmentDispatcher = MutableLiveData<Fragment>()
    val progressBarObserver = MutableLiveData<Boolean>()
    var addBluetoothObserver = MutableLiveData<String>()

    init {
        this@BluetoothViewModel.vehicle = DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne().executeOne()
        bluetoothDevices = DriveKitTripAnalysis.getBluetoothPairedDevices()
        fragmentDispatcher.postValue(GuideBluetoothFragment.newInstance(this, vehicleId))
    }

    fun onStartButtonClicked(){
        if (bluetoothDevices.isEmpty()){
            fragmentDispatcher.postValue(ErrorBluetoothFragment.newInstance(vehicleId))
        } else {
            fragmentDispatcher.postValue(SelectBluetoothFragment.newInstance(this, vehicleId))
        }
    }

    fun addBluetoothToVehicle(bluetooth: BluetoothData){
        progressBarObserver.postValue(true)
        vehicle?.let {
            val name = bluetooth.name?.let { bluetooth.name } ?: run { bluetooth.macAddress }

            DriveKitVehicle.addBluetoothToVehicle(Bluetooth(bluetooth.macAddress, name), it, object: VehicleAddBluetoothQueryListener{
                override fun onResponse(status: VehicleBluetoothStatus) {
                    progressBarObserver.postValue(false)
                    when (status){
                        SUCCESS -> fragmentDispatcher.postValue(SuccessBluetoothFragment.newInstance(this@BluetoothViewModel, it))
                        ERROR -> addBluetoothObserver.postValue("dk_vehicle_failed_to_paired_bluetooth")
                        UNKNOWN_VEHICLE -> addBluetoothObserver.postValue("dk_vehicle_unknown")
                        UNAVAILABLE_BLUETOOTH -> addBluetoothObserver.postValue("dk_vehicle_unknown")
                    }
                }
            })
        }
    }

    fun isBluetoothAlreadyPaired(macAddress: String, vehicles: List<Vehicle>): Boolean {
        return !vehicles.filter {
            it.bluetooth?.macAddress == macAddress
        }.isNullOrEmpty()
    }

    @Suppress("UNCHECKED_CAST")
    class BluetoothViewModelFactory(private val vehicleId: String, private val vehicleName: String) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BluetoothViewModel(vehicleId, vehicleName) as T
        }
    }
}