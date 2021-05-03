package com.drivequant.drivekit.challenge.ui.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.challenge.ui.viewmodel.ChallengeData
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKit

class ChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val challengeTitle = itemView.findViewById<TextView>(R.id.dk_text_view_title)
    private val challengeDate = itemView.findViewById<TextView>(R.id.dk_text_view_date)
    private val challengeIcon = itemView.findViewById<ImageView>(R.id.dk_image_view_icon)

    fun bind(challengeListData: ChallengeData) {
        challengeTitle.text = challengeListData.title
        challengeDate.text = "${challengeListData.startDate.formatDate(DKDatePattern.STANDARD_DATE)} - ${challengeListData.endDate.formatDate(DKDatePattern.STANDARD_DATE)}"
        DKResource.convertToDrawable(itemView.context, challengeListData.getChallengeResourceId())
            ?.let {
                challengeIcon.setImageDrawable(it)
            }
        setStyle()
    }

    private fun setStyle() {
        challengeTitle.headLine1()
        challengeDate.setTextColor(DriveKitUI.colors.mainFontColor())

    }
}
