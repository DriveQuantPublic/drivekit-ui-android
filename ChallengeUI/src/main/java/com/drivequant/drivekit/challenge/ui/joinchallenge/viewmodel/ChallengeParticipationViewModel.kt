package com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.challenge.JoinChallengeQueryListener
import com.drivequant.drivekit.challenge.JoinChallengeSyncStatus
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.databaseutils.entity.Challenge
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus
import com.drivequant.drivekit.dbchallengeaccess.DbChallengeAccess
import java.util.*

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

    fun manageChallengeDisplayState(): ChallengeParticipationDisplayState? {
        return challenge?.let {
            if (it.status == ChallengeStatus.SCHEDULED && it.isRegistered) {
                ChallengeParticipationDisplayState.COUNT_DOWN
            } else if (it.status == ChallengeStatus.PENDING && it.isRegistered && !it.conditionsFilled) {
                ChallengeParticipationDisplayState.PROGRESS

            } else if ((it.status == ChallengeStatus.PENDING || it.status == ChallengeStatus.SCHEDULED) && !it.isRegistered) {
                ChallengeParticipationDisplayState.JOIN
            } else {
                null
            }
        }
    }

    fun isChallengeStarted() = challenge?.let { it.startDate.after(it.endDate) } ?: run { false }

    fun getTimeLeft(): Long = challenge?.let { it.startDate.time - Date().time } ?: 0

    fun getRules() = challenge?.rules

    fun getConditionsDescription() = challenge?.conditionsDescription

    fun getDescription() = challenge?.description

    fun getTitle() = challenge?.title

    fun getDateRange() =
        "${challenge?.startDate?.formatDate(DKDatePattern.STANDARD_DATE)} - ${challenge?.endDate?.formatDate(
            DKDatePattern.STANDARD_DATE
        )}"

    fun isDriverRegistered() = challenge?.isRegistered ?: false

    @Suppress("UNCHECKED_CAST")
    class ChallengeParticipationViewModelFactory(private val challengeId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChallengeParticipationViewModel(challengeId) as T
        }
    }
}