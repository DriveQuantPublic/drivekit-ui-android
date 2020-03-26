package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.dbvehicleaccess.DbVehicleAccess
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehiclesFetchListener
import java.io.Serializable

class VehicleDetailViewModel(private val vehicleId: String): ViewModel(), Serializable {

    var vehicle: Vehicle? = null
    var vehicleName: String? = null
    var groupFields: MutableList<GroupField> = mutableListOf()

    fun init(context: Context){
        VehicleUtils().fetchVehiclesOrderedByDisplayName(context, SynchronizationType.CACHE, object : VehiclesFetchListener {
            override fun onVehiclesLoaded(syncStatus: VehicleSyncStatus, vehicles: List<Vehicle>) {
                vehicle = fetchVehicle()
                vehicle?.let {
                    vehicleName = VehicleUtils().buildFormattedName(context, it , vehicles)
                }
                createGroupFields()
            }
        })
    }

    private fun fetchVehicle(): Vehicle? {
        return DbVehicleAccess.findVehicle(vehicleId).executeOne()
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

    @Suppress("UNCHECKED_CAST")
    class VehicleDetailViewModelFactory(private val vehicleId: String)
        : ViewModelProvider.NewInstanceFactory() {
        override fun <T: ViewModel?> create(modelClass: Class<T>): T {
            return VehicleDetailViewModel(vehicleId) as T
        }
    }
}