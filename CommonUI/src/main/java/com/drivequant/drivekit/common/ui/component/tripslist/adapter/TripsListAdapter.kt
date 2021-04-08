package com.drivequant.drivekit.common.ui.component.tripslist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.tripslist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.tripslist.viewModel.DKTripsByDate
import com.drivequant.drivekit.common.ui.component.tripslist.viewModel.DKTripsListViewModel
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.common.ui.component.tripslist.viewholder.HeaderDayViewHolder
import com.drivequant.drivekit.common.ui.component.tripslist.viewholder.TripViewHolder
import com.drivequant.drivekit.common.ui.extension.formatDate

internal class TripsListAdapter(
    var context: Context?, val viewModel: DKTripsListViewModel) : BaseExpandableListAdapter() {

    override fun getGroup(position: Int): DKTripsByDate = viewModel.tripsList.getTripsList()[position]

    override fun getGroupView(position: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        lateinit var holder: HeaderDayViewHolder
        lateinit var view: View
        val date = getGroup(position).date
        val trips = viewModel.getTripsByDate(date)

        if (convertView == null) {
            val layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.group_item_trip_list, null)
            holder = HeaderDayViewHolder(view)
            view.tag = holder
        } else {
            holder = convertView.tag as HeaderDayViewHolder
            view = convertView
        }

        holder.tvDate.text = date.formatDate(DKDatePattern.WEEK_LETTER).capitalize()


        val headerValue = viewModel.getCustomHeader()?.let {
              it.customTripListHeader(holder.itemView.context, trips?.trips) ?: run {
                  it.tripListHeader().text(holder.itemView.context, trips?.trips)
              }
          }
          holder.tvInformations.text = headerValue ?: run {
              viewModel.getHeaderDay().text(holder.itemView.context, trips?.trips)
          }

        holder.tvDate.setTextColor(DriveKitUI.colors.mainFontColor())
        holder.tvInformations.setTextColor(DriveKitUI.colors.mainFontColor())
        holder.background.background = ContextCompat.getDrawable(context!!, R.drawable.dk_background_header_trip_list)
        DrawableCompat.setTint(holder.background.background, DriveKitUI.colors.neutralColor())

        val expandableListView = parent as ExpandableListView
        expandableListView.expandGroup(position)
        FontUtils.overrideFonts(context, view)
        return view
    }

    override fun getGroupId(position: Int): Long = position.toLong()

    override fun getGroupCount(): Int = viewModel.tripsList.getTripsList().size

    override fun getChild(position: Int, expandedListPosition: Int): DKTripListItem? {
        val date = getGroup(position).date
        val tripsByDate = viewModel.getTripsByDate(date)
        return tripsByDate?.trips?.get(tripsByDate.trips.indices.last - expandedListPosition)
    }

    override fun getChildView(position: Int, expandedListPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val trip: DKTripListItem? = getChild(position, expandedListPosition)
        val holder: TripViewHolder
        val view: View

        if (convertView == null){
            view = View.inflate(context, R.layout.item_trip_list, null)
            view.setBackgroundColor(DriveKitUI.colors.transparentColor())
            holder = TripViewHolder(view)
            view.tag = holder
        } else {
            holder = convertView.tag as TripViewHolder
            view = convertView
        }

        trip?.let {
            holder.bind(trip, viewModel.getTripData(), isLastChild)
        }
        return view
    }

    override fun getChildId(position: Int, expandedListPosition: Int): Long =
        expandedListPosition.toLong()

    override fun getChildrenCount(position: Int): Int {
        val date = getGroup(position).date
        val trips = viewModel.getTripsByDate(date)?.trips
        return if (trips.isNullOrEmpty()){
            0
        } else {
            trips.size
        }
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean = true

    override fun hasStableIds(): Boolean = false
}