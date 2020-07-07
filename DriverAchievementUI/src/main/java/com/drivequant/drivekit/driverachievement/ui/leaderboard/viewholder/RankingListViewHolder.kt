package com.drivequant.drivekit.driverachievement.ui.leaderboard.viewholder

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
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
    private val viewSeparator = itemView.findViewById<TextView>(R.id.view_separator)
    private val driverPositionBackground = textViewDriverPosition.background as GradientDrawable
    private val driverScoreBackground = textViewDriverScore.background as GradientDrawable


    fun bind(driverRanked: DriverRanked, userPosition: Int) {
        textViewDriverDistance.text =
            DKDataFormatter.formatDistance(itemView.context, driverRanked.distance)
        textViewDriverScore.text =
            DKSpannable().append(driverRanked.score.removeZeroDecimal(), itemView.context.resSpans {
                size(R.dimen.dk_text_medium)
                color(DriveKitUI.colors.mainFontColor())
            }).append(" / 10", itemView.context.resSpans {
                size(R.dimen.dk_text_small)
                color(DriveKitUI.colors.mainFontColor())
            }).toSpannable()

        textViewDriverNickname.text =
            if (driverRanked.nickname.isNullOrEmpty()) "-" else driverRanked.nickname

        if (driverRanked.rank == 1 || driverRanked.rank == 2 || driverRanked.rank == 3) {
            textViewDriverPosition.visibility = View.INVISIBLE
            val rankResId = when (driverRanked.rank) {
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

        viewSeparator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        setBackgroundDrawableColor(driverPositionBackground, DriveKitUI.colors.secondaryColor())
        setBackgroundDrawableColor(driverScoreBackground, DriveKitUI.colors.neutralColor())
    }

    private fun setBackgroundDrawableColor(background: GradientDrawable, color: Int) {
        when (background) {
            is ShapeDrawable -> (background as ShapeDrawable).paint.color = color
            is GradientDrawable -> background.setColor(color)
            is ColorDrawable -> (background as ColorDrawable).color = color
        }
    }
}