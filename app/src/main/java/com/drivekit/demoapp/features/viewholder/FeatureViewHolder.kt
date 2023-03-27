package com.drivekit.demoapp.features.viewholder

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drivekit.demoapp.component.FeatureCard
import com.drivekit.demoapp.features.enum.FeatureType
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.challenge.ui.ChallengeUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.core.utils.PermissionStatus
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.badges.activity.BadgeListActivity
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.permissions.listener.PermissionViewListener
import com.drivequant.drivekit.timeline.ui.DriveKitDriverDataTimelineUI
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysisUI
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI

internal class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cardView = itemView.findViewById<FeatureCard>(R.id.feature_card)

    fun bind(feature: FeatureType) {
        feature.apply {
            getIconResId()?.let {
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
        feature.getInfoUrlResId()?.let {
            val url = itemView.context.getString(it)
            itemView.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    private fun manageClickedButton(feature: FeatureType) {
        val context = cardView.context
        when (feature) {
            FeatureType.ALL -> {
                // do nothing
            }
            FeatureType.DRIVERDATA_TRIPS -> {
                DriverDataUI.startTripListActivity(context)
            }
            FeatureType.PERMISSIONSUTILS_ONBOARDING -> {
                if (showPermissionUtilsOnboarding()) {
                    PermissionsUtilsUI.showPermissionViews(context, object : PermissionViewListener {
                        override fun onFinish() {
                            // do nothing
                        }
                    })
                } else {
                    val alertDialog = DKAlertDialog.LayoutBuilder()
                        .init(context)
                        .layout(R.layout.template_alert_dialog_layout)
                        .positiveButton()
                        .show()

                    val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                    val descriptionTextView =
                        alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                    titleTextView?.text = context.getString(R.string.app_name)
                    descriptionTextView?.text = context.getString(R.string.feature_permission_onboarding_ok)
                    titleTextView?.headLine1()
                    descriptionTextView?.normalText()
                }
            }
            FeatureType.PERMISSIONSUTILS_DIAGNOSIS -> {
                PermissionsUtilsUI.startAppDiagnosisActivity(context)
            }
            FeatureType.VEHICLE_LIST -> {
                DriveKitVehicleUI.startVehicleListActivity(context)
            }
            FeatureType.VEHICLE_ODOMETER -> {
                DriveKitVehicleUI.startOdometerUIActivity(context)
            }
            FeatureType.CHALLENGE_LIST -> {
                ChallengeUI.startChallengeActivity(context)
            }
            FeatureType.DRIVERACHIEVEMENT_RANKING -> {
                DriverAchievementUI.startRankingActivity(context)
            }
            FeatureType.DRIVERACHIEVEMENT_BADGES -> {
                val intent = Intent(context, BadgeListActivity::class.java)
                context.startActivity(intent)
            }
            FeatureType.DRIVERACHIEVEMENT_STREAKS -> {
                DriverAchievementUI.startStreakListActivity(context)
            }
            FeatureType.TRIPANALYSIS_WORKINGHOURS -> {
                DriveKitTripAnalysisUI.startWorkingHoursActivity(context)
            }
            FeatureType.DRIVERDATA_TIMELINE -> {
                DriveKitDriverDataTimelineUI.startTimelineActivity(context)
            }
            FeatureType.DRIVERDATA_MYSYNTHESIS -> DriverDataUI.startMySynthesisActivity(context)
        }
    }

    private fun showPermissionUtilsOnboarding() =
        DiagnosisHelper.getLocationStatus(itemView.context) == PermissionStatus.NOT_VALID
                || DiagnosisHelper.getActivityStatus(itemView.context) == PermissionStatus.NOT_VALID
                || DiagnosisHelper.getBatteryOptimizationsStatus(itemView.context) == PermissionStatus.NOT_VALID
                || DiagnosisHelper.getNearbyDevicesStatus(itemView.context) == PermissionStatus.NOT_VALID
}
