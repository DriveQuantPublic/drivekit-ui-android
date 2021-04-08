package com.drivequant.drivekit.common.ui.component.tripslist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.tripslist.DKTripsList
import com.drivequant.drivekit.common.ui.component.tripslist.adapter.TripsListAdapter
import com.drivequant.drivekit.common.ui.component.tripslist.viewModel.DKTripsListViewModel
import kotlinx.android.synthetic.main.dk_trips_list_fragment.*


class DKTripsListFragment : Fragment() {

    lateinit var tripsList: DKTripsList
    private var adapter: TripsListAdapter? = null

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
                adapter = TripsListAdapter(context, DKTripsListViewModel(tripsList.getTripsList()))
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
}