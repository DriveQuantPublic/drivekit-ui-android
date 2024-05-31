package com.drivequant.drivekit.driverachievement.ui.badges.commons.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import com.drivequant.drivekit.common.ui.component.GaugeImage
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.driverachievement.ui.R


class BadgeItemView : LinearLayout {

    private lateinit var badgeGaugeImage: GaugeImage
    private lateinit var badgeName: TextView
    private lateinit var gaugeDrawable: Drawable

    private lateinit var badgeTitle: String
    private lateinit var badgeDescription: String
    private lateinit var badgeProgressCongrats: String

    @ColorRes
    private var gaugeColor: Int = android.R.color.black
    private var gaugeProgress: Double = 0.0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_badges_item_view, null).setDKStyle()
        badgeGaugeImage = view.findViewById(R.id.gauge_image)
        badgeName = view.findViewById(R.id.text_view_badge_name)

        badgeName.normalText()

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

    fun configureBadge(progress: Double, gaugeDrawable: Drawable, @ColorRes gaugeColor: Int, badgeName: String) {
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
            .layout(R.layout.dk_layout_badge_details)
            .cancelable(true)
            .negativeButton(context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_close))
            .show()

        val badgeTitle = badgeDetails.findViewById<TextView>(R.id.text_view_badge_title)
        val badgeDescription = badgeDetails.findViewById<TextView>(R.id.text_view_badge_goal)
        val badgeProgressCongrats = badgeDetails.findViewById<TextView>(R.id.text_view_badge_progress_congrats)
        val gaugeImage = badgeDetails.findViewById<GaugeImage>(R.id.gauge_image)
        val goal = badgeDetails.findViewById<TextView>(R.id.text_view_goal)
        val progress = badgeDetails.findViewById<TextView>(R.id.text_view_progress)

        gaugeImage?.configure(gaugeProgress, gaugeDrawable, gaugeColor)

        badgeProgressCongrats?.apply {
            text = this@BadgeItemView.badgeProgressCongrats
            normalText()
        }
        badgeDescription?.apply {
            text = this@BadgeItemView.badgeDescription
            normalText()
        }
        badgeTitle?.apply {
            text = this@BadgeItemView.badgeTitle
            normalText()
        }
        goal?.headLine2()
        progress?.headLine2()
    }
}
