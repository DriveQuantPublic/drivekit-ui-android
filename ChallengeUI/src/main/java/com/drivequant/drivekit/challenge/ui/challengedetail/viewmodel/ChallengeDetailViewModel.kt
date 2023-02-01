package com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel

import android.content.Context
import android.graphics.Typeface.BOLD
import android.text.Spannable
import android.text.SpannableString
import android.view.View
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
import com.drivequant.drivekit.common.ui.utils.*
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter.formatMeterDistanceInKm
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Challenge
import com.drivequant.drivekit.databaseutils.entity.ChallengeDetail
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.dbchallengeaccess.DbChallengeAccess
import kotlin.math.roundToInt

class ChallengeDetailViewModel(private val challengeId: String) : ViewModel() {

    var syncChallengeDetailError: MutableLiveData<Boolean> = MutableLiveData()
    var challengeDetailData: ChallengeDetail? = null
    var challengeDetailTrips = listOf<Trip>()
    lateinit var challenge: Challenge

    init {
        DbChallengeAccess.findChallengeById(challengeId)?.let {
            challenge = it
        }
    }

    fun getChallengeId() = challengeId

    fun getLocalChallengeDetail() : ChallengeDetail? =
        DbChallengeAccess.findChallengeDetailById(challengeId)

    fun fetchChallengeDetail(synchronizationType: SynchronizationType = SynchronizationType.DEFAULT) {
        if (DriveKit.isConfigured()) {
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

    fun getTripData() = when (challenge.themeCode) {
        in 101..104 -> TripData.ECO_DRIVING
        in 201..216 -> TripData.SAFETY
        in 301..305 -> TripData.DISTANCE
        in 306..309 -> TripData.DURATION
        221 -> TripData.DISTRACTION
        401 -> TripData.SPEEDING
        else -> TripData.SAFETY
    }

    fun getDriverProgress() : Int {
        var progress = 0
        challengeDetailData?.let {
            if (it.challengeStats.maxScore == it.challengeStats.minScore || it.challengeStats.maxScore == it.driverStats.score) {
                progress = 100
            } else {
                progress = when (challenge.themeCode) {
                    in 101..301,
                    in 306..309,
                    401 -> ((it.driverStats.score - it.challengeStats.minScore) * 100).div(
                        it.challengeStats.maxScore - it.challengeStats.minScore
                    ).roundToInt()
                    in 302..305 -> {
                        val duration = (it.driverStats.score * 3600 / 60).roundToInt()
                        val minDuration = (it.challengeStats.minScore * 3600 / 60).roundToInt()
                        val numeratorDuration = (duration - minDuration) * 100
                        val maxDuration = (it.challengeStats.maxScore * 3600 / 60).roundToInt()
                        numeratorDuration / (maxDuration - minDuration)
                    }
                    else -> 0
                }
            }
        }
        return progress.let {
            if (it <= 0) {
                 1
            } else {
                it
            }
        }
    }

    fun getBestPerformance(context: Context): String {
        return challengeDetailData?.let {
            return when (challenge.themeCode) {
                in 101..221,
                401 -> {
                    val score = if (it.challengeStats.maxScore == 10.0) {
                        it.challengeStats.maxScore.removeZeroDecimal()
                    } else {
                        it.challengeStats.maxScore.format(2)
                    }
                    "$score/10"
                }
                in 306..309 ->formatChallengeDuration(it.challengeStats.maxScore, context).convertToString()
                in 302..305 -> formatChallengeDistance(it.challengeStats.maxScore, context).convertToString()
                301 -> "${it.challengeStats.maxScore.removeZeroDecimal()} ${context.resources.getQuantityString(
                    R.plurals.trip_plural,
                    it.challengeStats.numberTrip
                )}"
                else -> "-"
            }
        } ?: ""
    }

