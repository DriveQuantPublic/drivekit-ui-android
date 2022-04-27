package com.drivekit.demoapp.manager

import com.drivequant.drivekit.challenge.ChallengesQueryListener
import com.drivequant.drivekit.challenge.ChallengesSyncStatus
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.driver.GetUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UserInfo
import com.drivequant.drivekit.core.driver.UserInfoGetStatus
import com.drivequant.drivekit.databaseutils.entity.Badge
import com.drivequant.drivekit.databaseutils.entity.Challenge
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.driverachievement.BadgeSyncStatus
import com.drivequant.drivekit.driverachievement.BadgesQueryListener
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.trip.TripsQueryListener
import com.drivequant.drivekit.driverdata.trip.TripsSyncStatus
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKWorkingHours
import com.drivequant.drivekit.tripanalysis.service.workinghours.SyncWorkingHoursQueryListener
import com.drivequant.drivekit.tripanalysis.service.workinghours.SyncWorkingHoursStatus
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleListQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus

enum class DKModule {
    USER_INFO,
    VEHICLE,
    WORKING_HOURS,
    TRIPS,
    BADGES,
    CHALLENGE;
}

enum class SyncStatus {
    SUCCESS, FAILED
}

object SyncModuleManager {

    private fun syncModule(module: DKModule, listener: ModuleSyncListener? = null) {
        when (module) {
            DKModule.CHALLENGE -> syncChallengeModule(listener)
            DKModule.BADGES -> syncBadgeModule(listener)
            DKModule.WORKING_HOURS -> syncWorkingHours(listener)
            DKModule.USER_INFO -> syncUserInfo(listener)
            DKModule.VEHICLE -> syncVehicles(listener)
            DKModule.TRIPS -> syncTrips(listener)
        }
    }

    fun syncModules(
        modules: MutableList<DKModule>,
        previousResults: MutableList<SyncStatus> = mutableListOf(),
        stepResultListener: StepResultListener? = null,
        listener: ModulesSyncListener? = null) {

        val module = modules.firstOrNull()
        module?.let {
            val results: MutableList<SyncStatus> = previousResults
            if (modules.size == 1) {
                syncModule(it, object : ModuleSyncListener {
                    override fun onModuleSyncResult(status: SyncStatus) {
                        results.add(status)
                        stepResultListener?.onStepFinished(status, listOf())
                        listener?.onModulesSyncResult(results)
                    }
                })
            } else if (modules.size > 1) {
                modules.removeFirstOrNull()
                syncModule(module, object : ModuleSyncListener {
                    override fun onModuleSyncResult(status: SyncStatus) {
                        results.add(status)
                        stepResultListener?.onStepFinished(status, modules)
                        syncModules(modules, previousResults, stepResultListener, listener)
                    }
                })
            }
        }
    }

    private fun syncVehicles(listener: ModuleSyncListener?) {
        DriveKitVehicle.getVehiclesOrderByNameAsc(object : VehicleListQueryListener {
            override fun onResponse(status: VehicleSyncStatus, vehicles: List<Vehicle>) {
                when (status) {
                    VehicleSyncStatus.NO_ERROR -> SyncStatus.SUCCESS
                    VehicleSyncStatus.CACHE_DATA_ONLY,
                    VehicleSyncStatus.FAILED_TO_SYNC_VEHICLES_CACHE_ONLY,
                    VehicleSyncStatus.SYNC_ALREADY_IN_PROGRESS -> SyncStatus.FAILED
                }.let {
                    listener?.onModuleSyncResult(it)
                }
            }
        })
    }

    private fun syncUserInfo(listener: ModuleSyncListener?) {
        DriveKit.getUserInfo(object : GetUserInfoQueryListener {
            override fun onResponse(status: UserInfoGetStatus, userInfo: UserInfo?) {
                when (status) {
                    UserInfoGetStatus.SUCCESS -> SyncStatus.SUCCESS
                    UserInfoGetStatus.CACHE_DATA_ONLY,
                    UserInfoGetStatus.FAILED_TO_SYNC_USER_INFO_CACHE_ONLY -> SyncStatus.FAILED
                }.let {
                    listener?.onModuleSyncResult(it)
                }
            }
        })
    }

    private fun syncTrips(listener: ModuleSyncListener?) {
        DriveKitDriverData.getTripsOrderByDateDesc(object : TripsQueryListener {
            override fun onResponse(status: TripsSyncStatus, trips: List<Trip>) {
                when (status) {
                    TripsSyncStatus.NO_ERROR -> SyncStatus.SUCCESS
                    TripsSyncStatus.CACHE_DATA_ONLY,
                    TripsSyncStatus.FAILED_TO_SYNC_TRIPS_CACHE_ONLY,
                    TripsSyncStatus.FAILED_TO_SYNC_SAFETY_EVENTS -> SyncStatus.FAILED
                }.let {
                    listener?.onModuleSyncResult(it)
                }
            }
        })
    }

    private fun syncWorkingHours(listener: ModuleSyncListener?) {
        DriveKitTripAnalysis.getWorkingHours(object : SyncWorkingHoursQueryListener {
            override fun onResponse(status: SyncWorkingHoursStatus, workingHours: DKWorkingHours?) {
                when (status) {
                    SyncWorkingHoursStatus.SUCCESS -> SyncStatus.SUCCESS
                    SyncWorkingHoursStatus.FAILED_TO_SYNC_CACHE_ONLY -> SyncStatus.FAILED
                }.let {
                    listener?.onModuleSyncResult(it)
                }
            }
        })
    }

    private fun syncBadgeModule(listener: ModuleSyncListener?) {
        DriveKitDriverAchievement.getBadges(object : BadgesQueryListener {
            override fun onResponse(
                badgeSyncStatus: BadgeSyncStatus,
                badges: List<Badge>,
                newAcquiredBadgesCount: Int
            ) {
                when (badgeSyncStatus) {
                    BadgeSyncStatus.NO_ERROR -> SyncStatus.SUCCESS
                    BadgeSyncStatus.FAILED_TO_SYNC_BADGES_CACHE_ONLY, BadgeSyncStatus.CACHE_DATA_ONLY -> SyncStatus.FAILED
                }.let {
                    listener?.onModuleSyncResult(it)
                }
            }
        })
    }

    private fun syncChallengeModule(listener: ModuleSyncListener?) {
        DriveKitChallenge.getChallenges(object : ChallengesQueryListener {
            override fun onResponse(
                challengesSyncStatus: ChallengesSyncStatus,
                challenges: List<Challenge>
            ) {
                when (challengesSyncStatus) {
                    ChallengesSyncStatus.SUCCESS -> SyncStatus.SUCCESS
                    ChallengesSyncStatus.FAILED_TO_SYNC_CHALLENGES_CACHE_ONLY, ChallengesSyncStatus.SYNC_ALREADY_IN_PROGRESS,
                    ChallengesSyncStatus.CACHE_DATA_ONLY -> SyncStatus.FAILED
                }.let {
                    listener?.onModuleSyncResult(it)
                }
            }
        })
    }
}

interface ModuleSyncListener {
    fun onModuleSyncResult(status: SyncStatus)
}

interface StepResultListener {
    fun onStepFinished(syncStatus: SyncStatus, remainingModules: List<DKModule>)
}

interface ModulesSyncListener {
    fun onModulesSyncResult(results: MutableList<SyncStatus>)
}