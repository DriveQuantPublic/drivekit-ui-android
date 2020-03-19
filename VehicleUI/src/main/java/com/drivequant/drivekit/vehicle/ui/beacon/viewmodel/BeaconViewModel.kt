package com.drivequant.drivekit.vehicle.ui.beacon.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v4.app.Fragment
import com.drivequant.beaconutils.BeaconData
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.dbvehicleaccess.DbVehicleAccess
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconStatus
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconStatus.*
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconStatus.SUCCESS
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleGetBeaconQueryListener
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.BeaconInputIdFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.BeaconScannerFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.ConnectBeaconFragment
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType.*
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType.VERIFY
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconStep.*
import java.io.Serializable

class BeaconViewModel(
    private val scanType: BeaconScanType,
    val vehicleId: String,
    val beacon: BeaconData?
) : ViewModel(), Serializable {

    var vehicle: Vehicle?

    val progressBarObserver = MutableLiveData<Boolean>()
    val codeObserver = MutableLiveData<Boolean>()
    var fragmentDispatcher = MutableLiveData<Fragment>()

    init {
        vehicle = fetchVehicle()

        when (scanType){
            PAIRING -> fragmentDispatcher.postValue(ConnectBeaconFragment.newInstance(this, vehicle!!))
            DIAGNOSTIC -> TODO()
            VERIFY -> TODO()
        }
    }


    fun onConnectButtonClicked(){
        fragmentDispatcher.postValue(BeaconInputIdFragment.newInstance(this, vehicle!!))
    }

    fun checkCode(codeValue: String){
        progressBarObserver.postValue(true)
        DriveKitVehicle.getBeaconByUniqueId(codeValue, object : VehicleGetBeaconQueryListener {
            override fun onResponse(status: VehicleBeaconStatus, beacon: Beacon) {
                progressBarObserver.postValue(false)
                if (status == SUCCESS){
                    fragmentDispatcher.postValue(BeaconScannerFragment.newInstance(this@BeaconViewModel))
                } else {
                    codeObserver.postValue(null)
                }
            }
        })
    }

    fun updateScanState(beaconStep: BeaconStep) {

    }


    // TODO: refacto with other VM
    private fun fetchVehicle(): Vehicle? {
        return DbVehicleAccess.findVehicle(vehicleId).executeOne()
    }

    @Suppress("UNCHECKED_CAST")
    class BeaconViewModelFactory(
        private val scanType: BeaconScanType,
        private val vehicleId: String,
        private val beacon: BeaconData?
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BeaconViewModel(scanType, vehicleId, beacon) as T
        }
    }
}