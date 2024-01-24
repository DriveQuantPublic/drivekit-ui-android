package com.drivequant.drivekit.challenge.ui.challengelist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.challenge.ChallengesQueryListener
import com.drivequant.drivekit.challenge.ChallengesSyncStatus
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.challenge.ui.challengelist.model.ChallengeListCategory
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorViewModel
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.extension.CalendarField
import com.drivequant.drivekit.core.extension.startingFrom
import com.drivequant.drivekit.databaseutils.entity.Challenge
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import java.util.Date

class ChallengeListViewModel : ViewModel() {

    val dateSelectorViewModel = DKDateSelectorViewModel()
    private var selectedDate: Date? = null

    var selectedCategory: ChallengeListCategory = ChallengeListCategory.RANKED //TEST
        private set

    var challenges: List<ChallengeData> = listOf()

    var activeChallenges = mutableListOf<ChallengeData>()
    var userRankedChallenges = mutableListOf<ChallengeData>()
    var finishedChallenges = mutableListOf<ChallengeData>()


    val syncStatus = MutableLiveData<Any>()
    val updateData = MutableLiveData<Any>()
    var syncChallengesError: MutableLiveData<Boolean> = MutableLiveData()
        private set

    init {
        configureDateSelector()

        // Get local challenges
        DriveKitChallenge.getChallenges(object : ChallengesQueryListener {
            override fun onResponse(
                challengesSyncStatus: ChallengesSyncStatus,
                challenges: List<Challenge>
            ) {
                if (challengesSyncStatus == ChallengesSyncStatus.CACHE_DATA_ONLY) {
                    this@ChallengeListViewModel.challenges = buildChallengeListData(challenges)
                    update()
                }
            }
        }, SynchronizationType.CACHE)

        updateData()
    }

    fun updateData() {
        // Synchronize challenge list from DriveQuant servers
        DriveKitChallenge.getChallenges(object : ChallengesQueryListener {
            override fun onResponse(
                challengesSyncStatus: ChallengesSyncStatus,
                challenges: List<Challenge>
            ) {
                this@ChallengeListViewModel.challenges = buildChallengeListData(challenges)
                update(true)
                syncStatus.postValue(Any())
            }
        })
    }

    private fun update(resettingSelectedDate: Boolean = false) {
        if (resettingSelectedDate) {
            this.selectedDate = null
        }

        // Get local challenges for current Tab
        val currentChallenges = getChallengesByCategory()

        if (currentChallenges.isEmpty()) {
            //configureWithNoData()
        } else {
            // Get all selectable years for current challenges
            val dates = computeChallengeYearList(currentChallenges).toList()

            if (dates.isNotEmpty()) {
                val selectedDateIndex: Int = this.selectedDate?.let {
                    val index = dates.indexOf(it)
                    if (index < 0) {
                        null
                    } else {
                        index
                    }
                } ?: (dates.size - 1)

                val date = dates[selectedDateIndex]
                this.selectedDate = date
                this.dateSelectorViewModel.configure(dates, selectedDateIndex, DKPeriod.YEAR)

                // TODO update something
            } else {
                val date = Date().startingFrom(CalendarField.YEAR)
                this.selectedDate = date
                this.dateSelectorViewModel.configure(listOf(date), 0, DKPeriod.YEAR)

                // TODO update something
            }
        }
        this.updateData.postValue(Any())
    }

    private fun configureDateSelector() {
        configureEmptyDateSelector()
        this.dateSelectorViewModel.onDateSelected = { date ->
            updateSelectedDate(date)
        }
    }

    private fun configureEmptyDateSelector() {
        val startDate = Date().startingFrom(CalendarField.YEAR)
        dateSelectorViewModel.configure(listOf(startDate), 0, DKPeriod.YEAR)
    }

    fun updateSelectedCategory(category: ChallengeListCategory) {
        if (this.selectedCategory != category) {
            this.selectedCategory = category
            update()
        }
    }

    private fun updateSelectedDate(date: Date) {
        if (this.selectedDate != date) {
            this.selectedDate = date
            update()
        }
    }

    private fun getChallengesByCategory() = when (this.selectedCategory) {
        ChallengeListCategory.ACTIVE -> this.challenges.filter { it.status == ChallengeStatus.SCHEDULED || it.status == ChallengeStatus.PENDING }
        ChallengeListCategory.RANKED -> this.challenges.filter { it.rank > 0 }
        ChallengeListCategory.ALL -> this.challenges
    }

    private fun computeChallengeYearList(challenges: List<ChallengeData>): Set<Date> {
        val dates = mutableSetOf<Date>()
        challenges.forEach {
            dates.add(it.startDate.startingFrom(CalendarField.YEAR))
            dates.add(it.endDate.startingFrom(CalendarField.YEAR))
        }
        return dates.sortedBy { it.time }.toSet()
    }

    /*fun filterChallenges() {
        activeChallenges.clear()
        finishedChallenges.clear()
        for (challengeData in challengeListData) {
            if (challengeData.status.isActiveChallenge()) {
                activeChallenges.add(challengeData)
            } else {
                finishedChallenges.add(challengeData)
            }
        }
    }*/

    /*fun fetchChallengeList(synchronizationType: SynchronizationType = SynchronizationType.DEFAULT) {
        if (DriveKit.isConfigured()) {
            DriveKitChallenge.getChallenges(object : ChallengesQueryListener {
                override fun onResponse(
                    challengesSyncStatus: ChallengesSyncStatus,
                    challenges: List<Challenge>) {
                    val value = when (challengesSyncStatus) {
                        ChallengesSyncStatus.CACHE_DATA_ONLY,
                        ChallengesSyncStatus.SUCCESS,
                        ChallengesSyncStatus.SYNC_ALREADY_IN_PROGRESS -> true
                        ChallengesSyncStatus.FAILED_TO_SYNC_CHALLENGES_CACHE_ONLY -> false
                    }
                    challengeListData = buildChallengeListData(challenges)
                    syncChallengesError.postValue(value)
                }
            }, synchronizationType)
        } else {
            syncChallengesError.postValue(false)
        }
    }*/

    private fun buildChallengeListData(challengeList: List<Challenge>): MutableList<ChallengeData> {
        this.challenges = listOf()
        //challengeListData.clear()
        return challengeList.map {
            ChallengeData(
                it.challengeId,
                it.title,
                it.description,
                it.conditionsDescription,
                it.startDate,
                it.endDate,
                it.rank,
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
