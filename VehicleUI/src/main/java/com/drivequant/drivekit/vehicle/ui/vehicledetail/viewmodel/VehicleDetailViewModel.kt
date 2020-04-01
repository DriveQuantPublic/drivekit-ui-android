package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleManagerStatus
import com.drivequant.drivekit.vehicle.manager.VehicleQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleRenameQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehiclesFetchListener
import java.io.Serializable

class VehicleDetailViewModel(private val vehicleId: String): ViewModel(), Serializable {

    val progressBarObserver = MutableLiveData<Boolean>()
    val renameObserver = MutableLiveData<VehicleManagerStatus>()

    var vehicle: Vehicle? = null
    var vehicleName: String? = null
    var groupFields: MutableList<GroupField> = mutableListOf()

    fun init(context: Context){
        VehicleUtils().fetchVehiclesOrderedByDisplayName(context, SynchronizationType.CACHE, object : VehiclesFetchListener {
            override fun onVehiclesLoaded(syncStatus: VehicleSyncStatus, vehicles: List<Vehicle>) {
                DriveKitVehicle.getVehicleByVehicleId(vehicleId, object : VehicleQueryListener {
                    override fun onResponse(status: VehicleSyncStatus, vehicle: Vehicle?) {
                        this@VehicleDetailViewModel.vehicle = vehicle
                        vehicle?.let {
                            vehicleName = it.buildFormattedName(context, vehicles)
                            createGroupFields()
                        }
                    }
                })
            }
        })
    }

    private fun createGroupFields() {
        vehicle?.let {
            groupFields.clear()
            for (groupField in GroupField.values()){
                if (groupField.isDisplayable(it)){
                    groupFields.add(groupField)
                }
            }
        }
    }

    fun renameVehicle(vehicleName: String){
        progressBarObserver.postValue(true)
        vehicle?.let {vehicle ->
            DriveKitVehicle.renameVehicle(vehicleName, vehicle, object : VehicleRenameQueryListener{
                override fun onResponse(status: VehicleManagerStatus) {
                    progressBarObserver.postValue(false)
                    renameObserver.postValue(status)
                }
            })
        }
    }

    @Suppress("UNCHECKED_CAST")
    class VehicleDetailViewModelFactory(private val vehicleId: String)
        : ViewModelProvider.NewInstanceFactory() {
        override fun <T: ViewModel?> create(modelClass: Class<T>): T {
            return VehicleDetailViewModel(vehicleId) as T
        }
    }
}