package com.drivekit.demoapp.component

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.buttonText
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText

internal class FeatureCard : FrameLayout {

    private lateinit var ctx: Context

    private lateinit var iconTitle: ImageView
    private lateinit var textTitle: TextView
    private lateinit var infoImage: ImageView
    private lateinit var description: TextView
    private lateinit var buttonTitle: TextView

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        ctx = context
        val view = View.inflate(ctx, R.layout.dk_layout_feature_cardview, null)
        iconTitle = view.findViewById(R.id.icon_title)
        textTitle = view.findViewById(R.id.text_view_title)
        infoImage = view.findViewById(R.id.info_button)
        description = view.findViewById(R.id.text_view_description)
        buttonTitle = view.findViewById(R.id.text_view_button)
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun configureIcon(drawableResId: Int) {
        iconTitle.apply {
            setImageDrawable(ContextCompat.getDrawable(ctx, drawableResId))
            visibility = View.VISIBLE
        }
    }

    fun configureTitle(textResId: Int) {
        textTitle.apply {
            text = ctx.getString(textResId)
            headLine2(DriveKitUI.colors.complementaryFontColor())
        }
    }

    fun configureInfoButton(listener : FeatureCardInfoClickListener) {
        infoImage.apply {
            visibility = VISIBLE
            setOnClickListener {
                listener.onInfoClicked()
            }
        }
    }

    fun configureDescription(textResId: Int) {
        description.apply {
            text = ctx.getText(textResId)
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
    }

    fun configureActionButton(textResId: Int, listener: FeatureCardActionClickListener) {
        buttonTitle.apply {
            text = ctx.getString(textResId)
            buttonText(
                textColor = DriveKitUI.colors.secondaryColor(),
                backgroundColor = Color.parseColor("#00ffffff")
            )
            setOnClickListener {
                listener.onButtonClicked()
            }
        }
    }

    interface FeatureCardInfoClickListener {
        fun onInfoClicked()
    }

    interface FeatureCardActionClickListener {
        fun onButtonClicked()
    }
}