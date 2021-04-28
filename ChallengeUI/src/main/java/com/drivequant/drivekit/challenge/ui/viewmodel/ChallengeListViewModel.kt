package com.drivequant.drivekit.challenge.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.challenge.ChallengesQueryListener
import com.drivequant.drivekit.challenge.ChallengesSyncStatus
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.challenge.ui.ChallengeData
import com.drivequant.drivekit.databaseutils.entity.Challenge
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus

class ChallengeListViewModel : ViewModel() {

    var syncStatus: ChallengesSyncStatus = ChallengesSyncStatus.SUCCESS
    var challengesData = mutableListOf<ChallengeData>()
    var mutableLiveDataChallengesData: MutableLiveData<List<ChallengeData>> = MutableLiveData()
    var selectedChallengeStatusData: ChallengeStatusData
    var challengesStatusData = mutableListOf<ChallengeStatusData>()

    init {
        challengesStatusData.add(ChallengeStatusData("", ChallengeStatus.PENDING))
        challengesStatusData.add(ChallengeStatusData("", ChallengeStatus.FINISHED))

        selectedChallengeStatusData = challengesStatusData.first()
    }

    fun fetchChallengeList() {
        DriveKitChallenge.getChallenges(object : ChallengesQueryListener {
            override fun onResponse(
                challengesSyncStatus: ChallengesSyncStatus,
                challenges: List<Challenge>) {
                syncStatus = challengesSyncStatus
                challengesData = buildChallengeListData(challenges)
                mutableLiveDataChallengesData.postValue(challengesData)
            }
        })
    }

    fun buildChallengeListData(challengeList: List<Challenge>): MutableList<ChallengeData> {
        challengesData.clear()
        return challengeList.map {
            ChallengeData(
                it.challengeId,
                it.title,
                it.description,
                it.conditionsDescription,
                it.startDate,
                it.endDate,
                it.conditions,
                it.rankKey,
                it.themeCode,
                it.iconCode,
                it.type,
                it.isRegistered,
                it.conditionsFilled,
                it.driverConditions,
                it.groups,
                it.rules,
                it.optinText,
                it.status
            )
        }.toMutableList()
    }
}