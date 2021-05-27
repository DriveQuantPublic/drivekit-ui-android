package com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import androidx.core.text.toSpannable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ChallengeDetailQueryListener
import com.drivequant.drivekit.challenge.ChallengeDetailSyncStatus
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRankingItem
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Challenge
import com.drivequant.drivekit.databaseutils.entity.ChallengeDetail
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.dbchallengeaccess.DbChallengeAccess

class ChallengeDetailViewModel(private val challengeId: String) : ViewModel() {

    var syncChallengeDetailError: MutableLiveData<Boolean> = MutableLiveData()
    var challengeDetail: MutableLiveData<ChallengeDetail> = MutableLiveData()
        private set
    var challengeDetailData: ChallengeDetail? = null
    var challengeTrips = listOf<Trip>()
    var useCache = false
    private lateinit var challenge: Challenge

    init {
        DbChallengeAccess.findChallengeById(challengeId)?.let {
            challenge = it
        }
    }

    fun getChallengeDetail() = challengeDetail.postValue(challengeDetailData)

    fun getChallengeId() = challengeId

    fun fetchChallengeDetail() {
        val synchronizationType =
            if (useCache) SynchronizationType.CACHE else SynchronizationType.DEFAULT

        if (DriveKit.isConfigured()) {
            DriveKitChallenge.getChallengeDetail(
                challengeId,
                object : ChallengeDetailQueryListener {
                    override fun onResponse(
                        challengeDetailSyncStatus: ChallengeDetailSyncStatus,
                        challengeDetail: ChallengeDetail?,
                        trips: List<Trip>) {
                        useCache = when (challengeDetailSyncStatus) {
                            ChallengeDetailSyncStatus.CACHE_DATA_ONLY,
                            ChallengeDetailSyncStatus.SUCCESS -> {
                                challengeDetailData = challengeDetail
                                challengeTrips = trips
                                syncChallengeDetailError.postValue(true)
                                true
                            }
                            ChallengeDetailSyncStatus.CHALLENGE_NOT_FOUND,
                            ChallengeDetailSyncStatus.FAILED_TO_SYNC_CHALLENGE_DETAIL_CACHE_ONLY,
                            ChallengeDetailSyncStatus.SYNC_ALREADY_IN_PROGRESS -> {
                                syncChallengeDetailError.postValue(false)
                                false
                            }
                        }
                    }
                }, synchronizationType
            )
        } else {
            syncChallengeDetailError.postValue(false)
        }
    }

    fun getTripData() = when (challenge.themeCode) {
        in 101..104 -> TripData.ECO_DRIVING
        in 201..216 -> TripData.SAFETY
        in 301..305 -> TripData.DISTANCE
        in 306..309 -> TripData.DURATION
        221 -> TripData.DISTRACTION
        else -> TripData.SAFETY
    }

    fun getScoreTitle() = when(challenge.themeCode) {
        in 101..104,in 201..216,221 -> "Score"
        in 301..305 -> "Distance"
        in 306..309 -> "Duration"
        else -> "Score"
    }

    fun getDriverGlobalRank(context: Context) =
        if (challengeDetailData?.driverStats?.rank?.toInt() == 0) {
            "-"
        } else {
            "${challengeDetailData?.driverStats?.rank?.toInt()}"
        }.let {
            DKSpannable().append(it, context.resSpans {
                color(DriveKitUI.colors.secondaryColor())
                size(R.dimen.dk_text_xbig)
            }).append(" / ", context.resSpans {
                color(DriveKitUI.colors.mainFontColor())
                size(R.dimen.dk_text_xbig)
            }).append(
                "${challengeDetailData?.nbDriverRanked}", context.resSpans {
                    color(DriveKitUI.colors.mainFontColor())
                    size(R.dimen.dk_text_xbig)
                }
            )
                .toSpannable()
        }

    fun getRankingList(): List<ChallengeRankingItem> {
        val listOfRanking = mutableListOf<ChallengeRankingItem>()
        challengeDetailData?.let { challengeDetail ->
            challengeDetail.driversRanked?.let { list ->
                for (driver in list) {
                    listOfRanking.add(
                        ChallengeRankingItem(
                            driver.rank,
                            driver.pseudo,
                            driver.distance,
                            driver.score,
                            driver.userId,
                            false
                        )
                    )
                }
            }
        }
        return listOfRanking
    }

    @Suppress("UNCHECKED_CAST")
    class ChallengeDetailViewModelFactory(private val challengeId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChallengeDetailViewModel(challengeId) as T
        }
    }
}