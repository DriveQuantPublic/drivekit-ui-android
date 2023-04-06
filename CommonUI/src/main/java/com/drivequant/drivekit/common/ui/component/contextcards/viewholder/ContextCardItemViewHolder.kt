package com.drivequant.drivekit.common.ui.component.contextcards.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.extension.tintFromHueOfColor

internal class ContextCardItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val indicator: ImageView = itemView.findViewById(R.id.image_view_context_card_indicator)
    private val title: TextView = itemView.findViewById(R.id.text_view_context_card_title)

    fun bind(@ColorRes sourceColor: Int, @StringRes text: Int) {
        val drawable = ContextCompat.getDrawable(
            itemView.context,
            R.drawable.dk_context_card_indicator
        )?.mutate()
        drawable?.tintDrawable(
            ContextCompat.getColor(
                itemView.context,
                sourceColor
            ).tintFromHueOfColor(DriveKitUI.colors.primaryColor())
        )
        this.indicator.setImageDrawable(drawable)
        with(this.title) {
            setText(text)
            smallText(DriveKitUI.colors.complementaryFontColor())
        }
    }
}
