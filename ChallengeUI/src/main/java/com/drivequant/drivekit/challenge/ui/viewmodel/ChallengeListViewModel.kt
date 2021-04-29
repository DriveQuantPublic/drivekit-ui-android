package com.drivequant.drivekit.challenge.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.challenge.ChallengesQueryListener
import com.drivequant.drivekit.challenge.ChallengesSyncStatus
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.databaseutils.entity.Challenge
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus

class ChallengeListViewModel : ViewModel() {

    var syncStatus: ChallengesSyncStatus = ChallengesSyncStatus.SUCCESS
    var challengeListData = mutableListOf<ChallengeListData>()
    var mutableLiveDataChallengesData: MutableLiveData<List<ChallengeListData>> = MutableLiveData()
    var selectedChallengeStatusData: ChallengeStatusData
    var challengesStatusData = mutableListOf<ChallengeStatusData>()
    var filteredChallenge = mutableListOf<ChallengeListData>()

    init {
        challengesStatusData.add(ChallengeStatusData("dk_challenge_active", ChallengeStatus.PENDING))
        challengesStatusData.add(ChallengeStatusData("dk_challenge_finished", ChallengeStatus.FINISHED))
        selectedChallengeStatusData = challengesStatusData.first()
    }

    fun filterChallenges(challengeStatus: ChallengeStatus) {
        filteredChallenge.clear()
        filteredChallenge = challengeListData.filter { it.status == challengeStatus }.toMutableList()
        mutableLiveDataChallengesData.postValue(filteredChallenge)
    }

    fun fetchChallengeList() {
        DriveKitChallenge.getChallenges(object : ChallengesQueryListener {
            override fun onResponse(
                challengesSyncStatus: ChallengesSyncStatus,
                challenges: List<Challenge>
            ) {
                syncStatus = challengesSyncStatus
                challengeListData = buildChallengeListData(challenges)
                filterChallenges(selectedChallengeStatusData.challengeStatus)
            }
        })
    }

    fun buildChallengeListData(challengeList: List<Challenge>): MutableList<ChallengeListData> {
        challengeListData.clear()
        return challengeList.map {
            ChallengeListData(
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