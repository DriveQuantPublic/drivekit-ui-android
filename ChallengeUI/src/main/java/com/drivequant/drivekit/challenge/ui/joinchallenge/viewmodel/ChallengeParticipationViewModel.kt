package com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.challenge.JoinChallengeQueryListener
import com.drivequant.drivekit.challenge.JoinChallengeSyncStatus
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.databaseutils.entity.Challenge
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus
import com.drivequant.drivekit.dbchallengeaccess.DbChallengeAccess

internal class ChallengeParticipationViewModel(challengeId: String) : ViewModel() {

    var challenge: Challenge? = null
    var syncJoinChallengeError: MutableLiveData<Boolean> = MutableLiveData()
        private set

    init {
        DbChallengeAccess.findChallengeById(challengeId)?.let {
            challenge = it
        }
    }

    fun joinChallenge(challengeId: String) {
        if (DriveKit.isConfigured()) {
            DriveKitChallenge.joinChallenge(challengeId, object : JoinChallengeQueryListener {
                override fun onResponse(joinChallengeSyncStatus: JoinChallengeSyncStatus) {
                    if (joinChallengeSyncStatus != JoinChallengeSyncStatus.JOIN_SUCCESS) {
                        syncJoinChallengeError.postValue(false)
                    } else {
                        syncJoinChallengeError.postValue(true)
                    }
                }
            })
        } else {
            syncJoinChallengeError.postValue(false)
        }
    }

    fun shouldDisplayJoinChallenge(): Boolean {
        return challenge?.let {
            (it.status == ChallengeStatus.PENDING || it.status == ChallengeStatus.SCHEDULED) && !it.isRegistered
        } ?: run {
            false
        }
    }

    fun shouldDisplayCountDown(): Boolean {
        return challenge?.let {
            it.status == ChallengeStatus.SCHEDULED && it.isRegistered
        } ?: run {
            false
        }
    }

    fun shouldDisplayProgressBars(): Boolean {
        return challenge?.let {
            it.status == ChallengeStatus.PENDING && it.isRegistered && !it.conditionsFilled
        } ?: run {
            false
        }
    }

    @Suppress("UNCHECKED_CAST")
    class ChallengeParticipationViewModelFactory(private val challengeId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChallengeParticipationViewModel(challengeId) as T
        }
    }
}