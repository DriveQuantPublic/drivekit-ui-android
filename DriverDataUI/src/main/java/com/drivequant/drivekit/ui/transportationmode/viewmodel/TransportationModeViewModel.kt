package com.drivequant.drivekit.ui.transportationmode.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.text.Spanned
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.dbtripaccess.DbTripAccess
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.trip.TransportationModeQueryListener
import com.drivequant.drivekit.driverdata.trip.TransportationModeUpdateStatus
import com.drivequant.drivekit.ui.extension.text

internal class TransportationModeViewModel(private val itinId: String) : ViewModel() {
    var trip: Trip?
        private set
    val updateObserver: MutableLiveData<TransportationModeUpdateStatus> = MutableLiveData()
    var selectedTransportationMode: TransportationMode? = null
    var selectedProfileDriver: TransportationProfile? = null
    var comment: String = ""

    init {
        trip = DbTripAccess.findTrip(itinId).executeOneTrip()?.toTrip()
    }

    fun displayPassengerOption(): Boolean {
        return selectedTransportationMode == TransportationMode.CAR
    }

    fun checkFieldsValidity(): Boolean {
        return if (displayPassengerOption()) {
            selectedTransportationMode != null && selectedProfileDriver != null
        } else {
            selectedTransportationMode != null
        }
    }

    fun isCommentValid(comment: String): Boolean {
        return comment.length <= 120
    }

    fun updateInformations() {
        selectedTransportationMode?.let {
            val passenger = if (selectedProfileDriver == null) {
                false
            } else {
                selectedProfileDriver == TransportationProfile.PASSENGER
            }
            DriveKitDriverData.declareTransportationMode(
                itinId,
                it,
                passenger,
                comment,
                object : TransportationModeQueryListener {
                    override fun onResponse(status: TransportationModeUpdateStatus) {
                        updateObserver.postValue(status)
                    }
                })
        }
    }

    fun buildTransportationModes(): List<TransportationMode> {
        val list = TransportationMode.values().asList().toMutableList()
        list.remove(TransportationMode.UNKNOWN)
        return list
    }

    fun buildTransportationProfiles(): List<TransportationProfile> {
        return TransportationProfile.values().asList()
    }

    fun buildSelectedTransportationModeTitle(context: Context): Spanned {
        return DKResource.buildString(
            context, DriveKitUI.colors.mainFontColor(), DriveKitUI.colors.primaryColor(),
            "dk_driverdata_transportation_mode",
            " ${selectedTransportationMode.text(context)}"
        )
    }

    @Suppress("UNCHECKED_CAST")
    class TransportationModeViewModelFactory(private val itinId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TransportationModeViewModel(itinId) as T
        }
    }
}