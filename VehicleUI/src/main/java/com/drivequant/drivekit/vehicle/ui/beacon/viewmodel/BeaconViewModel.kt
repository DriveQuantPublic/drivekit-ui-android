package com.drivequant.drivekit.vehicle.ui.beacon.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v4.app.Fragment
import com.drivequant.beaconutils.BeaconInfo
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.dbvehicleaccess.DbVehicleAccess
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleListQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.manager.beacon.*
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.BeaconInputIdFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.BeaconScannerFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.ConnectBeaconFragment
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType.*
import java.io.Serializable

class BeaconViewModel(
    val scanType: BeaconScanType,
    val vehicleId: String?,
    var beacon: Beacon?
) : ViewModel(), Serializable {

    var vehicle: Vehicle?
    var vehiclePaired: Vehicle? = null
    var seenBeacon: BeaconInfo? = null

    var listener: ScanState? = null

    val progressBarObserver = MutableLiveData<Boolean>()
    val codeObserver = MutableLiveData<HashMap<String, VehicleBeaconInfoStatus>>()
    val beaconAddObserver = MutableLiveData<VehicleBeaconStatus>()
    val beaconChangeObserver = MutableLiveData<VehicleBeaconStatus>()
    var fragmentDispatcher = MutableLiveData<Fragment>()

    init {
        vehicle = fetchVehicle()

        when (scanType){
            PAIRING -> fragmentDispatcher.postValue(ConnectBeaconFragment.newInstance(this))
            DIAGNOSTIC -> TODO()
            VERIFY -> {
                if (DriveKitTripAnalysis.getConfig().beacons.isEmpty()) {
                    fragmentDispatcher.postValue(BeaconScannerFragment.newInstance(this@BeaconViewModel, BeaconStep.BEACON_NOT_CONFIGURED))
                } else {
                    fragmentDispatcher.postValue(BeaconScannerFragment.newInstance(this@BeaconViewModel, BeaconStep.INITIAL))
                }
            }
        }
    }

    fun onConnectButtonClicked(){
        fragmentDispatcher.postValue(BeaconInputIdFragment.newInstance(this))
    }

    fun onCodeValid(){
        fragmentDispatcher.postValue(BeaconScannerFragment.newInstance(this@BeaconViewModel, BeaconStep.SCAN))
    }

    fun checkCode(codeValue: String){
        progressBarObserver.postValue(true)
        DriveKitVehicle.getBeaconByUniqueId(codeValue, object : VehicleGetBeaconQueryListener {
            override fun onResponse(status: VehicleBeaconInfoStatus, beacon: Beacon) {
                progressBarObserver.postValue(false)
                this@BeaconViewModel.beacon = beacon
                val map = HashMap<String, VehicleBeaconInfoStatus>()
                map[codeValue] = status
                codeObserver.postValue(map)
            }
        })
    }

    fun addBeaconToVehicle(){
        progressBarObserver.postValue(true)
        beacon?.let { beacon ->
            vehicle?.let { vehicle ->
                DriveKitVehicle.addBeaconToVehicle(beacon, vehicle, object : VehicleAddBeaconQueryListener{
                    override fun onResponse(status: VehicleBeaconStatus) {
                        progressBarObserver.postValue(false)
                        beaconAddObserver.postValue(status)
                    }
                })
            }
        }
    }

    fun changeBeaconToVehicle(){
        progressBarObserver.postValue(true)
        beacon?.let { beacon ->
            vehicle?.let { vehicle ->
                DriveKitVehicle.changeBeaconToVehicle(beacon, vehicle, object : VehicleChangeBeaconQueryListener {
                    override fun onResponse(status: VehicleBeaconStatus) {
                        progressBarObserver.postValue(false)
                        beaconChangeObserver.postValue(status)
                    }
                })
            }
        }
    }

    fun checkVehiclePaired(listener: ServiceListeners){
        DriveKitVehicle.getVehiclesOrderByNameAsc(object : VehicleListQueryListener {
            override fun onResponse(status: VehicleSyncStatus, vehicles: List<Vehicle>) {
                if (status == VehicleSyncStatus.NO_ERROR){
                    beacon?.let {beacon ->
                        for (vehicle in vehicles){
                            vehicle.beacon?.let { vehicleBeacon ->
                                if (vehicleBeacon.proximityUuid == beacon.proximityUuid && vehicleBeacon.major == beacon.major && vehicleBeacon.minor == beacon.minor){
                                    vehiclePaired = vehicle
                                }
                            }
                        }
                    }

                    when (scanType){
                        PAIRING, VERIFY -> {
                            vehicle?.let { vehicle ->
                                vehiclePaired?.let { vehiclePaired ->
                                    listener.onCheckVehiclePaired(vehicle.vehicleId == vehiclePaired.vehicleId)
                                } ?: run {
                                    listener.onCheckVehiclePaired(false)
                                }
                            }
                        }
                        DIAGNOSTIC -> {
                            listener.onCheckVehiclePaired(vehiclePaired != null)
                        }
                    }
                }
            }
        })
    }

    fun updateScanState(beaconStep: BeaconStep) {
        listener?.onStateUpdated(beaconStep)
    }

    fun scanValidationFinished(){
        listener?.onScanFinished()
    }

    fun showLoader() {
        listener?.displayLoader()
    }

    fun hideLoader() {
        listener?.hideLoader()
    }

    // TODO: refacto with other VM
    private fun fetchVehicle(): Vehicle? {
        return vehicleId?.let {
            DbVehicleAccess.findVehicle(it).executeOne()
        }?:run {
            null
        }
    }

    private fun fetchVehicles(): List<Vehicle> {
        return DbVehicleAccess.findVehicles().execute()
    }


    fun fetchVehicleFromBeacon(): Vehicle? {
        var matchedVehicle : Vehicle? = null
        for (vehicle in fetchVehicles()) {
            vehicle.beacon?.let { beacon ->
                seenBeacon?.let { seenBeacon ->
                    if (beacon.proximityUuid.equals(seenBeacon.proximityUuid, true)
                        && beacon.major == seenBeacon.major
                        && beacon.minor == seenBeacon.minor) {
                        matchedVehicle = vehicle
                    }
                }
            }
        }
        return matchedVehicle
    }

    fun isBeaconValid(): Boolean {
        var isBeaconValid = false
        beacon?.let { beacon ->
            seenBeacon?.let { seenBeacon ->
                if (beacon.proximityUuid.equals(seenBeacon.proximityUuid, true)
                            && beacon.major == seenBeacon.major
                            && beacon.minor == seenBeacon.minor) {
                    isBeaconValid = true
                }
            }
        }
        return isBeaconValid
    }

    @Suppress("UNCHECKED_CAST")
    class BeaconViewModelFactory(
        private val scanType: BeaconScanType,
        private val vehicleId: String?,
        private val beacon: Beacon?
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BeaconViewModel(scanType, vehicleId, beacon) as T
        }
    }

    interface ServiceListeners {
        fun onCheckVehiclePaired(isSameVehicle: Boolean)
    }
}