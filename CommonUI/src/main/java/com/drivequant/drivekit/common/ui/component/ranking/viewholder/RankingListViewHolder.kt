package com.drivequant.drivekit.common.ui.component.ranking.viewholder

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRankingItem
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalTextWithColor
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.graphical.DKColors


class RankingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val textViewDriverDistance =
        itemView.findViewById<TextView>(R.id.text_view_driver_distance)
    private val textViewDriverPseudo =
        itemView.findViewById<TextView>(R.id.text_view_driver_pseudo)
    private val textViewDriverPosition =
        itemView.findViewById<TextView>(R.id.text_view_driver_position)
    private val imageViewDriverPosition =
        itemView.findViewById<ImageView>(R.id.image_view_driver_position)
    private val textViewDriverScore = itemView.findViewById<TextView>(R.id.text_view_driver_score)
    private val container = itemView.findViewById<LinearLayout>(R.id.ranking_item_container)
    private val imageViewJump = itemView.findViewById<ImageView>(R.id.image_view_jump)
    private val driverPositionBackground = textViewDriverPosition.background as GradientDrawable
    private val driverScoreBackground = textViewDriverScore.background as GradientDrawable
    private var currentDriverColor = DKColors.mainFontColor

    fun bind(rankingDriverData: DKDriverRankingItem) {
       if (!rankingDriverData.isJumpRank()) {
           val context = itemView.context
           container.visibility = View.VISIBLE
           imageViewJump.visibility = View.GONE
           if (rankingDriverData.isCurrentUser()) {
               currentDriverColor = DKColors.fontColorOnSecondaryColor
               driverPositionBackground.setColor(DKColors.secondaryColor)
               driverScoreBackground.setColor(DKColors.secondaryColor)
           } else {
               currentDriverColor = DKColors.mainFontColor
               driverScoreBackground.setColor(DKColors.neutralColor)
               driverPositionBackground.setColor(DKColors.transparentColor)
           }

           textViewDriverDistance.text = rankingDriverData.getDistance(context)
           textViewDriverScore.text = rankingDriverData.getScore(context, currentDriverColor)
           textViewDriverPseudo.text = rankingDriverData.getPseudo(context)

           if (rankingDriverData.getRank() in 1..3) {
               textViewDriverPosition.visibility = View.INVISIBLE
               imageViewDriverPosition.visibility = View.VISIBLE
               rankingDriverData.getRankResource(context)?.let { drawable ->
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
        textViewDriverPosition.normalTextWithColor(textColor = currentDriverColor)
        textViewDriverPseudo.headLine2()
        textViewDriverDistance.smallText()
    }
}
