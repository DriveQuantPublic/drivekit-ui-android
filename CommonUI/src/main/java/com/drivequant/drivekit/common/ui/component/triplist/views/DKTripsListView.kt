package com.drivequant.drivekit.common.ui.component.triplist.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.FrameLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.triplist.DKTripList
import com.drivequant.drivekit.common.ui.component.triplist.adapter.TripListAdapter
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.DKTripListViewModel


class DKTripListView : FrameLayout {

    private var adapter: TripListAdapter? = null
    private lateinit var view: View
    private lateinit var expandableListView: ExpandableListView
    private lateinit var swipeToRefreshTrips: SwipeRefreshLayout
    private lateinit var viewModel: DKTripListViewModel

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        view = View.inflate(context, R.layout.dk_trips_list_fragment, null)
        expandableListView = view.findViewById(R.id.dk_trips_list)
        swipeToRefreshTrips = view.findViewById(R.id.dk_swipe_refresh_trips)
        viewModel = DKTripListViewModel()
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun configure(tripsList: DKTripList) {
        updateSwipeRefreshTripsVisibility(false)
        if (tripsList.canSwipeToRefresh()) {
            swipeToRefreshTrips.setOnRefreshListener {
                tripsList.onSwipeToRefresh()
            }
        } else {
            swipeToRefreshTrips.isEnabled = false
        }
        viewModel.apply {
            setDKTripList(tripsList)
            sortTrips()
        }
        if (tripsList.getTripsList().isEmpty()) {
            adapter?.notifyDataSetChanged()
        } else {
            adapter?.notifyDataSetChanged() ?: run {
                adapter = TripListAdapter(context, viewModel)
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
    fun updateSwipeRefreshTripsVisibility(display: Boolean) {
        if (!display) {
            swipeToRefreshTrips.visibility = View.VISIBLE
        }
        swipeToRefreshTrips.isRefreshing = display
    }

    fun setTripsListEmptyView(view: View) {
        expandableListView.emptyView = view
    }
}
