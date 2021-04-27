package com.drivequant.drivekit.common.ui.component.ranking.viewholder

import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRankingItem
import com.drivequant.drivekit.common.ui.extension.*


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
    private val viewSeparator = itemView.findViewById<TextView>(R.id.view_separator)
    private val container = itemView.findViewById<LinearLayout>(R.id.ranking_item_container)
    private val imageViewJump = itemView.findViewById<ImageView>(R.id.image_view_jump)
    private val driverPositionBackground = textViewDriverPosition.background as GradientDrawable
    private val driverScoreBackground = textViewDriverScore.background as GradientDrawable
    private var currentDriverColor = DriveKitUI.colors.mainFontColor()

    fun bind(rankingDriverData: DKDriverRankingItem) {
       if (!rankingDriverData.isJumpRank()) {
           container.visibility = View.VISIBLE
           imageViewJump.visibility = View.GONE
           if (rankingDriverData.isCurrentUser()) {
               currentDriverColor = DriveKitUI.colors.fontColorOnSecondaryColor()
               driverPositionBackground.setColor(DriveKitUI.colors.secondaryColor())
               driverScoreBackground.setColor(DriveKitUI.colors.secondaryColor())
           } else {
               currentDriverColor = DriveKitUI.colors.mainFontColor()
               driverScoreBackground.setColor(DriveKitUI.colors.neutralColor())
               driverPositionBackground.setColor(DriveKitUI.colors.transparentColor())
           }

           textViewDriverDistance.text = rankingDriverData.getDistance(itemView.context)
           textViewDriverScore.text = rankingDriverData.getScore(itemView.context, currentDriverColor)
           textViewDriverNickname.text = rankingDriverData.getNickname(itemView.context)

           if (rankingDriverData.getRank() in 1..3) {
               textViewDriverPosition.visibility = View.INVISIBLE
               imageViewDriverPosition.visibility = View.VISIBLE
               rankingDriverData.getRankResource(itemView.context)?.let { drawable ->
                   imageViewDriverPosition.setImageDrawable(drawable)
               }
           } else {
               imageViewDriverPosition.visibility = View.INVISIBLE
               textViewDriverPosition.visibility = View.VISIBLE
               textViewDriverPosition.text = "${rankingDriverData.getRank()}"
           }
       } else {
           container.visibility = View.GONE
           imageViewJump.visibility = View.VISIBLE
       }
        setStyle()
    }

    private fun setStyle() {
        textViewDriverPosition.normalText(textColor = currentDriverColor)
        textViewDriverNickname.headLine2()
        textViewDriverDistance.smallText(textColor = DriveKitUI.colors.complementaryFontColor())
        viewSeparator.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}