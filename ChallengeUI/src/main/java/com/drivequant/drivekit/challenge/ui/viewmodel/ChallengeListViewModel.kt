package com.drivequant.drivekit.challenge.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.challenge.ChallengesQueryListener
import com.drivequant.drivekit.challenge.ChallengesSyncStatus
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.databaseutils.entity.Challenge
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus

class ChallengeListViewModel : ViewModel() {

    private var challengeListData = mutableListOf<ChallengeData>()
    var mutableLiveDataChallengesData: MutableLiveData<List<ChallengeData>> = MutableLiveData()
    var selectedChallengeStatusData: ChallengeStatusData
    var challengesStatusData = mutableListOf<ChallengeStatusData>()
    var filteredChallenge = mutableListOf<ChallengeData>()
    var syncChallengesError: MutableLiveData<Any> = MutableLiveData()
        private set

    init {
        challengesStatusData.add(
            ChallengeStatusData(
                "dk_challenge_active",
                listOf(ChallengeStatus.PENDING, ChallengeStatus.SCHEDULED)
            )
        )
        challengesStatusData.add(
            ChallengeStatusData(
                "dk_challenge_finished",
                listOf(ChallengeStatus.FINISHED, ChallengeStatus.ARCHIVED)
            )
        )
        selectedChallengeStatusData = challengesStatusData.first()
    }

    fun filterChallenges(statusList: List<ChallengeStatus>) {
        filteredChallenge.clear()
        for (challengeData in challengeListData) {
            for (status in statusList) {
                if (status == challengeData.status) {
                    filteredChallenge.add(challengeData)
                }
            }
        }
        mutableLiveDataChallengesData.postValue(filteredChallenge)
    }

    fun fetchChallengeList() {
        if (DriveKit.isConfigured()) {
            DriveKitChallenge.getChallenges(object : ChallengesQueryListener {
                override fun onResponse(
                    challengesSyncStatus: ChallengesSyncStatus,
                    challenges: List<Challenge>
                ) {
                    if (challengesSyncStatus == ChallengesSyncStatus.FAILED_TO_SYNC_CHALLENGES_CACHE_ONLY) {
                        syncChallengesError.postValue(Any())
                    }
                    challengeListData = buildChallengeListData(challenges)
                    filterChallenges(selectedChallengeStatusData.statusList)
                }
            })
        } else {
            syncChallengesError.postValue(Any())
        }
    }

    fun buildChallengeListData(challengeList: List<Challenge>): MutableList<ChallengeData> {
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