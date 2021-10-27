package com.drivequant.drivekit.vehicle.ui.odometer.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerHistoryDetailActivity
import com.drivequant.drivekit.vehicle.ui.odometer.adapter.OdometerHistoriesListAdapter
import com.drivequant.drivekit.vehicle.ui.odometer.adapter.OdometerHistoriesListener
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerHistoriesViewModel
import kotlinx.android.synthetic.main.dk_fragment_odometer_histories_list.*


class OdometerHistoriesListFragment : Fragment(), OdometerHistoriesListener {

    private var vehicleId: String? = null
    private var historyAdapter: OdometerHistoriesListAdapter? = null
    private lateinit var viewModel: OdometerHistoriesViewModel

    companion object {
        fun newInstance(vehicleId: String) =
            OdometerHistoriesListFragment().apply {
                arguments = Bundle().apply {
                    putString("vehicleIdTag", vehicleId)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vehicleId = it.getString("vehicleIdTag")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        vehicleId?.let {
            outState.putString("vehicleIdTag", it)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_odometer_histories_list, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getString("vehicleIdTag"))?.let {
            vehicleId = it
        }
        vehicleId?.let { vehicleId ->
            context?.let { context ->
                viewModel = ViewModelProviders.of(this,
                OdometerHistoriesViewModel.OdometerHistoriesViewModelFactory(vehicleId)).get(OdometerHistoriesViewModel::class.java)
                initReadingsList()
                addOdometerReading(context, vehicleId)
            }
        }
    }

    private fun addOdometerReading(context: Context, vehicleId: String) {
        dk_button_add_reference.apply {
            text = DKResource.convertToString(context, "dk_vehicle_odometer_add_reference")
            headLine2(DriveKitUI.colors.fontColorOnSecondaryColor())
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                OdometerHistoryDetailActivity.launchActivity(context, vehicleId, -1)
            }
        }
    }

    private fun initReadingsList() {
        dk_recycler_view_histories.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter?.notifyDataSetChanged() ?: run {
            historyAdapter = OdometerHistoriesListAdapter(requireContext(), viewModel, this)
        }
        dk_recycler_view_histories.adapter = historyAdapter
    }

    override fun onResume() {
        super.onResume()
        historyAdapter?.notifyDataSetChanged()
    }

    override fun onHistoryClicked(historyId: Int) {
        vehicleId?.let {
            context?.let { context ->
                OdometerHistoryDetailActivity.launchActivity(context, it, historyId)
            }
        }
    }
}