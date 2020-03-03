package com.drivequant.drivekit.ui.trips.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.HeaderDay
import com.drivequant.drivekit.ui.trips.viewholder.HeaderDayViewHolder
import com.drivequant.drivekit.ui.trips.viewholder.TripViewHolder
import com.drivequant.drivekit.ui.trips.viewmodel.TripsByDate
import com.drivequant.drivekit.ui.trips.viewmodel.TripsListViewModel
import java.text.SimpleDateFormat
import java.util.*

class TripsListAdapter(
    var context: Context?,
    var tripsListViewModel: TripsListViewModel)
    : BaseExpandableListAdapter() {

    override fun getGroup(position: Int): TripsByDate {
        return tripsListViewModel.tripsByDate[position]
    }

    override fun getGroupView(position: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        lateinit var holder: HeaderDayViewHolder
        lateinit var view: View
        val date = getGroup(position).date
        val trips = tripsListViewModel.getTripsByDate(date)

        if (convertView == null){
            val layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.group_item_trip_list, null)
            holder = HeaderDayViewHolder(view)
            view.tag = holder
            FontUtils.overrideFonts(context,view)
        } else {
            holder = convertView.tag as HeaderDayViewHolder
            view = convertView
        }

        val dateFormatDate = SimpleDateFormat("EEEE d MMMM", Locale.getDefault())
        val stringDate: String = dateFormatDate.format(date)

        holder.tvDate.text = stringDate.capitalize()
        holder.tvInformations.text = HeaderDay.DISTANCE_DURATION.text(holder.itemView.context, trips?.trips)

        val expandableListView = parent as ExpandableListView
        expandableListView.expandGroup(position)

        return view
    }

    override fun getGroupId(position: Int): Long {
        return position.toLong()
    }

    override fun getGroupCount(): Int {
        return tripsListViewModel.tripsByDate.size
    }

    override fun getChild(position: Int, expandedListPosition: Int): Trip? {
        val date = getGroup(position).date
        val tripsByDate = tripsListViewModel.getTripsByDate(date)
        return tripsByDate?.trips?.get(tripsByDate.trips.indices.last - expandedListPosition)
    }

    override fun getChildView(position: Int, expandedListPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val trip: Trip? = getChild(position, expandedListPosition)
        val holder: TripViewHolder
        val view: View

        if (convertView == null){
            view = View.inflate(context, R.layout.item_trip_list, null)
            holder = TripViewHolder(view)
            view.tag = holder
            FontUtils.overrideFonts(context,view)
        } else {
            holder = convertView.tag as TripViewHolder
            view = convertView
        }

        trip?.let {
            holder.bind(trip, isLastChild)
        }
        return view
    }

    override fun getChildId(position: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildrenCount(position: Int): Int {
        val date = getGroup(position).date
        val trips = tripsListViewModel.getTripsByDate(date)?.trips
        return if (trips.isNullOrEmpty()){
            0
        } else {
            trips.size
        }
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }
}