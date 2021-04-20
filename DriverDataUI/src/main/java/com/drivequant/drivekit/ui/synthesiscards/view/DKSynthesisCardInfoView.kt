package com.drivequant.drivekit.ui.synthesiscards.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.ui.R
import kotlinx.android.synthetic.main.dk_synthesis_card_infoview_item.view.*

internal class DKSynthesisCardInfoView(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs) {

    fun init(icon: Drawable, text: Spannable) {
        val view = View.inflate(context, R.layout.dk_synthesis_card_infoview_item, null)

        view.textview.text = text
        view.textview.bigText(DriveKitUI.colors.complementaryFontColor())
        view.textview.compoundDrawablePadding = 36
        view.textview.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)

        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}