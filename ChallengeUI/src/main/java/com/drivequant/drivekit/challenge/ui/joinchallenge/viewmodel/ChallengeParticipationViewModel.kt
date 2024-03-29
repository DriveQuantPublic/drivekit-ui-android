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

class ChallengeParticipationViewModel(private val challengeId: String) : ViewModel() {

    val challenge: Challenge?
        get() = DbChallengeAccess.findChallengeById(challengeId)
    var syncJoinChallengeError: MutableLiveData<Boolean> = MutableLiveData()
        private set

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
        return DbChallengeAccess.findChallengeById(challengeId)?.let {
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

    fun computeDriverProgress(driverCondition: String, condition: String) =
        (driverCondition.toDouble().div(condition.toDouble()) * 100).toInt()

    fun isChallengeStarted() = challenge?.let { Date().after(it.startDate) } ?: run { false }

    fun getTimeLeft(): Long = challenge?.let { it.startDate.time - Date().time } ?: 0

    fun getDateRange() =
        "${challenge?.startDate?.formatDate(DKDatePattern.STANDARD_DATE)} - ${challenge?.endDate?.formatDate(
            DKDatePattern.STANDARD_DATE
        )}"

    fun isDriverRegistered() = challenge?.isRegistered ?: false

    @Suppress("UNCHECKED_CAST")
    class ChallengeParticipationViewModelFactory(private val challengeId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChallengeParticipationViewModel(challengeId) as T
        }
    }
}
