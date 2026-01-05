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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.DKPrimaryButton
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.injectContent
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.databinding.DkFragmentOdometerHistoriesListBinding
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerHistoryDetailActivity
import com.drivequant.drivekit.vehicle.ui.odometer.adapter.OdometerHistoriesListAdapter
import com.drivequant.drivekit.vehicle.ui.odometer.adapter.OdometerHistoriesListener
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerHistoriesViewModel

class OdometerHistoriesListFragment : Fragment(), OdometerHistoriesListener {

    private var vehicleId: String? = null
    private var historyAdapter: OdometerHistoriesListAdapter? = null
    private lateinit var viewModel: OdometerHistoriesViewModel
    private var _binding: DkFragmentOdometerHistoriesListBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

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
        vehicleId?.let {
            outState.putString("vehicleIdTag", it)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DkFragmentOdometerHistoriesListBinding.inflate(inflater, container, false)
        binding.root.setDKStyle(android.R.color.white)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_vehicles_odometer_histories_list), javaClass.simpleName)
        savedInstanceState?.getString("vehicleIdTag")?.let {
            vehicleId = it
        }
        vehicleId?.let { vehicleId ->
            context?.let { context ->
                viewModel = ViewModelProvider(this,
                OdometerHistoriesViewModel.OdometerHistoriesViewModelFactory(vehicleId))[OdometerHistoriesViewModel::class.java]
                initReadingsList(context)
            }
            addOdometerReading(vehicleId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addOdometerReading(vehicleId: String) {
        binding.dkButtonAddReference.injectContent {
            DKPrimaryButton(getString(R.string.dk_vehicle_odometer_add_history)) {
                OdometerHistoryDetailActivity.launchActivity(requireActivity(), vehicleId, -1, this@OdometerHistoriesListFragment)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initReadingsList(context: Context) {
        binding.dkRecyclerViewHistories.layoutManager = LinearLayoutManager(context)
        historyAdapter?.notifyDataSetChanged() ?: run {
            historyAdapter = OdometerHistoriesListAdapter(context, viewModel, this)
        }
        binding.dkRecyclerViewHistories.adapter = historyAdapter
    }

    override fun onHistoryClicked(historyId: Int, context: Context) {
        vehicleId?.let {
            activity?.let { activity ->
                OdometerHistoryDetailActivity.launchActivity(activity, it, historyId, this@OdometerHistoriesListFragment)
            }
        }
    }

    @Suppress("OverrideDeprecatedMigration")
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
