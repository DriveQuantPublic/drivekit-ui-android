package com.drivequant.drivekit.vehicle.ui.beacon.viewmodel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.beaconutils.BeaconInfo
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleListQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.manager.beacon.*
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.BeaconInputIdFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.BeaconScannerFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.ConnectBeaconFragment
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType.*
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
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
    var estimatedDistance: Double = 0.0
    var rssi: Int = 0
    var txPower: Int = 0
    var batteryLevel: Int = 0
        set(value) {
            field = value
            beacon?.let {
                DriveKitVehicle.updateBeaconBatteryLevel(it, value, object : VehicleUpdateBeaconBatteryLevelQueryListener {
                    override fun onResponse(status: VehicleBeaconBatteryStatus) {
                        // do nothing
                    }
                })
            }
        }
    var listener: ScanState? = null
    var beaconInfoStatusListener: BeaconInfoStatusListener? = null

    private var bluetoothAdapter: BluetoothAdapter? = null
    val progressBarObserver = MutableLiveData<Boolean>()
    val beaconAddObserver = MutableLiveData<VehicleBeaconStatus>()
    val beaconChangeObserver = MutableLiveData<VehicleBeaconStatus>()
    val beaconDetailObserver = MutableLiveData<Any>()
    var fragmentDispatcher = MutableLiveData<Fragment>()

    fun init(context: Context) {
        this.bluetoothAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        vehicleId?.let { vehicleId ->
            this@BeaconViewModel.vehicle = DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne().executeOne()
            computeVehicleName(context)
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

    fun computeVehicleName(context: Context) {
        vehicle?.let {
            vehicleName = it.buildFormattedName(context)
        }
    }

    fun onConnectButtonClicked() {
        fragmentDispatcher.postValue(BeaconInputIdFragment.newInstance(this))
    }

    fun onCodeValid() {
        fragmentDispatcher.postValue(BeaconScannerFragment.newInstance(this@BeaconViewModel, BeaconStep.SCAN))
    }

    fun checkCode(codeValue: String) {
        progressBarObserver.postValue(true)
        DriveKitVehicle.getBeaconByUniqueId(codeValue, object : VehicleGetBeaconQueryListener {
            override fun onResponse(status: VehicleBeaconInfoStatus, beacon: Beacon) {
                progressBarObserver.postValue(false)
                this@BeaconViewModel.beacon = beacon
                beaconInfoStatusListener?.onBeaconStatusReceived(codeValue, status)
            }
        })
    }

    fun addBeaconToVehicle() {
        progressBarObserver.postValue(true)
        beacon?.let { beacon ->
            vehicle?.let { vehicle ->
                if (vehicle.beacon != null) {
                    DriveKitVehicle.changeBeaconToVehicle(beacon, vehicle, object : VehicleChangeBeaconQueryListener {
                            override fun onResponse(status: VehicleBeaconStatus) {
                                progressBarObserver.postValue(false)
                                beaconAddObserver.postValue(status)
                            }
                        })
                } else {
                    DriveKitVehicle.addBeaconToVehicle(beacon, vehicle, object : VehicleAddBeaconQueryListener {
                            override fun onResponse(status: VehicleBeaconStatus) {
                                progressBarObserver.postValue(false)
                                beaconAddObserver.postValue(status)
                            }
                        })
                }
            }
        }
    }

    fun changeBeaconToVehicle() {
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

    fun checkVehiclePaired(listener: ServiceListeners) {
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

                    when (scanType) {
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

    fun launchDetailFragment() {
        beaconDetailObserver.postValue(Any())
    }

    fun updateScanState(beaconStep: BeaconStep) {
        listener?.onStateUpdated(beaconStep)
    }

    fun scanValidationFinished() {
        listener?.onScanFinished()
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

    fun fetchVehicleFromSeenBeacon(vehicles: List<Vehicle>): Vehicle? {
        var matchedVehicle : Vehicle? = null
        seenBeacon?.let { seenBeacon ->
            for (vehicle in vehicles) {
                if (vehicle.beacon?.proximityUuid.equals(seenBeacon.proximityUuid, true)
                    && vehicle.beacon?.major == seenBeacon.major
                    && vehicle.beacon?.minor == seenBeacon.minor
                ) {
                    matchedVehicle = vehicle
                    break
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

    fun isBluetoothSensorEnabled(): Boolean = bluetoothAdapter?.isEnabled ?: false

    @Suppress("UNCHECKED_CAST")
    class BeaconViewModelFactory(
        private val scanType: BeaconScanType,
        private val vehicleId: String?,
        private val beacon: Beacon?
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BeaconViewModel(scanType, vehicleId, beacon) as T
        }
    }

    interface ServiceListeners {
        fun onCheckVehiclePaired(isSameVehicle: Boolean)
    }

    interface BeaconInfoStatusListener {
        fun onBeaconStatusReceived(beaconCode: String, status: VehicleBeaconInfoStatus)
    }
}
