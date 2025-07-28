package com.drivequant.drivekit.ui.driverpassengermode.viewmodel

import android.content.Context
import android.text.Spanned
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.dbtripaccess.DbTripAccess
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.trip.TransportationModeUpdateStatus
import com.drivequant.drivekit.driverdata.trip.driverpassengermode.DriverPassengerMode
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.text

internal class DriverPassengerModeViewModel(private val itinId: String) : ViewModel() {
    var trip: Trip?
        private set
    val updateObserver: MutableLiveData<TransportationModeUpdateStatus> = MutableLiveData()
    var selectedTransportationMode: TransportationMode? = null
    var selectedDriverPassengerMode: DriverPassengerMode? = null
    var comment: String = ""

    init {
        trip = DbTripAccess.findTrip(itinId).executeOneTrip()?.toTrip()
    }

    fun checkFieldsValidity(): Boolean =
        selectedDriverPassengerMode != null || selectedTransportationMode != null

    fun isCommentValid(comment: String) = comment.length <= 120

    fun updateInformations() {
        selectedTransportationMode?.let {
            val passenger = if (selectedDriverPassengerMode == null) {
                false
            } else {
                selectedDriverPassengerMode == DriverPassengerMode.PASSENGER
            }
            DriveKitDriverData.updateDriverPassengerMode(
                itinId,
                selectedDriverPassengerMode ?: DriverPassengerMode.PASSENGER,
                comment
            ) { status ->

                // TODO do something
            }
        }
    }

    fun buildTransportationModes(): List<TransportationMode> {
        val list = TransportationMode.values().asList().toMutableList()
        list.remove(TransportationMode.UNKNOWN)
        return list
    }

    fun buildDriverPassengerModes(): List<DriverPassengerMode> {
        return DriverPassengerMode.values().asList()
    }

    fun buildSelectedTransportationModeTitle(context: Context): Spanned {
        return DKResource.buildString(
            context, DKColors.mainFontColor, DKColors.primaryColor,
            R.string.dk_driverdata_transportation_mode,
            " ${selectedTransportationMode.text(context)}"
        )
    }

    @Suppress("UNCHECKED_CAST")
    class DriverPassengerModeViewModelFactory(private val itinId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DriverPassengerModeViewModel(itinId) as T
        }
    }
}
