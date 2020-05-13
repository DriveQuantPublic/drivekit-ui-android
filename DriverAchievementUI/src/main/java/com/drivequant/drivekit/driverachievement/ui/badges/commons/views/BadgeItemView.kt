package com.drivequant.drivekit.driverachievement.ui.badges.commons.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.drivequant.drivekit.common.ui.component.GaugeImage
import com.drivequant.drivekit.driverachievement.ui.R

/**
 * Created by Mohamed on 2020-05-13.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class BadgeItemView : LinearLayout {


    private lateinit var badgeGaugeImage: GaugeImage
    private lateinit var badgeName: TextView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.badge_item_view, null)
        badgeGaugeImage = view.findViewById(R.id.gauge_image)
        badgeName = view.findViewById(R.id.text_view_badge_name)

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

    fun configureBadge(progress: Double, gaugeDrawable: Drawable, gaugeColor: Int, name: String) {
        gaugeDrawable.let {
            badgeGaugeImage.configure(
                progress,
                gaugeDrawable,
                gaugeColor
            )
            badgeName.text = name
        }
    }

    fun onBadgeClick() {

    }

    override fun setOnClickListener(l: OnClickListener?) {
        Toast.makeText(context, "Hola", Toast.LENGTH_SHORT).show()
    }

    override fun isClickable(): Boolean {
        return true
    }
}