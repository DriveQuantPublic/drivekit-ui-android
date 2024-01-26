package com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel

import android.content.Context
import android.graphics.Typeface.BOLD
import android.text.Spannable
import android.text.SpannableString
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ChallengeDetailQueryListener
import com.drivequant.drivekit.challenge.ChallengeDetailSyncStatus
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DriverProgression
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.extension.capitalizeFirstLetter
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter.formatMeterDistanceInKm
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.FormatType
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Challenge
import com.drivequant.drivekit.databaseutils.entity.ChallengeDetail
import com.drivequant.drivekit.databaseutils.entity.ChallengeType
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.dbchallengeaccess.DbChallengeAccess
import kotlin.math.roundToInt

class ChallengeDetailViewModel(val challengeId: String) : ViewModel() {

    var syncChallengeDetailError: MutableLiveData<Boolean> = MutableLiveData()
    var challengeDetailData: ChallengeDetail? = null
    var challengeDetailTrips = listOf<Trip>()
    lateinit var challenge: Challenge

    init {
        DbChallengeAccess.findChallengeById(challengeId)?.let {
            challenge = it
        }
    }

    private fun isChallengeManaged(): Boolean = (challenge.challengeType != ChallengeType.DEPRECATED && challenge.challengeType != ChallengeType.UNKNOWN)

    fun getLocalChallengeDetail(): ChallengeDetail? {
        return if (isChallengeManaged()) {
            DbChallengeAccess.findChallengeDetailById(challengeId)
        } else {
            null
        }
    }

    fun fetchChallengeDetail(synchronizationType: SynchronizationType = SynchronizationType.DEFAULT) {
        if (DriveKit.isConfigured() && isChallengeManaged()) {
            DriveKitChallenge.getChallengeDetail(
                challengeId,
                object : ChallengeDetailQueryListener {
                    override fun onResponse(
                        challengeDetailSyncStatus: ChallengeDetailSyncStatus,
                        challengeDetail: ChallengeDetail?,
                        trips: List<Trip>
                    ) {
                        if (challengeDetailSyncStatus != ChallengeDetailSyncStatus.CHALLENGE_NOT_FOUND) {
                            challengeDetailData = challengeDetail
                            challengeDetailTrips = trips
                        }
                        val value = when (challengeDetailSyncStatus) {
                            ChallengeDetailSyncStatus.CACHE_DATA_ONLY,
                            ChallengeDetailSyncStatus.SUCCESS,
                            ChallengeDetailSyncStatus.SYNC_ALREADY_IN_PROGRESS -> true
                            ChallengeDetailSyncStatus.CHALLENGE_NOT_FOUND,
                            ChallengeDetailSyncStatus.FAILED_TO_SYNC_CHALLENGE_DETAIL_CACHE_ONLY -> false
                        }
                        syncChallengeDetailError.postValue(value)
                    }
                }, synchronizationType)
        } else {
            syncChallengeDetailError.postValue(false)
        }
    }

    fun getTripData() = when (challenge.challengeType) {
        ChallengeType.SAFETY,
        ChallengeType.HARD_BRAKING,
        ChallengeType.HARD_ACCELERATION -> TripData.SAFETY
        ChallengeType.ECODRIVING -> TripData.ECO_DRIVING
        ChallengeType.DISTRACTION -> TripData.DISTRACTION
        ChallengeType.SPEEDING -> TripData.SPEEDING
        ChallengeType.DEPRECATED,
        ChallengeType.UNKNOWN -> TripData.SAFETY // Should not happen.
    }

    fun getDriverProgress(): Int {
        val progress = challengeDetailData?.let {
            if (it.challengeStats.maxScore == it.challengeStats.minScore || it.challengeStats.maxScore == it.driverStats.score) {
                100
            } else {
                ((it.driverStats.score - it.challengeStats.minScore) * 100).div(it.challengeStats.maxScore - it.challengeStats.minScore).roundToInt()
            }
        } ?: 0
        return progress.let {
            if (it <= 0) {
                 1
            } else {
                it
            }
        }
    }

    fun getBestPerformance(): String {
        return challengeDetailData?.let {
            val score = if (it.challengeStats.maxScore == 10.0) {
                it.challengeStats.maxScore.removeZeroDecimal()
            } else {
                it.challengeStats.maxScore.format(2)
            }
            "$score/10"
        } ?: ""
    }

