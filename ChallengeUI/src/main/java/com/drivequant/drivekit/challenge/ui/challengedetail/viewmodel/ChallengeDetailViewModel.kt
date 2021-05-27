package com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.*
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.databaseutils.entity.ChallengeDetail
import com.drivequant.drivekit.databaseutils.entity.Trip

internal class ChallengeDetailViewModel(private val challengeId: String) : ViewModel() {

    var syncChallengeDetailError: MutableLiveData<Boolean> = MutableLiveData()
        private set

    var challengeDetailData: ChallengeDetail? = null
    var challengeTrips = listOf<Trip>()

    fun fetchChallengeDetail(challengeId: String) {
        if (DriveKit.isConfigured()) {
            DriveKitChallenge.getChallengeDetail(
                challengeId,
                object : ChallengeDetailQueryListener {
                    override fun onResponse(
                        challengeDetailSyncStatus: ChallengeDetailSyncStatus,
                        challengeDetail: ChallengeDetail?,
                        trips: List<Trip>
                    ) {
                        challengeDetailData = challengeDetail
                        challengeTrips = trips
                    }
                })
        } else {
            syncChallengeDetailError.postValue(false)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class ChallengeDetailViewModelFactory(private val challengeId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChallengeDetailViewModel(challengeId) as T
        }
    }
}