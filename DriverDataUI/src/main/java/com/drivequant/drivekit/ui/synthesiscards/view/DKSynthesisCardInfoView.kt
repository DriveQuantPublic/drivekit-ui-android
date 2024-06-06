package com.drivequant.drivekit.ui.synthesiscards.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.ui.R

internal class DKSynthesisCardInfoView(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs) {

    fun init(icon: Drawable, text: Spannable) {
        val view = View.inflate(context, R.layout.dk_synthesis_card_infoview_item, null)

        val textView = view.findViewById<TextView>(R.id.textview)
        textView.text = text
        textView.bigText()
        textView.compoundDrawablePadding = 36
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)

        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}
