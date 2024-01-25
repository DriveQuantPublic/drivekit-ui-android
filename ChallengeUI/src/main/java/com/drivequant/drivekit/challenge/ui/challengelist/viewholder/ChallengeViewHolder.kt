package com.drivequant.drivekit.challenge.ui.challengelist.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.ChallengeData
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.DKDatePattern

internal class ChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val challengeIcon = itemView.findViewById<ImageView>(R.id.dk_image_view_icon)
    private val calendarIcon = itemView.findViewById<ImageView>(R.id.calendar_icon)
    private val title = itemView.findViewById<TextView>(R.id.dk_text_view_title)
    private val dates = itemView.findViewById<TextView>(R.id.dk_text_view_date)
    private val participationText = itemView.findViewById<TextView>(R.id.dk_text_view_participation)

    fun bind(challengeData: ChallengeData) {
        challengeData.getIconResId()?.let {
            challengeIcon.setImageResource(it)
        }
        title.text = challengeData.title
        dates.text = buildString {
            append(challengeData.startDate.formatDate(DKDatePattern.STANDARD_DATE))
            append(" - ")
            append(challengeData.endDate.formatDate(DKDatePattern.STANDARD_DATE))
        }
        participationText.text = challengeData.getParticipationText(itemView.context)
        setStyle()
    }

    private fun setStyle() {
        this.title.headLine1(DriveKitUI.colors.primaryColor())
        this.calendarIcon.drawable.tintDrawable(DriveKitUI.colors.complementaryFontColor())
        this.dates.smallText(DriveKitUI.colors.complementaryFontColor())
        this.participationText.normalText(DriveKitUI.colors.secondaryColor())
    }
}
