package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.common.ui.extension.normalText

class SwitchSettings: LinearLayout {

    private lateinit var textViewTitle: TextView
    private lateinit var textViewDescription:TextView
    private lateinit var switchView: SwitchCompat

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_layout_switch_settings, null)
        textViewTitle = view.findViewById(R.id.text_view_title)
        textViewDescription = view.findViewById(R.id.text_view_description)
        switchView = view.findViewById(R.id.switch_view)

        textViewTitle.bigText(DriveKitUI.colors.primaryColor())
        textViewTitle.setTypeface(DriveKitUI.primaryFont(context), Typeface.NORMAL)

        textViewDescription.normalText(DriveKitUI.colors.mainFontColor())
        textViewDescription.setTypeface(DriveKitUI.primaryFont(context), Typeface.NORMAL)

        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    fun onCreateView(view: View) {
        textViewTitle = view.findViewById(R.id.text_view_title)
        textViewDescription = view.findViewById(R.id.text_view_description)
        switchView = view.findViewById(R.id.switch_view)
    }

    fun setTitle(title: String) {
        textViewTitle.text = title
    }

    fun setDescription(description: String) {
        textViewDescription.text = description
    }

    fun setChecked(checked: Boolean) {
        switchView.isChecked = checked
    }

    fun isChecked(): Boolean {
        return switchView.isChecked
    }
}