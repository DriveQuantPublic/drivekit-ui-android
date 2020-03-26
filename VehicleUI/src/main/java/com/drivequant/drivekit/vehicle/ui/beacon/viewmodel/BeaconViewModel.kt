package com.drivequant.drivekit.vehicle.ui.beacon.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.support.v4.app.Fragment
import com.drivequant.beaconutils.BeaconInfo
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleListQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.manager.beacon.*
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.BeaconInputIdFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.BeaconScannerFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.ConnectBeaconFragment
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType.*
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehiclesFetchListener
import java.io.Serializable

class BeaconViewModel(
    val scanType: BeaconScanType,
    var vehicleId: String?,
    var beacon: Beacon?
) : ViewModel(), Serializable {
    var vehicle: Vehicle? = null
    var vehicleName: String? = null
    var vehiclePaired: Vehicle? = null
    var seenBeacon: BeaconInfo? = null
    var batteryLevel: Int = 0
    var listener: ScanState? = null

    val progressBarObserver = MutableLiveData<Boolean>()
    val codeObserver = MutableLiveData<HashMap<String, VehicleBeaconInfoStatus>>()
    val beaconAddObserver = MutableLiveData<VehicleBeaconStatus>()
    val beaconChangeObserver = MutableLiveData<VehicleBeaconStatus>()
    val beaconDetailObserver = MutableLiveData<Any>()
    var fragmentDispatcher = MutableLiveData<Fragment>()

    fun init(context: Context){
        vehicleId?.let { vehicleId ->
            DriveKitVehicle.getVehicleByVehicleId(vehicleId, object : VehicleQueryListener {
                override fun onResponse(status: VehicleSyncStatus, vehicle: Vehicle?) {
                    this@BeaconViewModel.vehicle = vehicle
                    computeVehicleName(context)
                }
            })
        }

        when (scanType){
            PAIRING -> fragmentDispatcher.postValue(ConnectBeaconFragment.newInstance(this))
            DIAGNOSTIC -> {
                if (DriveKitTripAnalysis.getConfig().getAllBeacons().isEmpty()) {
                    if (beacon != null){
                        fragmentDispatcher.postValue(BeaconScannerFragment.newInstance(this, BeaconStep.INITIAL))
                    } else {
                        fragmentDispatcher.postValue(BeaconScannerFragment.newInstance(this, BeaconStep.BEACON_NOT_CONFIGURED))
                    }
                } else {
                    fragmentDispatcher.postValue(BeaconScannerFragment.newInstance(this, BeaconStep.INITIAL))
                }
            }
            VERIFY -> {
                fragmentDispatcher.postValue(BeaconScannerFragment.newInstance(this, BeaconStep.INITIAL))
            }
        }
    }

    fun computeVehicleName(context: Context){
        vehicle?.let {
            VehicleUtils().fetchVehiclesOrderedByDisplayName(context, SynchronizationType.CACHE, object : VehiclesFetchListener{
                override fun onVehiclesLoaded(syncStatus: VehicleSyncStatus, vehicles: List<Vehicle>) {
                    vehicleName = VehicleUtils().buildFormattedName(context, it, vehicles)
                }
            })
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

    fun launchDetailFragment(){
        beaconDetailObserver.postValue(null)
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

    fun fetchVehicleFromBeacon(vehicles: List<Vehicle>): Vehicle? {
        var matchedVehicle : Vehicle? = null

        for (vehicle in vehicles) {
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