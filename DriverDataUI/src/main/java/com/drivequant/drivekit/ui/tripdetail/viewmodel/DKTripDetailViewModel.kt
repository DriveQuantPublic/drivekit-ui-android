package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.arch.lifecycle.MutableLiveData

interface DKTripDetailViewModel {
    fun setSelectedEvent(position: Int)
    fun getTripEvents(): List<TripEvent>
    fun getSelectedEvent(): MutableLiveData<Int>
}