package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.content.res.TypedArray
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.drivequant.drivekit.common.ui.R

class SwitchSettings: LinearLayout {

    private lateinit var textViewTitle: TextView
    private lateinit var textViewDescription:TextView
    private lateinit var switchView: SwitchCompat

    private var onCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val view = View.inflate(context, R.layout.dk_layout_switch_settings, null)

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

    fun setChecked(checked: Boolean) {
        switchView.isChecked = checked
    }

    fun isChecked(): Boolean {
        return switchView.isChecked
    }

}