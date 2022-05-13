package com.drivekit.demoapp.dashboard.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.drivekit.demoapp.dashboard.enum.InfoBannerType
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.extension.normalText

internal class InfoBannerView : LinearLayout {

    private lateinit var view: View
    private lateinit var icon: ImageView
    private lateinit var title: TextView
    private lateinit var arrow: ImageView

    constructor(context: Context, infoBannerType: InfoBannerType, listener: InfoBannerListener? = null) : super(context) {
        init(context, infoBannerType, listener)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    interface InfoBannerListener {
        fun onInfoBannerClicked()
    }

    private fun init(context: Context, infoBannerType: InfoBannerType, listener: InfoBannerListener? = null) {
        view = View.inflate(context, R.layout.layout_info_banner_item, null)
        icon = view.findViewById(R.id.image_view_info_banner_icon)
        title = view.findViewById(R.id.text_view_info_banner_title)
        arrow = view.findViewById(R.id.image_view_info_banner_arrow)

        view.apply {
            setBackgroundColor(infoBannerType.getBackgroundColorResId())
            setOnClickListener { listener?.onInfoBannerClicked() }
        }
        ContextCompat.getDrawable(context, infoBannerType.getIconResId())?.let {
            it.mutate()
            DrawableCompat.setTint(it, infoBannerType.getColor())
            icon.setImageDrawable(it)
        }

        title.apply {
            text = context.getString(infoBannerType.getTitleResId())
            normalText(infoBannerType.getColor())
        }

        if (infoBannerType.displayArrow()) {
            ContextCompat.getDrawable(context, R.drawable.dk_common_arrow_forward)?.let {
                arrow.apply {
                    it.mutate()
                    DrawableCompat.setTint(it, infoBannerType.getColor())
                    setImageDrawable(it)
                    visibility = View.VISIBLE
                }
            } ?: run {
                visibility = View.GONE
            }
        } else {
            visibility = View.GONE
        }

        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }
}