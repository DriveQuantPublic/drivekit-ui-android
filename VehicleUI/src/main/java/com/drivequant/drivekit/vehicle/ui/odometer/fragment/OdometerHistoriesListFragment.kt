package com.drivequant.drivekit.vehicle.ui.odometer.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_vehicles_odometer_histories_list"
            ), javaClass.simpleName
        )
        (savedInstanceState?.getString("vehicleIdTag"))?.let {
            vehicleId = it
        }
        vehicleId?.let { vehicleId ->
            context?.let { context ->
                viewModel = ViewModelProviders.of(this,
                OdometerHistoriesViewModel.OdometerHistoriesViewModelFactory(vehicleId)).get(OdometerHistoriesViewModel::class.java)
                initReadingsList(context)
                addOdometerReading(context, vehicleId)
            }
        }
    }

    private fun addOdometerReading(context: Context, vehicleId: String) {
        dk_button_add_reference.apply {
            text = DKResource.convertToString(context, "dk_vehicle_odometer_add_history")
            headLine2(DriveKitUI.colors.fontColorOnSecondaryColor())
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                OdometerHistoryDetailActivity.launchActivity(requireActivity(), vehicleId, -1, this@OdometerHistoriesListFragment)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initReadingsList(context: Context) {
        dk_recycler_view_histories.layoutManager = LinearLayoutManager(context)
        historyAdapter?.notifyDataSetChanged() ?: run {
            historyAdapter = OdometerHistoriesListAdapter(context, viewModel, this)
        }
        dk_recycler_view_histories.adapter = historyAdapter
    }

    override fun onHistoryClicked(historyId: Int, context: Context) {
        vehicleId?.let {
            activity?.let { activity ->
                OdometerHistoryDetailActivity.launchActivity(activity, it, historyId, this@OdometerHistoriesListFragment)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == OdometerHistoryDetailActivity.UPDATE_VEHICLE_HISTORY_LIST_REQUEST_CODE) {
            historyAdapter?.notifyDataSetChanged()
            val intentData = Intent()
            activity?.setResult(AppCompatActivity.RESULT_OK, intentData)
        }
    }
}