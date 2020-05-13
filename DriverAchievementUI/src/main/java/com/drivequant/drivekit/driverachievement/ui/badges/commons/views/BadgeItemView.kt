package com.drivequant.drivekit.driverachievement.ui.badges.commons.views

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeImage
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.common.ui.extension.highlightSmall
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.driverachievement.ui.R

/**
 * Created by Mohamed on 2020-05-13.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class BadgeItemView : LinearLayout {


    private lateinit var badgeGaugeImage: GaugeImage
    lateinit var badgeName: TextView

    private lateinit var badgeTitle: String
    private lateinit var badgeDescription: String
    private lateinit var badgeProgressCongrats: String

    private var gaugeDrawable: Drawable? = null
    private var gaugeColor: Int = 0
    private var gaugeProgress: Double = 0.0


    private var isBadgeClickable: Boolean = true

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.badges_item_view, null)
        badgeGaugeImage = view.findViewById(R.id.gauge_image)
        badgeName = view.findViewById(R.id.text_view_badge_name)

        badgeName.normalText(DriveKitUI.colors.complementaryFontColor())

        view.setOnClickListener {
            onBadgeClick()
        }

        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

    }

    fun configureBadge(
        progress: Double,
        gaugeDrawable: Drawable,
        gaugeColor: Int,
        badgeName: String
    ) {
        gaugeDrawable.let {
            this.gaugeDrawable = it
            this.gaugeProgress = progress
            this.gaugeColor = gaugeColor

            badgeGaugeImage.configure(
                progress,
                gaugeDrawable,
                gaugeColor
            )
            this.badgeName.text = badgeName
        }
    }

    fun setBadgeDescription(description: String) {
        badgeDescription = description
    }

    fun setBadgeTitle(title: String) {
        badgeTitle = title
    }

    fun setBadgeProgressCongrats(progressCongrats: String) {
        badgeProgressCongrats = progressCongrats
    }

    private fun onBadgeClick() {
        if (isBadgeClickable) {
            val badgeDetails = DKAlertDialog.LayoutBuilder()
                .init(context)
                .layout(R.layout.layout_badge_details)
                .cancelable(true)
                .negativeButton(context.getString(R.string.dk_common_cancel),
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                    }).show()

            val badgeTitle = badgeDetails.findViewById<TextView>(R.id.text_view_badge_title)
            val badgeDescription = badgeDetails.findViewById<TextView>(R.id.text_view_badge_goal)
            val badgeProgressCongrats = badgeDetails.findViewById<TextView>(R.id.text_view_badge_progress_congrats)

            val badgeItemView = badgeDetails.findViewById<BadgeItemView>(R.id.badge_item_view)
            badgeItemView?.badgeName?.visibility = GONE

            gaugeDrawable?.let {
                badgeItemView?.configureBadge(gaugeProgress,it, gaugeColor,"")
            }

            val goal = badgeDetails.findViewById<TextView>(R.id.text_view_goal)
            val progress = badgeDetails.findViewById<TextView>(R.id.text_view_progress)
            val viewSeparator = badgeDetails.findViewById<TextView>(R.id.view_separator)

            badgeTitle?.normalText(DriveKitUI.colors.fontColorOnPrimaryColor())
            badgeTitle?.setBackgroundColor(DriveKitUI.colors.primaryColor())

            viewSeparator?.setBackgroundColor(DriveKitUI.colors.neutralColor())

            badgeDescription?.bigText(DriveKitUI.colors.complementaryFontColor())
            badgeProgressCongrats?.bigText(DriveKitUI.colors.complementaryFontColor())

            goal?.highlightSmall(DriveKitUI.colors.primaryColor())
            progress?.highlightSmall(DriveKitUI.colors.primaryColor())

            badgeProgressCongrats?.text = this.badgeProgressCongrats
            badgeDescription?.text = this.badgeDescription
            badgeTitle?.text = this.badgeTitle

        }
    }
}