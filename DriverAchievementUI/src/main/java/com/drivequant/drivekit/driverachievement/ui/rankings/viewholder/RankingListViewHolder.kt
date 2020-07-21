package com.drivequant.drivekit.driverachievement.ui.rankings.viewholder

import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKUtils
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingDriverData


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
    private var driverPositionColor = DriveKitUI.colors.mainFontColor()
    private var driverScoreColor = DriveKitUI.colors.mainFontColor()

    fun bind(rankingDriverData: RankingDriverData?) {
       rankingDriverData?.let {
           container.visibility = View.VISIBLE
           imageViewJump.visibility = View.GONE
           if (it.driverId == DriveKit.config.userId) {
               driverPositionColor = DriveKitUI.colors.fontColorOnSecondaryColor()
               driverScoreColor = DriveKitUI.colors.fontColorOnSecondaryColor()
               DKUtils.setBackgroundDrawableColor(driverPositionBackground,
                   DriveKitUI.colors.secondaryColor())
               DKUtils.setBackgroundDrawableColor(driverScoreBackground,
                   DriveKitUI.colors.secondaryColor())
           } else {
               driverPositionColor = DriveKitUI.colors.mainFontColor()
               driverScoreColor = DriveKitUI.colors.mainFontColor()
               DKUtils.setBackgroundDrawableColor(driverScoreBackground,
                   DriveKitUI.colors.neutralColor())
               textViewDriverPosition.setBackgroundResource(0)
           }

           textViewDriverDistance.text = it.getFormattedDistance()
           textViewDriverScore.text = it.getFormattedScore(itemView.context, driverScoreColor)
           textViewDriverNickname.text = it.getNickname(itemView.context)

           if (it.driverRank in 1..3) {
               textViewDriverPosition.visibility = View.INVISIBLE
               imageViewDriverPosition.visibility = View.VISIBLE
               it.getRankDrawable(itemView.context)?.let { drawable ->
                   imageViewDriverPosition.setImageDrawable(drawable)
               }
           } else {
               imageViewDriverPosition.visibility = View.INVISIBLE
               textViewDriverPosition.visibility = View.VISIBLE
               textViewDriverPosition.text = "${it.driverRank}"
           }
            setStyle()

       } ?: kotlin.run {
           container.visibility = View.GONE
           imageViewJump.visibility = View.VISIBLE
       }
        viewSeparator.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }

    private fun setStyle() {
        textViewDriverNickname.headLine2()
        textViewDriverDistance.smallText(textColor = DriveKitUI.colors.complementaryFontColor())
        textViewDriverPosition.normalText(textColor = driverPositionColor)
    }
}