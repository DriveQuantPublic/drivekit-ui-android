package com.drivequant.drivekit.vehicle.ui.beacon.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.drivequant.beaconutils.BeaconInfo
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.adapter.BeaconDetailAdapter
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconDetailViewModel
import kotlinx.android.synthetic.main.fragment_beacon_detail.*

class BeaconDetailFragment : Fragment() {
    companion object {
        fun newInstance(viewModel: BeaconDetailViewModel, vehicle: Vehicle, batteryLevel: Int, beaconInfo: BeaconInfo) : BeaconDetailFragment {
            val fragment = BeaconDetailFragment()
            fragment.viewModel = viewModel
            fragment.vehicle = vehicle
            fragment.batteryLevel = batteryLevel
            fragment.beaconInfo = beaconInfo
            return fragment
        }
    }

    private lateinit var vehicle: Vehicle
    private var batteryLevel: Int = 0
    private lateinit var beaconInfo: BeaconInfo
    private lateinit var viewModel: BeaconDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_beacon_detail, container, false).setDKStyle()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.buildListData(requireContext())
        val layoutManager = LinearLayoutManager(requireContext())
        recycler_view_container.layoutManager = layoutManager
        recycler_view_container.adapter = BeaconDetailAdapter(requireContext(), viewModel)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.beacon_detail_menu_bar, menu) // TODO : email icon in common ?
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_email){
            // TODO send listener
        }

        return super.onOptionsItemSelected(item)
    }
}