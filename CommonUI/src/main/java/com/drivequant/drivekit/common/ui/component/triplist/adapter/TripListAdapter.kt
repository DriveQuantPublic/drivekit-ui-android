package com.drivequant.drivekit.common.ui.component.triplist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.DKTripListViewModel
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.DKTripsByDate
import com.drivequant.drivekit.common.ui.component.triplist.viewholder.HeaderDayViewHolder
import com.drivequant.drivekit.common.ui.component.triplist.viewholder.TripViewHolder
import com.drivequant.drivekit.common.ui.extension.capitalizeFirstLetter
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.tint
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.FontUtils

internal class TripListAdapter(
    var context: Context?,
    private val tripsListViewModel: DKTripListViewModel
) : BaseExpandableListAdapter() {

    override fun getGroup(position: Int): DKTripsByDate = tripsListViewModel.sortedTrips[position]

    override fun getGroupView(position: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        lateinit var holder: HeaderDayViewHolder
        lateinit var view: View
        val date = getGroup(position).date
        val trips = tripsListViewModel.getTripsByDate(date)

        if (convertView == null) {
            val layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.group_item_trip_list, parent, false)
            holder = HeaderDayViewHolder(view)
            view.tag = holder
        } else {
            holder = convertView.tag as HeaderDayViewHolder
            view = convertView
        }

        holder.tvDate.text = date.formatDate(DKDatePattern.WEEK_LETTER).capitalizeFirstLetter()
        val headerValue = tripsListViewModel.getCustomHeader()?.let {
              it.customTripListHeader(holder.itemView.context, trips?.trips) ?: run {
                  it.tripListHeader().text(holder.itemView.context, trips?.trips)
              }
          }
          holder.tvInformations.text = headerValue ?: run {
              tripsListViewModel.getHeaderDay().text(holder.itemView.context, trips?.trips)
          }

        context?.let { context ->
            holder.background.background = ContextCompat.getDrawable(context, R.drawable.dk_background_header_trip_list)?.also {
                it.tint(context, R.color.neutralColor)
            }
        }

        val expandableListView = parent as ExpandableListView
        expandableListView.expandGroup(position)
        FontUtils.overrideFonts(context, view)
        return view
    }

    override fun getGroupId(position: Int): Long = position.toLong()

    override fun getGroupCount(): Int = tripsListViewModel.sortedTrips.size

    override fun getChild(position: Int, expandedListPosition: Int): DKTripListItem? {
        val date = getGroup(position).date
        val tripsByDate = tripsListViewModel.getTripsByDate(date)
        return tripsByDate?.trips?.get(tripsByDate.trips.indices.last - expandedListPosition)
    }

    override fun getChildView(position: Int, expandedListPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val trip: DKTripListItem? = getChild(position, expandedListPosition)
        val holder: TripViewHolder
        val view: View

        if (convertView == null){
            view = View.inflate(context, R.layout.item_trip_list, null)
            holder = TripViewHolder(view)
            view.tag = holder
        } else {
            holder = convertView.tag as TripViewHolder
            view = convertView
        }

        trip?.let {
            holder.bind(trip, tripsListViewModel.getTripData(), isLastChild, addHorizontalPadding = true)
        }
        return view
    }

    override fun getChildId(position: Int, expandedListPosition: Int): Long =
        expandedListPosition.toLong()

    override fun getChildrenCount(position: Int): Int {
        val date = getGroup(position).date
        val trips = tripsListViewModel.getTripsByDate(date)?.trips
        return if (trips.isNullOrEmpty()){
            0
        } else {
            trips.size
        }
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean = true

    override fun hasStableIds(): Boolean = false
}
