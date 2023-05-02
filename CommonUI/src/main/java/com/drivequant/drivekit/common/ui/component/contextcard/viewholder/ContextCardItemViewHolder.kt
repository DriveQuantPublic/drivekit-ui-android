package com.drivequant.drivekit.common.ui.component.contextcard.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.extension.tintFromHueOfColor

internal class ContextCardItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val indicator: ImageView = itemView.findViewById(R.id.image_view_context_card_indicator)
    private val titleView: TextView = itemView.findViewById(R.id.text_view_context_card_title)
    private val subtitleView: TextView = itemView.findViewById(R.id.text_view_context_card_subtitle)

    fun bind(@ColorRes sourceColor: Int, title: String, subtitle: String?) {
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
        if (subtitle != null) {
            with(this.titleView) {
                this.text = title
                normalText(DriveKitUI.colors.primaryColor())
            }
            with(this.subtitleView) {
                this.text = subtitle
                normalText(DriveKitUI.colors.complementaryFontColor())
                visibility = View.VISIBLE
            }
        } else {
            with(this.titleView) {
                this.text = title
                normalText(DriveKitUI.colors.complementaryFontColor())
            }
            this.subtitleView.visibility = View.GONE
        }
    }
}
