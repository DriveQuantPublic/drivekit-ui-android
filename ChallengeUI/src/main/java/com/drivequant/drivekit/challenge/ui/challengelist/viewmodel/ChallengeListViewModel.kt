package com.drivequant.drivekit.challenge.ui.challengelist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.challenge.ChallengesQueryListener
import com.drivequant.drivekit.challenge.ChallengesSyncStatus
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.databaseutils.entity.Challenge

class ChallengeListViewModel : ViewModel() {

    private var challengeListData = mutableListOf<ChallengeData>()
    var activeChallenges = mutableListOf<ChallengeData>()
    var finishedChallenges = mutableListOf<ChallengeData>()
    var syncChallengesError: MutableLiveData<Boolean> = MutableLiveData()
        private set

    fun filterChallenges() {
        activeChallenges.clear()
        finishedChallenges.clear()
        for (challengeData in challengeListData) {
            if (challengeData.status.isActiveChallenge()) {
                activeChallenges.add(challengeData)
            } else {
                finishedChallenges.add(challengeData)
            }
        }
    }

    fun fetchChallengeList() {
        if (DriveKit.isConfigured()) {
            DriveKitChallenge.getChallenges(object : ChallengesQueryListener {
                override fun onResponse(
                    challengesSyncStatus: ChallengesSyncStatus,
                    challenges: List<Challenge>) {
                    if (challengesSyncStatus != ChallengesSyncStatus.SYNC_ALREADY_IN_PROGRESS) {
                        challengeListData = buildChallengeListData(challenges)
                    }
                    val value = when (challengesSyncStatus) {
                        ChallengesSyncStatus.CACHE_DATA_ONLY,
                        ChallengesSyncStatus.SUCCESS -> true
                        ChallengesSyncStatus.SYNC_ALREADY_IN_PROGRESS,ChallengesSyncStatus.FAILED_TO_SYNC_CHALLENGES_CACHE_ONLY -> false
                    }
                    syncChallengesError.postValue(value)
                }
            })
        } else {
            syncChallengesError.postValue(false)
        }
    }

    private fun buildChallengeListData(challengeList: List<Challenge>): MutableList<ChallengeData> {
        challengeListData.clear()
        return challengeList.map {
            ChallengeData(
                it.challengeId,
                it.title,
                it.description,
                it.conditionsDescription,
                it.startDate,
                it.endDate,
                it.rankKey,
                it.themeCode,
                it.iconCode,
                it.type,
                it.isRegistered,
                it.conditionsFilled,
                it.driverConditions,
                it.groups,
                it.rules,
                it.status
            )
        }.toMutableList()
    }
}