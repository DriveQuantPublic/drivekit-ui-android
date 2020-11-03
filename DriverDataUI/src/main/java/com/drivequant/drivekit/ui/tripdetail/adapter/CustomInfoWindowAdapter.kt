package com.drivequant.drivekit.ui.tripdetail.adapter

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(
    var context: Context,
    private val tripDetailViewModel: TripDetailViewModel) : GoogleMap.InfoWindowAdapter {

    var view: View = View.inflate(context, R.layout.item_info_marker, null)

    override fun getInfoContents(marker: Marker?): View {
        return view
    }

    override fun getInfoWindow(marker: Marker?): View {
        FontUtils.overrideFonts(context, view)
        marker?.let{
            val event = tripDetailViewModel.displayEvents[it.tag as Int]
            val eventHour = view.findViewById<TextView>(R.id.text_view_time)
            eventHour.text = event.time.formatDate(DKDatePattern.HOUR_MINUTE_LETTER)
            eventHour.setTextColor(DriveKitUI.colors.complementaryFontColor())

            val bubbleTitle = view.findViewById<TextView>(R.id.bubble_title)
            bubbleTitle.text = event.getTitle(context)
            bubbleTitle.headLine2(DriveKitUI.colors.primaryColor())

            val descriptionTextView = view.findViewById<TextView>(R.id.bubble_description)
            event.getDescription(view.context, tripDetailViewModel.trip!!)?.let {description ->
                descriptionTextView.visibility  = View.VISIBLE
                descriptionTextView.text = description
            } ?: kotlin.run {
                descriptionTextView.visibility  = View.GONE
            }

            val bubbleInfo = view.findViewById<ImageView>(R.id.bubble_more_info)
            bubbleInfo.setColorFilter(DriveKitUI.colors.secondaryColor())
            if (event.showInfoIcon()){
                bubbleInfo.visibility = View.VISIBLE
            }else{
                bubbleInfo.visibility = View.INVISIBLE
            }
        }
        return view
    }

    fun displayInfo(marker: Marker){
        val event = tripDetailViewModel.displayEvents[marker.tag as Int]
        if (event.showInfoIcon()) {
            AlertDialog.Builder(context)
                .setTitle(marker.title)
                .setMessage(event.getExplanation(context))
                .setPositiveButton(android.R.string.yes) { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}