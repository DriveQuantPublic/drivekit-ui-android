package com.drivekit.demoapp.notification.settings.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI

internal class NotificationButton : LinearLayout {

    private lateinit var textViewTitle: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var arrowForward: ImageView

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val view = View.inflate(context, R.layout.layout_notification_button, null)
        textViewTitle = view.findViewById(R.id.text_view_title)
        textViewDescription = view.findViewById(R.id.text_view_description)
        arrowForward = view.findViewById(R.id.image_view_arrow)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.NotificationButton, 0, 0)
            try {
                a.getString(R.styleable.NotificationButton_notificationButtonTitle)?.let {
                    textViewTitle.text = it
                }
                a.getString(R.styleable.NotificationButton_notificationButtonDescription)?.let {
                    textViewDescription.text = it
                }
            } finally {
                a.recycle()
            }
        }

        DrawableCompat.setTint(arrowForward.drawable, DriveKitUI.colors.mainFontColor())

        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    fun setNotificationTitle(title: String) {
        textViewTitle.text = title
    }

    fun setNotificationDescription(description: String) {
        textViewDescription.text = description
    }

    fun setWarning() {
        textViewDescription.setTextColor(Color.RED)
    }

    fun hideWarning() {
        textViewDescription.setTextColor(Color.parseColor("#808080"))
    }
}