package com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel

import android.content.Context
import android.graphics.Typeface.BOLD
import android.text.Spannable
import android.text.SpannableString
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ChallengeDetailQueryListener
import com.drivequant.drivekit.challenge.ChallengeDetailSyncStatus
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.*
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
    var useCache = false
    lateinit var challenge: Challenge

    init {
        DbChallengeAccess.findChallengeById(challengeId)?.let {
            challenge = it
        }
    }

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

    fun getDriverProgress() : Int {
        var progress = 0
        challengeDetailData?.let {
            if (it.challengeStats.maxScore == it.challengeStats.minScore || it.challengeStats.maxScore == it.driverStats.score) {
                progress = 100
            } else {
                progress = when (challenge.themeCode) {
                    in 101..301 -> ((it.driverStats.score - it.challengeStats.minScore) * 100).div(
                        it.challengeStats.maxScore - it.challengeStats.minScore
                    ).roundToInt()
                    in 306..309 -> ((it.driverStats.distance - it.challengeStats.minScore) * 100).div(
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
        return progress
    }

    fun getBestPerformance(context: Context): String {
        return challengeDetailData?.let {
            return when (challenge.themeCode) {
                in 101..221 -> "${it.challengeStats.maxScore.format(2)}/10"
                in 306..309 -> DKDataFormatter.formatDuration(
                    context,
                    it.challengeStats.maxScore * 3600
                ).convertToString()
                in 302..305 -> DKDataFormatter.formatMeterDistance(
                    context,
                    it.challengeStats.maxScore * 1000
                ).convertToString()
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
                in 101..221 -> "${it.challengeStats.minScore.format(2)}/10"
                in 306..309 -> DKDataFormatter.formatDuration(
                    context,
                    it.challengeStats.minScore * 3600
                ).convertToString()
                in 302..305 -> DKDataFormatter.formatMeterDistance(
                    context,
                    it.challengeStats.minScore * 1000
                ).convertToString()
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
                in 101..221 -> {
                    DKSpannable().append(it.driverStats.score.format(2), context.resSpans {
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
                    val data =
                        DKDataFormatter.formatDuration(context, it.driverStats.duration * 3600)
                    val spannable = DKSpannable()
                    data.forEach { formatType ->
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
                                    size(R.dimen.dk_text_big)
                                })
                            is FormatType.SEPARATOR -> spannable.append(formatType.value)
                        }
                    }
                    spannable.toSpannable()
                }
                in 302..305 -> {
                    val data =
                        DKDataFormatter.formatMeterDistance(context, it.driverStats.distance * 1000)
                    val spannable = DKSpannable()
                    data.forEach { formatType ->
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
                                    size(R.dimen.dk_text_big)
                                })
                            is FormatType.SEPARATOR -> spannable.append(formatType.value)
                        }
                    }
                    spannable.toSpannable()
                }
                301 -> {
                    DKSpannable().append("${it.driverStats.numberTrip} ", context.resSpans {
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

                }
                else -> SpannableString("")
            }
        } ?: SpannableString("")
    }

    fun getChallengeResultScoreTitle() = when (challenge.themeCode) {
        in 101..104 -> "ecoDriving score"
        in 201..204 -> "safety score"
        in 205..208 -> "Braking score"
        in 209..212 -> "acceleration score"
        in 213..216 -> "adherence score"
        221 -> "distraction score"
        301 -> "number of trips"
        in 306..309 -> "driving time"
        in 302..305 -> "distance travelled"
        else -> ""
    }

    private fun computeRankPercentage(): Int {
        val percent = challengeDetailData?.let {
            100 - (it.driverStats.rank.div(it.nbDriverRanked.toDouble()) * 100)
        } ?: kotlin.run {
            0.0
        }
        return percent.roundToInt()
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

    fun computeRatingStartCount(): Float {
        val value = computeRankPercentage()
        return if (isUserTheFirst() || value >= 100) {
            100.toFloat()
        } else {
            when (value) {
                in 51..75 -> 3
                in 26..50 -> 2
                in 0..25 -> 1
                else -> 0
            }.toFloat()
        }
    }

    fun getScoreTitle(context: Context) = when(challenge.themeCode) {
        in 101..104,in 201..216,221 -> "dk_common_ranking_score"
        in 301..305 -> "dk_common_distance"
        in 306..309 -> "dk_common_duration"
        else -> "dk_common_ranking_score"
    }.let {
        DKResource.convertToString(context, it)
    }

    fun getDriverGlobalRank(context: Context) =
        if (challengeDetailData?.driverStats?.rank?.toInt() == 0) {
            "-"
        } else {
            "${challengeDetailData?.driverStats?.rank?.toInt()}"
        }.let {
            DKSpannable().append(challengeDetailData!!.driversRanked?.get(0)!!.pseudo, context.resSpans {
                color(DriveKitUI.colors.mainFontColor())
                size(R.dimen.dk_text_xbig) })
                .append(it, context.resSpans {
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
            challengeDetail.driversRanked?.let { driversRanking ->
                for (driver in driversRanking) {
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

    fun getDriverDistance(context: Context): String {
        return challengeDetailData?.let {
            DKDataFormatter.formatMeterDistanceInKm(
                context,
                it.driverStats.distance * 1000
            ).convertToString()
        } ?: ""
    }

    fun getCompetitorDistance(context: Context): String {
        return challengeDetailData?.let {
            DKDataFormatter.formatMeterDistanceInKm(
                context,
                it.challengeStats.distance * 1000
            ).convertToString()
        } ?: ""
    }

    fun getDriverDuration(context: Context):String {
        return challengeDetailData?.let {
             DKDataFormatter.formatDuration(context, it.driverStats.duration * 3600).convertToString()
        }?: ""
    }

    fun getCompetitorDuration(context: Context): String {
        return challengeDetailData?.let {
            DKDataFormatter.formatDuration(context, it.challengeStats.duration * 3600).convertToString()
        }?: ""
    }

    fun getDriverTripsNumber(context: Context): String {
        return challengeDetailData?.let {
            "${it.driverStats.numberTrip} ${context.resources.getQuantityString(
                R.plurals.trip_plural,
                it.driverStats.numberTrip
            )}"
        } ?: ""
    }

    fun getCompetitorTripsNumber(context: Context): String {
        return challengeDetailData?.let {
            "${it.challengeStats.numberTrip} ${context.resources.getQuantityString(
                R.plurals.trip_plural,
                it.challengeStats.numberTrip
            )}"
        } ?: ""
    }

    @Suppress("UNCHECKED_CAST")
    class ChallengeDetailViewModelFactory(private val challengeId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChallengeDetailViewModel(challengeId) as T
        }
    }
}