package com.drivequant.drivekit.vehicle.ui.manager.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R
import java.io.Serializable

class VehicleListViewModel : ViewModel(), Serializable {
    val vehiclesData = MutableLiveData<Boolean>()
    val vehicles: MutableList<Vehicle> = mutableListOf()


    fun getTitle(context: Context, vehicle: Vehicle): String {
        val defaultName = getDefaultName(vehicle)
        return if (vehicle.name == defaultName){
            context.getString(R.string.dk_vehicle_my_vehicle) + " - position"
        } else {
            defaultName
        }
    }

    fun getSubtitle(vehicle: Vehicle): String {
        // Todo if liteconfig, display category by carTypeIndex ?
        return getDefaultName(vehicle)
    }

    private fun getDefaultName(vehicle: Vehicle): String {
        return "${vehicle.brand} ${vehicle.model} ${vehicle.version}"
    }

    class VehicleListViewModelFactory : ViewModelProvider.NewInstanceFactory(){
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return VehicleListViewModel() as T
        }
    }
}