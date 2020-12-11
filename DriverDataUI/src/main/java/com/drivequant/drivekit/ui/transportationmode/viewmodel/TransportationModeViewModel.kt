package com.drivequant.drivekit.ui.transportationmode.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.text.Spanned
import android.text.TextUtils
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.trip.TransportationModeQueryListener
import com.drivequant.drivekit.driverdata.trip.TransportationModeUpdateStatus
import com.drivequant.drivekit.ui.extension.text

internal class TransportationModeViewModel(private val itinId: String) : ViewModel() {
    val updateObserver: MutableLiveData<TransportationModeUpdateStatus> = MutableLiveData()
    var selectedTransportationMode: TransportationMode? = null
    var selectedProfileDriver: TransportationProfile? = null
    var comment: String = ""

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
        return TextUtils.isEmpty(comment) || comment.length <= 120
    }

    fun updateInformations() {
        selectedTransportationMode?.let {
            val passenger = if (selectedProfileDriver == null){
                false
            } else {
                selectedProfileDriver == TransportationProfile.DRIVER
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
            context, DriveKitUI.colors.mainFontColor(), DriveKitUI.colors.mainFontColor(),
            "dk_driverdata_transportation_mode",
            " ${selectedTransportationMode?.text(context)}"
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