package com.drivequant.drivekit.challenge.ui.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.challenge.ui.viewmodel.ChallengeListData
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.utils.DKResource

class ChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val challengeTitle = itemView.findViewById<TextView>(R.id.dk_text_view_title)
    private val challengeDate = itemView.findViewById<TextView>(R.id.dk_text_view_date)
    private val challengeIcon = itemView.findViewById<ImageView>(R.id.dk_image_view_icon)

    fun bind(challengeListData: ChallengeListData) {
        challengeTitle.text = challengeListData.title
        challengeDate.text = "${challengeListData.startDate} - ${challengeListData.endDate}"
        DKResource.convertToDrawable(itemView.context, challengeListData.getChallengeResourceId())
            ?.let {
                challengeIcon.setImageDrawable(it)
            }
    }
}
