package com.drivequant.drivekit.common.ui.component.tripslist.fragment

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.FrameLayout
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.tripslist.DKRefreshTrips
import com.drivequant.drivekit.common.ui.component.tripslist.DKTripsList
import com.drivequant.drivekit.common.ui.component.tripslist.adapter.TripsListAdapter
import com.drivequant.drivekit.common.ui.component.tripslist.viewModel.DKTripsListViewModel
import kotlinx.android.synthetic.main.dk_trips_list_fragment.view.*


class DKTripsListView : FrameLayout {

    private var refresh: DKRefreshTrips? = null
    private lateinit var view: View
    lateinit var expandableListView: ExpandableListView
    var adapter: TripsListAdapter? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr) {
        init()
    }

    fun configure(tripsList: DKTripsList, refresh: DKRefreshTrips?) {
        this.refresh = refresh
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

    private fun init() {
        view = View.inflate(context, R.layout.dk_trips_list_fragment, null)
        expandableListView = view.findViewById(R.id.dk_trips_list)
        view.dk_refresh_trips.setOnRefreshListener {
            refresh?.onRefreshTrips()
        }
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun updateRefreshTripsVisibility(display: Boolean) {
        if (display) {
            dk_refresh_trips.isRefreshing = display
        } else {
            dk_refresh_trips.visibility = View.VISIBLE
            dk_refresh_trips.isRefreshing = display
        }
    }
/*
    companion object {
        fun newInstance(tripsList: DKTripsList): DKTripsListFragment {
            val fragment = DKTripsListFragment()
            fragment.tripsList = tripsList
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_trips_list_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (tripsList.getTripsList().isEmpty()) {
            adapter?.notifyDataSetChanged()
        } else {
            adapter?.notifyDataSetChanged() ?: run {
                adapter = TripsListAdapter(context, DKTripsListViewModel(tripsList))
                dk_trips_list.setAdapter(adapter)
            }
        }
        dk_trips_list.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            adapter?.getChild(groupPosition, childPosition)?.let {
              tripsList.onTripClickListener(it.getItinId())
            }
            false
        }
    }
 */

}