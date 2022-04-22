package com.drivekit.demoapp.features.viewholder

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.drivekit.demoapp.component.FeatureCard
import com.drivekit.demoapp.features.enum.FeatureType
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.challenge.ui.ChallengeUI
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.badges.activity.BadgeListActivity
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.permissions.listener.PermissionViewListener
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysisUI
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import kotlin.collections.ArrayList

internal class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cardView = itemView.findViewById<FeatureCard>(R.id.feature_card)

    fun bind(feature: FeatureType) {
        feature.apply {
            getIcon()?.let {
                cardView.configureIcon(it)
            }
            cardView.configureTitle(getTitleResId())
            cardView.configureDescription(getDescriptionResId())
            cardView.configureActionButton(getActionButtonTitleResId(), object : FeatureCard.FeatureCardActionClickListener {
                override fun onButtonClicked() {
                    manageClickedButton(feature)
                }
            })
            cardView.configureInfoButton(object : FeatureCard.FeatureCardInfoClickListener {
                override fun onInfoClicked() {
                    manageClickedInfoButton(feature)
                }
            })
        }
    }

    private fun manageClickedInfoButton(feature: FeatureType) {
        when (feature) {
            FeatureType.ALL -> null
            FeatureType.DRIVERDATA_TRIPS -> R.string.drivekit_doc_android_driver_data
            FeatureType.PERMISSIONSUTILS_ONBOARDING -> R.string.drivekit_doc_android_permission_management // TODO vérifier les deux clés
            FeatureType.PERMISSIONSUTILS_DIAGNOSIS -> R.string.drivekit_doc_android_permission_utils // TODO vérifier les deux clés
            FeatureType.VEHICLE_LIST -> R.string.drivekit_doc_android_vehicle_list
            FeatureType.VEHICLE_ODOMETER -> R.string.drivekit_doc_android_odometer
            FeatureType.CHALLENGE_LIST -> R.string.drivekit_doc_android_challenges
            FeatureType.DRIVERACHIEVEMENT_RANKING -> R.string.drivekit_doc_android_ranking
            FeatureType.DRIVERACHIEVEMENT_BADGES -> R.string.drivekit_doc_android_badges
            FeatureType.DRIVERACHIEVEMENT_STREAKS -> R.string.drivekit_doc_android_streaks
            FeatureType.TRIPANALYSIS_WORKINGHOURS -> R.string.drivekit_doc_android_working_hours
        }?.let {
            val url = itemView.context.getString(it)
            itemView.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    private fun manageClickedButton(feature: FeatureType) {
        when (feature) {
            FeatureType.ALL -> {
                // do nothing
            }
            FeatureType.DRIVERDATA_TRIPS -> {
                DriverDataUI.startTripListActivity(itemView.context)
            }
            FeatureType.PERMISSIONSUTILS_ONBOARDING -> {
                if (PermissionsUtilsUI.hasError(itemView.context)) {
                    PermissionsUtilsUI.showPermissionViews(itemView.context, ArrayList(PermissionView.values().toMutableList()), object : PermissionViewListener {
                        override fun onFinish() {

                        }
                    })
                } else {
                    // TODO afficher l'AlertDialog quand le ticket UserId sera mergé
                }
            }
            FeatureType.PERMISSIONSUTILS_DIAGNOSIS -> {
                PermissionsUtilsUI.startAppDiagnosisActivity(itemView.context)
            }
            FeatureType.VEHICLE_LIST -> {
                DriveKitVehicleUI.startVehicleListActivity(itemView.context)
            }
            FeatureType.VEHICLE_ODOMETER -> {
                DriveKitVehicleUI.startOdometerUIActivity(itemView.context)
            }
            FeatureType.CHALLENGE_LIST -> {
                ChallengeUI.startChallengeActivity(itemView.context)
            }
            FeatureType.DRIVERACHIEVEMENT_RANKING -> {
                DriverAchievementUI.startRankingActivity(itemView.context)
            }
            FeatureType.DRIVERACHIEVEMENT_BADGES -> {
                val intent = Intent(itemView.context, BadgeListActivity::class.java)
                itemView.context.startActivity(intent)
            }
            FeatureType.DRIVERACHIEVEMENT_STREAKS -> {
                DriverAchievementUI.startStreakListActivity(itemView.context)
            }
            FeatureType.TRIPANALYSIS_WORKINGHOURS -> {
                DriveKitTripAnalysisUI.startWorkingHoursActivity(itemView.context)
            }
        }
    }
}