package com.drivequant.drivekit.driverachievement.ui.leaderboard.viewholder

import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.DKUtils
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingDriverData


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
    private val driverPositionBackground = textViewDriverPosition.background as GradientDrawable
    private val driverScoreBackground = textViewDriverScore.background as GradientDrawable

    fun bind(rankingDriverData: RankingDriverData) {
        val driverPositionColor:Int
        val driverScoreColor:Int

        if (rankingDriverData.driverId == DriveKit.config.userId) {
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

        textViewDriverDistance.text = DKDataFormatter.formatDistance(
            itemView.context,
            rankingDriverData.driverDistance
        )

        val driverScore = if(rankingDriverData.driverScore < 9) {
            rankingDriverData.driverScore.format(2)
        } else {
            rankingDriverData.driverScore.removeZeroDecimal()
        }

        textViewDriverScore.text =
            DKSpannable().append(driverScore, itemView.context.resSpans {
                size(R.dimen.dk_text_medium)
                color(driverScoreColor)
            }).append(" / 10", itemView.context.resSpans {
                size(R.dimen.dk_text_small)
                color(driverScoreColor)
            }).toSpannable()


        textViewDriverNickname.text =
            if (rankingDriverData.driverNickname.isNullOrEmpty()) DKResource.convertToString(
                itemView.context,
                "dk_achievements_ranking_anonymous_driver"
            ) else rankingDriverData.driverNickname

        if (rankingDriverData.driverRank in 1..3) {
            textViewDriverPosition.visibility = View.INVISIBLE
            imageViewDriverPosition.visibility = View.VISIBLE
            val rankResId = when (rankingDriverData.driverRank) {
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
            imageViewDriverPosition.visibility = View.INVISIBLE
            textViewDriverPosition.visibility = View.VISIBLE
            textViewDriverPosition.text = rankingDriverData.driverRank.toString()
        }

        textViewDriverNickname.headLine2()
        textViewDriverDistance.smallText(textColor = DriveKitUI.colors.complementaryFontColor())
        textViewDriverPosition.normalText(textColor = driverPositionColor)
        viewSeparator.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}