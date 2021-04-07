package com.drivequant.drivekit.common.ui.component.tripslist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.tripslist.DKTripsList
import kotlinx.android.synthetic.main.dk_trips_list_fragment.*


class DKTripsListFragment : Fragment() {

    lateinit var tripsList: DKTripsList

    companion object {
        @JvmStatic
        fun newInstance(tripsList: DKTripsList): DKTripsListFragment {
            val fragment = DKTripsListFragment()
            fragment.tripsList = tripsList
            return fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("tripsList", tripsList)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getSerializable("tripsList") as DKTripsList?)?.let {
            tripsList = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_trips_list_fragment, container, false)


}