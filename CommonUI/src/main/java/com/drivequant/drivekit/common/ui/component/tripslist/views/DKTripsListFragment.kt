package com.drivequant.drivekit.common.ui.component.tripslist.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.tripslist.DKTripsList
import com.drivequant.drivekit.common.ui.component.tripslist.adapter.TripsListAdapter
import com.drivequant.drivekit.common.ui.component.tripslist.viewModel.DKTripsListViewModel
import kotlinx.android.synthetic.main.dk_trips_list_fragment.*
import kotlinx.android.synthetic.main.dk_trips_list_fragment.view.*

class DKTripsListFragment : Fragment() {

    private var adapter: TripsListAdapter? = null
    private lateinit var expandableListView: ExpandableListView
    private lateinit var viewModel: DKTripsListViewModel

    companion object {
        fun newInstance() : DKTripsListFragment = DKTripsListFragment()
    }

    fun configure(tripsList: DKTripsList) {
        if (tripsList.canSwipeToRefresh()) {
            dk_refresh_trips.setOnRefreshListener {
                tripsList.onSwipeToRefresh()
            }
        } else {
            dk_refresh_trips.isEnabled = false
        }
        viewModel.apply {
            setDKTripsList(tripsList)
            sortTrips()
        }
        if (tripsList.getTripsList().isEmpty()) {
            adapter?.notifyDataSetChanged()
        } else {
            adapter?.notifyDataSetChanged() ?: run {
                adapter = TripsListAdapter(context, viewModel)
                expandableListView.setAdapter(adapter)
            }
        }
        expandableListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            adapter?.getChild(groupPosition, childPosition)?.let {
                tripsList.onTripClickListener(it.getItinId())
            }
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = DKTripsListViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_trips_list_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandableListView = view.findViewById(R.id.dk_trips_list)
    }

    fun isFilterPlacerHolder() = adapter != null

    fun updateRefreshTripsVisibility(display: Boolean) {
        if (display) {
            dk_refresh_trips.isRefreshing = display
        } else {
            dk_refresh_trips.visibility = View.VISIBLE
            dk_refresh_trips.isRefreshing = display
        }
    }

    fun setTripsListEmptyView(view: View) {
        expandableListView.emptyView = view
    }
}