    fun getWorstPerformance(context: Context): String {
        return challengeDetailData?.let {
            return when (challenge.themeCode) {
                in 101..221,
                401 -> "${it.challengeStats.minScore.format(2)}/10"
                in 306..309,
                in 302..305,
                301 -> "${it.challengeStats.minScore.removeZeroDecimal()} ${context.resources.getQuantityString(
                    R.plurals.trip_plural,
                    it.challengeStats.numberTrip
                )}"
                else -> "-"
            }
        } ?: ""
    }

    fun getMainScore(context: Context): Spannable {
        return challengeDetailData?.let {
            return when (challenge.themeCode) {
                in 101..221,
                401 -> {
                    val score = if (it.driverStats.score == 10.0) {
                        it.driverStats.score.removeZeroDecimal()
                    } else {
                        it.driverStats.score.format(2)
                    }
                    DKSpannable().append(score, context.resSpans {
                        color(DriveKitUI.colors.primaryColor())
                        size(R.dimen.dk_text_xxxbig)
                        typeface(BOLD)

                    }).append(" /10", context.resSpans {
                        color(DriveKitUI.colors.mainFontColor())
                        size(R.dimen.dk_text_big)
                        typeface(BOLD)
                    }).toSpannable()
                }

                in 306..309 -> {
                    val spannable = DKSpannable()
                    formatChallengeDuration(it.driverStats.score, context).forEach { formatType ->
                        when (formatType) {
                            is FormatType.VALUE -> spannable.append(
                                formatType.value,
                                context.resSpans {
                                    color(DriveKitUI.colors.primaryColor())
                                    typeface(BOLD)
                                    size(R.dimen.dk_text_xxxbig)
                                })
                            is FormatType.UNIT -> spannable.append(
                                formatType.value,
                                context.resSpans {
                                    color(DriveKitUI.colors.primaryColor())
                                    typeface(BOLD)
                                    size(R.dimen.dk_text_xxxbig)
                                })
                            is FormatType.SEPARATOR -> spannable.append(formatType.value)
                        }
                    }
                    spannable.toSpannable()
                }
                in 302..305 -> {
                    val spannable = DKSpannable()
                    formatChallengeDistance(it.driverStats.score, context).forEach { formatType ->
                        when (formatType) {
                            is FormatType.VALUE -> spannable.append(
                                formatType.value,
                                context.resSpans {
                                    color(DriveKitUI.colors.primaryColor())
                                    typeface(BOLD)
                                    size(R.dimen.dk_text_xxxbig)
                                })
                            is FormatType.UNIT -> spannable.append(
                                formatType.value,
                                context.resSpans {
                                    color(DriveKitUI.colors.mainFontColor())
                                    typeface(BOLD)
                                    size(R.dimen.dk_text_big)
                                })
                            is FormatType.SEPARATOR -> spannable.append(formatType.value)
                        }
                    }
                    spannable.toSpannable()
                }
                301 -> DKSpannable().append("${it.driverStats.numberTrip} ", context.resSpans {
                        color(DriveKitUI.colors.primaryColor())
                        size(R.dimen.dk_text_xxxbig)
                        typeface(BOLD)

                    }).append(
                        context.resources.getQuantityString(
                            R.plurals.trip_plural,
                            it.driverStats.numberTrip
                        ), context.resSpans {
                            color(DriveKitUI.colors.mainFontColor())
                            size(R.dimen.dk_text_big)
                            typeface(BOLD)
                        }).toSpannable()

                else -> SpannableString("")
            }
        } ?: SpannableString("")
    }

    fun challengeGlobalRank(context: Context) =
        if (challengeDetailData?.driverStats?.rank?.toInt() == 0) {
            "-"
        } else {
            "${challengeDetailData?.driverStats?.rank?.toInt()}"
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
                size(R.dimen.dk_text_normal)
                typeface(BOLD)
            }).append("  ")
                .append(it, context.resSpans {
                    color(DriveKitUI.colors.secondaryColor())
                    size(R.dimen.dk_text_xxbig)
                    typeface(BOLD)
                }).append(" / ", context.resSpans {
                    color(DriveKitUI.colors.mainFontColor())
                    size(R.dimen.dk_text_medium)
                    typeface(BOLD)
                }).append(
                    "${challengeDetailData?.nbDriverRanked}", context.resSpans {
                        color(DriveKitUI.colors.mainFontColor())
                        size(R.dimen.dk_text_normal)
                        typeface(BOLD)
                    }).toSpannable()
        }

    fun getChallengeResultScoreTitle() = when (challenge.themeCode) {
        in 101..104 -> "dk_challenge_eco_driving_score"
        in 201..204 -> "dk_challenge_safety_score"
        in 205..208 -> "dk_challenge_braking_score"
        in 209..212 -> "dk_challenge_acceleration_score"
        in 213..216 -> "dk_challenge_adherence_score"
        221 -> "dk_challenge_distraction_score"
        301 -> "dk_challenge_nb_trip"
        in 306..309 -> "dk_challenge_driving_time"
        in 302..305 -> "dk_challenge_traveled_distance"
        401 -> "dk_challenge_speeding_score"
        else -> "-"
    }

    private fun computeRankPercentage(): Int = challengeDetailData?.let {
        100 - (it.driverStats.rank.div(it.nbDriverRanked.toDouble()) * 100).roundToInt()
    } ?: kotlin.run {
        0
    }

    fun isUserTheFirst() =
        challengeDetailData?.let {
            it.driverStats.rank.roundToInt() == 1 || computeRankPercentage() == 100 || it.challengeStats.maxScore == it.driverStats.score
        } ?: run {
            false
        }

    fun shouldDisplayMinScore(): Boolean = challengeDetailData?.let {
        it.challengeStats.maxScore == it.challengeStats.minScore
    } ?: run {
        false
    }

    fun shouldDisplayDistanceCard() = when(challenge.themeCode) {
        in 302..305 -> View.GONE
        else -> View.VISIBLE
    }
    fun shouldDisplayTripsCard() = when(challenge.themeCode) {
        301 -> View.GONE
        else -> View.VISIBLE
    }
    fun shouldDisplayDurationCard() = when(challenge.themeCode) {
        in 306..309 -> View.GONE
        else -> View.VISIBLE
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

    fun getScoreTitle(context: Context) = when(challenge.themeCode) {
        in 101..221 -> "dk_common_ranking_score"
        in 306..309 -> "dk_common_duration"
        in 302..305 -> "dk_common_distance"
        301 -> "dk_common_trip_plural"

        else -> "dk_common_ranking_score"
    }.let {
        DKResource.convertToString(context, it).capitalizeFirstLetter()
    }

    fun getDriverDistance(context: Context) =
        challengeDetailData?.let {
            formatChallengeDistance(it.driverStats.distance, context).convertToString()
        } ?: "-"

    fun getCompetitorDistance(context: Context): String =
        challengeDetailData?.let {
            formatChallengeDistance(it.challengeStats.distance, context).convertToString()
        } ?: "-"

    fun getDriverDuration(context: Context) =
        challengeDetailData?.let {
            formatChallengeDuration(it.driverStats.duration, context).convertToString()
        } ?: "-"

    fun getCompetitorDuration(context: Context) =
        challengeDetailData?.let {
            formatChallengeDuration(it.challengeStats.duration, context).convertToString()
        } ?: "-"

    fun getDriverTripsNumber(context: Context): String {
        return challengeDetailData?.let {
            "${it.driverStats.numberTrip} ${context.resources.getQuantityString(
                R.plurals.trip_plural,
                it.driverStats.numberTrip
            )}"
        } ?: "-"
    }

    fun getCompetitorTripsNumber(context: Context): String {
        return challengeDetailData?.let {
            "${it.challengeStats.numberTrip} ${context.resources.getQuantityString(
                R.plurals.trip_plural,
                it.challengeStats.numberTrip
            )}"
        } ?: "-"
    }

    fun getRankingHeaderIcon(context: Context) = when (challenge.themeCode) {
        in 101..104 -> "dk_challenge_leaderboard_ecodriving"
        in 201..204, in 205..208,  in 209..212, in 213..216  -> "dk_challenge_leaderboard_safety"
        221 -> "dk_challenge_leaderboard_distraction"
        301 -> "dk_challenge_leaderboard_trips_number"
        in 306..309 -> "dk_challenge_leaderboard_duration"
        in 302..305 -> "dk_challenge_leaderboard_distance"
        401 -> "dk_challenge_leaderboard_speeding"
        else -> "-"
    }.let {
        DKResource.convertToDrawable(context, it)
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

    fun geRankingGlobalRank(context: Context) =
        if (challengeDetailData?.driverStats?.rank?.toInt() == 0) {
            "-"
        } else {
            "${challengeDetailData?.driverStats?.rank?.toInt()}"
        }.let {
            DKSpannable().append(it, context.resSpans {
                color(DriveKitUI.colors.secondaryColor())
                size(R.dimen.dk_text_xbig)
                typeface(BOLD)
            }).append(" / ", context.resSpans {
                color(DriveKitUI.colors.mainFontColor())
                size(R.dimen.dk_text_xbig)
                typeface(BOLD)
            }).append(
                "${challengeDetailData?.nbDriverRanked}", context.resSpans {
                    color(DriveKitUI.colors.mainFontColor())
                    size(R.dimen.dk_text_xbig)
                    typeface(BOLD)
                }).toSpannable()
        }

    fun formatChallengeDistance(distance:Double, context: Context): List<FormatType> =
        formatMeterDistanceInKm(
            context,
            distance * 1000,
            minDistanceToRemoveFractions = 10.0
        )

    fun formatChallengeDuration(duration: Double, context: Context): List<FormatType> =
        DKDataFormatter.formatDuration(
            context,
            DKDataFormatter.ceilDuration(duration * 3600, 600), DurationUnit.HOUR
        )

    @Suppress("UNCHECKED_CAST")
    class ChallengeDetailViewModelFactory(private val challengeId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChallengeDetailViewModel(challengeId) as T
        }
    }
}
