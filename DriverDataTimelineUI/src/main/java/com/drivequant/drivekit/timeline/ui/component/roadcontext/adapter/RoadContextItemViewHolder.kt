package com.drivequant.drivekit.timeline.ui.component.roadcontext.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.extension.tintFromHueOfColor
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.TimelineRoadContext
import com.drivequant.drivekit.timeline.ui.component.roadcontext.getColorResId
import com.drivequant.drivekit.timeline.ui.component.roadcontext.getTitleResId
import kotlinx.android.synthetic.main.dk_road_context_list_item.view.*

internal class RoadContextItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(roadContext: TimelineRoadContext) {
        val drawable = ContextCompat.getDrawable(
            itemView.context,
            R.drawable.dk_road_context_indicator
        )?.mutate()
        drawable?.tintDrawable(
            ContextCompat.getColor(
                itemView.context,
                roadContext.getColorResId()
            ).tintFromHueOfColor(DriveKitUI.colors.primaryColor())
        )
        with(itemView) {
            image_view_road_context_indicator.setImageDrawable(drawable)
            with(text_view_road_context_name) {
                text = DKResource.convertToString(itemView.context, roadContext.getTitleResId())
                setTextColor(DriveKitUI.colors.complementaryFontColor())
                smallText(DriveKitUI.colors.complementaryFontColor())
            }
        }
    }
}
