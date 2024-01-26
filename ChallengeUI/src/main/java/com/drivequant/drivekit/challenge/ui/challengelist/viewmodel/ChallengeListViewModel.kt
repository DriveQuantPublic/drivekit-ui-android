package com.drivequant.drivekit.challenge.ui.challengelist.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.challenge.ChallengesQueryListener
import com.drivequant.drivekit.challenge.ChallengesSyncStatus
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengelist.model.ChallengeListCategory
import com.drivequant.drivekit.challenge.ui.challengelist.model.ChallengeState
import com.drivequant.drivekit.challenge.ui.challengelist.model.getChallengeState
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorViewModel
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.extension.CalendarField
import com.drivequant.drivekit.core.extension.startingFrom
import com.drivequant.drivekit.databaseutils.entity.Challenge
import com.drivequant.drivekit.databaseutils.entity.ChallengeType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import java.util.Date

class ChallengeListViewModel : ViewModel() {

    val dateSelectorViewModel = DKDateSelectorViewModel()
    private var selectedDate: Date? = null

    private var selectedCategory: ChallengeListCategory = ChallengeListCategory.ACTIVE

    private var sourceChallenges: List<ChallengeData> = listOf()
    var currentChallenges: List<ChallengeData> = listOf()
        private set
    val hasChallengesToDisplay: Boolean
        get() = this.currentChallenges.isNotEmpty()

    private var userHasAlreadyRegistered: Boolean = false
    private var userHasAlreadyRanked: Boolean = false

    val syncStatus = MutableLiveData<Boolean>()
    val updateData = MutableLiveData<Any>()

    init {
        configureDateSelector()
        updateLocalData()
        synchronizeChallenges()
    }

    fun updateLocalData() {
        DriveKitChallenge.getChallenges(object : ChallengesQueryListener {
            override fun onResponse(
                challengesSyncStatus: ChallengesSyncStatus,
                challenges: List<Challenge>
            ) {
                if (challengesSyncStatus == ChallengesSyncStatus.CACHE_DATA_ONLY) {
                    buildChallengeListData(challenges)
                    update()
                }
            }
        }, SynchronizationType.CACHE)
    }

    fun synchronizeChallenges() {
        DriveKitChallenge.getChallenges(object : ChallengesQueryListener {
            override fun onResponse(
                challengesSyncStatus: ChallengesSyncStatus,
                challenges: List<Challenge>
            ) {
                buildChallengeListData(challenges)
                update(true)
                val success = challengesSyncStatus != ChallengesSyncStatus.FAILED_TO_SYNC_CHALLENGES_CACHE_ONLY
                syncStatus.postValue(success)
            }
        })
    }

    @StringRes
    fun computeNoChallengeTextResId(): Int {
        return when (this.selectedCategory) {
            ChallengeListCategory.ACTIVE -> R.string.dk_challenge_no_active_challenge

            ChallengeListCategory.RANKED -> {
                if (!this.userHasAlreadyRegistered) {
                    R.string.dk_challenge_no_ranked_challenge_not_registered_yet
                } else if (!this.userHasAlreadyRanked) {
                    R.string.dk_challenge_no_ranked_challenge_not_ranked_yet
                } else {
                    R.string.dk_challenge_no_ranked_challenge_empty_list
                }
            }

            ChallengeListCategory.ALL -> R.string.dk_challenge_no_all_challenge
        }
    }

    fun updateSelectedCategory(category: ChallengeListCategory) {
        if (this.selectedCategory != category) {
            this.selectedCategory = category
            update(resettingSelectedDate = true)
        }
    }

    @StringRes
    fun getScreenTagResId() = when (this.selectedCategory) {
        ChallengeListCategory.ACTIVE -> R.string.dk_tag_challenge_list_active
        ChallengeListCategory.RANKED -> R.string.dk_tag_challenge_list_ranked
        ChallengeListCategory.ALL -> R.string.dk_tag_challenge_list_all
    }

    private fun update(resettingSelectedDate: Boolean = false) {
        if (resettingSelectedDate) {
            this.selectedDate = null
        }

        // Get local challenges for current Tab
        val challengesByCategory = getChallengesByCategory()

        val dates = computeChallengeYearList(challengesByCategory).toList()
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
            this.currentChallenges = challengesByCategory.filter { it.startAndEndYear.contains(this.selectedDate) }
            this.dateSelectorViewModel.configure(dates, selectedDateIndex, DKPeriod.YEAR)
        } else {
            val date = Date().startingFrom(CalendarField.YEAR)
            this.selectedDate = date
            this.currentChallenges = challengesByCategory.filter { it.startAndEndYear.contains(this.selectedDate) }
            this.dateSelectorViewModel.configure(listOf(date), 0, DKPeriod.YEAR)
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

    private fun updateSelectedDate(date: Date) {
        if (this.selectedDate != date) {
            this.selectedDate = date
            update()
        }
    }

    private fun getChallengesByCategory() = when (this.selectedCategory) {
        ChallengeListCategory.ACTIVE -> this.sourceChallenges.filter { it.state == ChallengeState.ACTIVE }
        ChallengeListCategory.RANKED -> this.sourceChallenges.filter { it.isRanked }
        ChallengeListCategory.ALL -> this.sourceChallenges
    }

    private fun computeChallengeYearList(challenges: List<ChallengeData>): Set<Date> {
        val dates = mutableSetOf<Date>()
        challenges.forEach { dates.addAll(it.startAndEndYear) }
        return dates.sortedBy { it.time }.toSet()
    }

    private fun buildChallengeListData(challengeList: List<Challenge>) {
        this.sourceChallenges = filterChallengeTypes(challengeList).map {
            ChallengeData(
                it.challengeId,
                it.title,
                it.startDate,
                it.endDate,
                setOf(
                    it.startDate.startingFrom(CalendarField.YEAR),
                    it.endDate.startingFrom(CalendarField.YEAR)
                ),
                it.rank,
                isRanked = it.rank > 0,
                it.challengeType,
                it.isRegistered,
                it.conditionsFilled,
                it.getChallengeState(),
                it.status,
                it.nbDriverRegistered,
            )
        }

        this.userHasAlreadyRegistered = this.sourceChallenges.any { it.isRegistered }
        this.userHasAlreadyRanked = this.sourceChallenges.any { it.isRegistered && it.isRanked }
    }

    // Do not display `DEPRECATED` and `UNKNOWN` challenge types
    private fun filterChallengeTypes(challenges: List<Challenge>): List<Challenge> =
        challenges.filterNot { it.challengeType == ChallengeType.DEPRECATED || it.challengeType == ChallengeType.UNKNOWN }
}
