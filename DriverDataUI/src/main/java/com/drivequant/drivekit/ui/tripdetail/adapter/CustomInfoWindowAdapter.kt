package com.drivequant.drivekit.ui.tripdetail.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

internal class CustomInfoWindowAdapter(
    var context: Context,
    private val tripDetailViewModel: TripDetailViewModel) : GoogleMap.InfoWindowAdapter {

    var view: View = View.inflate(context, R.layout.item_info_marker, null)

    override fun getInfoContents(marker: Marker): View {
        return view
    }

    override fun getInfoWindow(marker: Marker): View {
        FontUtils.overrideFonts(context, view)
        val event = tripDetailViewModel.displayEvents[marker.tag as Int]
        val eventHour = view.findViewById<TextView>(R.id.text_view_time)
        eventHour.text = event.time.formatDate(DKDatePattern.HOUR_MINUTE_LETTER)

        val bubbleTitle = view.findViewById<TextView>(R.id.bubble_title)
        bubbleTitle.text = event.getTitle(context)
        bubbleTitle.headLine2()

        val descriptionTextView = view.findViewById<TextView>(R.id.bubble_description)
        descriptionTextView.smallText()
        event.getDescription(view.context, tripDetailViewModel.trip!!)?.let {description ->
            descriptionTextView.visibility  = View.VISIBLE
            descriptionTextView.text = description
        } ?: kotlin.run {
            descriptionTextView.visibility  = View.GONE
        }

        val bubbleInfo = view.findViewById<ImageView>(R.id.bubble_more_info)
        bubbleInfo.setColorFilter(DKColors.secondaryColor)
        if (event.showInfoIcon()) {
            bubbleInfo.visibility = View.VISIBLE
        } else {
            bubbleInfo.visibility = View.INVISIBLE
        }
        return view
    }

    fun displayInfo(marker: Marker){
        val event = tripDetailViewModel.displayEvents[marker.tag as Int]
        if (event.showInfoIcon()) {
            val alert = DKAlertDialog.LayoutBuilder()
                .init(context)
                .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                .positiveButton()
                .show()

            val title = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
            val description = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)

            title?.text = marker.title
            description?.text = event.getExplanation(context)

            title?.headLine1()
            description?.normalText()
        }
    }
}
