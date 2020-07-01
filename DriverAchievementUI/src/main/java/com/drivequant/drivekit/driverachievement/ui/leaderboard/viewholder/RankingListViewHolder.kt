package com.drivequant.drivekit.driverachievement.ui.leaderboard.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.DriverRanked
import com.drivequant.drivekit.driverachievement.ui.R

class RankingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val textViewDriverDistance =
        itemView.findViewById<TextView>(R.id.text_view_driver_distance)
    private val textViewDriverNickname =
        itemView.findViewById<TextView>(R.id.text_view_driver_nickname)
    private val textViewDriverPosition =
        itemView.findViewById<TextView>(R.id.text_view_driver_position)
    private val imageViewDriverPosition =
        itemView.findViewById<ImageView>(R.id.image_view_driver_position)
    private val textViewDriverScore = itemView.findViewById<TextView>(R.id.text_view_driver_score)

    fun bind(driverRanked: DriverRanked) {
        textViewDriverDistance.text = driverRanked.distance.toString()
        textViewDriverScore.text = driverRanked.score.toString()
        textViewDriverNickname.text = driverRanked.nickname

        if(driverRanked.rank in 1..3) {
           val rankResId =  when (driverRanked.rank) {
                1 -> "dk_achievements_rank_1"
                2 -> "dk_achievements_rank_2"
                3 -> "dk_achievements_rank_3"
               else -> null
           }

           val drawable = rankResId?.let {
                DKResource.convertToDrawable(
                    itemView.context,
                    it
                )
            }
            imageViewDriverPosition.setImageDrawable(drawable)
        } else {
            textViewDriverPosition.text = driverRanked.rank.toString()
        }
        setStyle()
    }

    private fun setStyle() {
        textViewDriverNickname.headLine2()
        textViewDriverDistance.smallText(textColor = DriveKitUI.colors.complementaryFontColor())
        textViewDriverPosition.bigText()
    }
}