    fun getWorstPerformance(): String {
        return challengeDetailData?.let {
            "${it.challengeStats.minScore.format(2)}/10"
        } ?: ""
    }

    fun getMainScore(context: Context): Spannable {
        return challengeDetailData?.let {
            val score = if (it.driverStats.score == 10.0) {
                it.driverStats.score.removeZeroDecimal()
            } else {
                it.driverStats.score.format(2)
            }
            DKSpannable().append(score, context.resSpans {
                color(DriveKitUI.colors.primaryColor())
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_xxxbig)
                typeface(BOLD)

            }).append(" /10", context.resSpans {
                color(DriveKitUI.colors.mainFontColor())
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_big)
                typeface(BOLD)
            }).toSpannable()
        } ?: SpannableString("")
    }

    fun challengeGlobalRank(context: Context) =
        if (challengeDetailData?.driverStats?.rank == 0) {
            "-"
        } else {
            "${challengeDetailData?.driverStats?.rank}"
        }.let {
            val pseudo = challengeDetailData?.let { challengeDetail ->
                challengeDetail.driversRanked?.let { drivers ->
                    val pseudo = drivers[challengeDetail.userIndex].pseudo
                    if (drivers.isNotEmpty() && !pseudo?.trim().isNullOrBlank()) {
                        pseudo
                    } else {
                        ""
                    }
                }
            } ?: "-"
            DKSpannable().append(pseudo, context.resSpans {
                color(DriveKitUI.colors.mainFontColor())
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_normal)
                typeface(BOLD)
            }).append("  ")
                .append(it, context.resSpans {
                    color(DriveKitUI.colors.secondaryColor())
                    size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_xxbig)
                    typeface(BOLD)
                }).append(" / ", context.resSpans {
                    color(DriveKitUI.colors.mainFontColor())
                    size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_medium)
                    typeface(BOLD)
                }).append(
                    "${challengeDetailData?.nbDriverRegistered}", context.resSpans {
                        color(DriveKitUI.colors.mainFontColor())
                        size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_normal)
                        typeface(BOLD)
                    }).toSpannable()
        }

    @StringRes
    fun getChallengeResultScoreTitleResId(): Int = when (challenge.challengeType) {
        ChallengeType.SAFETY -> R.string.dk_challenge_safety_score
        ChallengeType.ECODRIVING -> R.string.dk_challenge_eco_driving_score
        ChallengeType.DISTRACTION -> R.string.dk_challenge_distraction_score
        ChallengeType.SPEEDING -> R.string.dk_challenge_speeding_score
        ChallengeType.HARD_BRAKING -> R.string.dk_challenge_braking_score
        ChallengeType.HARD_ACCELERATION -> R.string.dk_challenge_acceleration_score
        ChallengeType.DEPRECATED,
        ChallengeType.UNKNOWN -> com.drivequant.drivekit.common.ui.R.string.dk_common_no_value
    }

    private fun computeRankPercentage(): Int = challengeDetailData?.let {
        100 - (it.driverStats.rank.div(it.nbDriverRanked.toDouble()) * 100).roundToInt()
    } ?: kotlin.run {
        0
    }

    fun isUserTheFirst() =
        challengeDetailData?.let {
            it.driverStats.rank == 1 || computeRankPercentage() == 100 || it.challengeStats.maxScore == it.driverStats.score
        } ?: run {
            false
        }

    fun shouldDisplayMinScore(): Boolean = challengeDetailData?.let {
        it.challengeStats.maxScore == it.challengeStats.minScore
    } ?: run {
        false
    }

    fun computeRatingStartCount(): Float {
        val value = computeRankPercentage()
        return if (isUserTheFirst() || value >= 100) {
            4
        } else {
            when (value) {
                in 76..100 -> 4
                in 51..75 -> 3
                in 26..50 -> 2
                in 0..25 -> 1
                else -> 0
            }
        }.toFloat()
    }

    fun getScoreTitle(context: Context) =
        context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_ranking_score).capitalizeFirstLetter()

    fun getDriverDistance(context: Context) =
        challengeDetailData?.let {
            formatChallengeDistance(it.driverStats.distance, context).convertToString()
        } ?: "-"

    fun getCompetitorDistance(context: Context): String =
        challengeDetailData?.let {
            formatChallengeDistance(it.challengeStats.distance, context).convertToString()
        } ?: "-"

    fun getDriverTripsNumber(context: Context): String {
        return challengeDetailData?.let {
            "${it.driverStats.numberTrip} ${context.resources.getQuantityString(
                com.drivequant.drivekit.common.ui.R.plurals.trip_plural,
                it.driverStats.numberTrip
            )}"
        } ?: "-"
    }

    fun getNbDriverRanked(): Int = challengeDetailData?.nbDriverRanked ?: 0

    fun getNbDriverRegistered(): Int = challengeDetailData?.nbDriverRegistered ?: 0

    fun getNbDriverRankedPercentage(): String {
        val nbDriverRanked = challengeDetailData?.nbDriverRanked ?: 0
        val nbDriverRegistered = challengeDetailData?.nbDriverRegistered
        return if (nbDriverRegistered != null && nbDriverRegistered > 0 && nbDriverRanked <= nbDriverRegistered) {
            val percentage = (nbDriverRanked.toDouble() / nbDriverRegistered.toDouble() * 100.0).roundToInt()
            "$percentage %"
        } else {
            "-"
        }
    }

    fun getRankingHeaderIcon(context: Context) = when (challenge.challengeType) {
        ChallengeType.SAFETY,
        ChallengeType.HARD_BRAKING,
        ChallengeType.HARD_ACCELERATION -> R.drawable.dk_challenge_leaderboard_safety
        ChallengeType.ECODRIVING -> R.drawable.dk_challenge_leaderboard_ecodriving
        ChallengeType.DISTRACTION -> R.drawable.dk_challenge_leaderboard_distraction
        ChallengeType.SPEEDING -> R.drawable.dk_challenge_leaderboard_speeding
        ChallengeType.DEPRECATED,
        ChallengeType.UNKNOWN -> null
    }?.let {
        ContextCompat.getDrawable(context, it)
    }

    fun getRankingList(): List<ChallengeRankingItem> {
        val listOfRanking = mutableListOf<ChallengeRankingItem>()
        challengeDetailData?.let { challengeDetail ->
            challengeDetail.driversRanked?.let { driversRanking ->
                for (driver in driversRanking) {
                    listOfRanking.add(
                        ChallengeRankingItem(
                            this,
                            driver.rank,
                            driver.pseudo,
                            driver.distance,
                            driver.score
                        )
                    )
                }
            }
        }
        return listOfRanking
    }

    fun getRankingProgression(): DriverProgression? =
        challengeDetailData?.let {
            return it.driversRanked?.let { driversRanked ->
                val previousRank = driversRanked[it.userIndex].previousRank
                val rank = driversRanked[it.userIndex].rank
                return if (rank > 0 && previousRank > 0) {
                    val delta = previousRank - rank
                    return when {
                        delta > 0 -> DriverProgression.GOING_UP
                        delta == 0 -> null
                        else -> DriverProgression.GOING_DOWN
                    }
                } else {
                    null
                }
            }
        }

    fun getRankingGlobalRank(context: Context) =
        if (challengeDetailData?.driverStats?.rank == 0) {
            "-"
        } else {
            "${challengeDetailData?.driverStats?.rank}"
        }.let {
            DKSpannable().append(it, context.resSpans {
                color(DriveKitUI.colors.secondaryColor())
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_xbig)
                typeface(BOLD)
            }).append(" / ", context.resSpans {
                color(DriveKitUI.colors.mainFontColor())
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_xbig)
                typeface(BOLD)
            }).append(
                "${challengeDetailData?.nbDriverRegistered}", context.resSpans {
                    color(DriveKitUI.colors.mainFontColor())
                    size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_xbig)
                    typeface(BOLD)
                }).toSpannable()
        }

    fun formatChallengeDistance(distance:Double, context: Context): List<FormatType> =
        formatMeterDistanceInKm(
            context,
            distance * 1000,
            minDistanceToRemoveFractions = 10.0
        )

    @Suppress("UNCHECKED_CAST")
    class ChallengeDetailViewModelFactory(private val challengeId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChallengeDetailViewModel(challengeId) as T
        }
    }
}
