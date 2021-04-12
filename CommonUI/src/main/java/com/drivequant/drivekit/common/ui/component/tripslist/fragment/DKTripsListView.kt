package com.drivequant.drivekit.common.ui.component.tripslist.fragment

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.FrameLayout
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.tripslist.DKTripsList
import com.drivequant.drivekit.common.ui.component.tripslist.adapter.TripsListAdapter
import com.drivequant.drivekit.common.ui.component.tripslist.viewModel.DKTripsListViewModel
import kotlinx.android.synthetic.main.dk_trips_list_fragment.view.*


class DKTripsListView : FrameLayout {

    private var adapter: TripsListAdapter? = null
    private lateinit var view: View
    private lateinit var expandableListView: ExpandableListView

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
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun configure(tripsList: DKTripsList) {
        if (tripsList.canSwipeToRefresh()) {
            view.dk_refresh_trips.setOnRefreshListener {
                tripsList.onSwipeToRefresh()
            }
        } else {
            view.dk_refresh_trips.isEnabled = false
        }

        if (tripsList.getTripsList().isEmpty()) {
            adapter?.notifyDataSetChanged()
        } else {
            adapter?.notifyDataSetChanged() ?: run {
                adapter = TripsListAdapter(context, DKTripsListViewModel(tripsList))
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