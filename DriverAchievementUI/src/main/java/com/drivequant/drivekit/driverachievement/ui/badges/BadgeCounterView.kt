package com.drivequant.drivekit.driverachievement.ui.badges

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.driverachievement.ui.R

internal class BadgeCounterView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private lateinit var badgeTypeTextView: TextView
    private lateinit var badgeCountTextView: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.badgeTypeTextView = findViewById(R.id.badgeType)
        this.badgeCountTextView = findViewById(R.id.badgeCount)

        this.badgeTypeTextView.smallText()
        this.badgeCountTextView.headLine1()
    }

    fun update(@StringRes levelTitleResId: Int, @ColorRes colorResId: Int, acquired: Int, total: Int) {
        val color = ContextCompat.getColor(context, colorResId)
        badgeTypeTextView.text = resources.getString(levelTitleResId)
        badgeTypeTextView.setTextColor(color)
        val badgeCountText = "$acquired / $total"
        badgeCountTextView.text = badgeCountText
        badgeCountTextView.setTextColor(color)
        (this.background as? GradientDrawable)?.setStroke(1.convertDpToPx(), ContextCompat.getColor(context, colorResId))
    }
}
