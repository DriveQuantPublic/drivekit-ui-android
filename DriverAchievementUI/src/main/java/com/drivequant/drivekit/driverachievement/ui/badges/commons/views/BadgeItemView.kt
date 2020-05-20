package com.drivequant.drivekit.driverachievement.ui.badges.commons.views

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeImage
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.driverachievement.ui.R

/**
 * Created by Mohamed on 2020-05-13.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class BadgeItemView : LinearLayout {

    private lateinit var badgeGaugeImage: GaugeImage
    private lateinit var badgeName: TextView
    private lateinit var gaugeDrawable: Drawable

    private lateinit var badgeTitle: String
    private lateinit var badgeDescription: String
    private lateinit var badgeProgressCongrats: String

    private var gaugeColor: Int = Color.rgb(0, 0, 0)
    private var gaugeProgress: Double = 0.0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.badges_item_view, null).setDKStyle()
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

    fun configureBadge(progress: Double, gaugeDrawable: Drawable, gaugeColor: Int, badgeName: String) {
        this.gaugeDrawable = gaugeDrawable
        this.gaugeProgress = progress
        this.gaugeColor = gaugeColor
        this.badgeName.text = badgeName
        badgeGaugeImage.configure(progress, gaugeDrawable, gaugeColor)
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
        val badgeProgressCongrats =
            badgeDetails.findViewById<TextView>(R.id.text_view_badge_progress_congrats)
        val gaugeImage = badgeDetails.findViewById<GaugeImage>(R.id.gauge_image)
        val goal = badgeDetails.findViewById<TextView>(R.id.text_view_goal)
        val progress = badgeDetails.findViewById<TextView>(R.id.text_view_progress)
        val goalViewSeparator = badgeDetails.findViewById<TextView>(R.id.goal_view_separator)
        val progressViewSeparator =
            badgeDetails.findViewById<TextView>(R.id.progress_view_separator)

        gaugeImage?.configure(gaugeProgress, gaugeDrawable, gaugeColor)

        badgeProgressCongrats?.text = this.badgeProgressCongrats
        badgeDescription?.text = this.badgeDescription
        badgeTitle?.text = this.badgeTitle

        badgeTitle?.normalText(DriveKitUI.colors.fontColorOnPrimaryColor())
        badgeTitle?.setBackgroundColor(DriveKitUI.colors.primaryColor())
        progressViewSeparator?.setBackgroundColor(DriveKitUI.colors.neutralColor())
        goalViewSeparator?.setBackgroundColor(DriveKitUI.colors.neutralColor())
        badgeDescription?.normalText(DriveKitUI.colors.complementaryFontColor())
        badgeProgressCongrats?.normalText(DriveKitUI.colors.complementaryFontColor())
        goal?.headLine2(DriveKitUI.colors.primaryColor())
        progress?.headLine2(DriveKitUI.colors.primaryColor())
    }
}