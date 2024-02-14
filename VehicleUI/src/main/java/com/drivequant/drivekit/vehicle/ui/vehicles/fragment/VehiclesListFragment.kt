package com.drivequant.drivekit.vehicle.ui.vehicles.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.databinding.FragmentVehiclesListBinding
import com.drivequant.drivekit.vehicle.ui.databinding.HeaderVehicleListBinding
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity
import com.drivequant.drivekit.vehicle.ui.vehicles.activity.VehiclesListActivity
import com.drivequant.drivekit.vehicle.ui.vehicles.adapter.VehiclesListAdapter
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehiclesListViewModel

class VehiclesListFragment : Fragment() {

    private lateinit var viewModel: VehiclesListViewModel
    private var adapter: VehiclesListAdapter? = null
    private var shouldSyncVehicles = true
    private lateinit var synchronizationType: SynchronizationType
    private var _binding: FragmentVehiclesListBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVehiclesListBinding.inflate(inflater, container, false)
        binding.root.setDKStyle()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_vehicles_list), javaClass.simpleName)

        binding.refreshVehicles.setOnRefreshListener {
            updateVehicles(SynchronizationType.DEFAULT)
        }

        viewModel = ViewModelProvider(this)[VehiclesListViewModel::class.java]
        viewModel.apply {
            progressBarObserver.observe(viewLifecycleOwner) {
                it?.let { displayProgressCircular ->
                    if (displayProgressCircular) {
                        showProgressCircular()
                    } else {
                        hideProgressCircular()
                    }
                }
            }

            removeBeaconOrBluetoothObserver.observe(viewLifecycleOwner) {
                viewModel.fetchVehicles(requireContext(), SynchronizationType.CACHE)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        synchronizationType = if (viewModel.hasLocalVehicles()) {
            SynchronizationType.CACHE
        } else {
            SynchronizationType.DEFAULT
        }
        updateVehicles(synchronizationType)
    }

    private fun updateTitle(title: String) {
        if (activity is VehiclesListActivity) {
            (activity as VehiclesListActivity).updateTitle(title)
        }
    }

    fun updateVehicles(synchronizationType: SynchronizationType) {
        adapter?.setTouched(false)
        viewModel.vehiclesData.observe(viewLifecycleOwner) {
            if (viewModel.syncStatus == VehicleSyncStatus.FAILED_TO_SYNC_VEHICLES_CACHE_ONLY) {
                Toast.makeText(
                    context,
                    R.string.dk_vehicle_error_message,
                    Toast.LENGTH_LONG
                ).show()
            }
            if (it.isNullOrEmpty()) {
                getVehicleListHeaderBinding().linearLayoutHeaderVehicleList.visibility = View.VISIBLE
            } else {
                binding.vehiclesList.layoutManager = LinearLayoutManager(context)
                displayVehiclesList()
                adapter?.let { adapter ->
                    adapter.setVehicles(it)
                    adapter.notifyDataSetChanged()
                } ?: run {
                    adapter = VehiclesListAdapter(requireContext(), viewModel, it.toMutableList())
                    binding.vehiclesList.adapter = adapter
                }
            }
            binding.refreshVehicles.apply {
                visibility = View.VISIBLE
                isRefreshing = false
            }
            setupAddReplaceButton()
            updateTitle(viewModel.getScreenTitle(requireContext()))
            if (synchronizationType == SynchronizationType.CACHE && shouldSyncVehicles) {
                shouldSyncVehicles = false
                updateVehicles(SynchronizationType.DEFAULT)
            }
        }
        binding.refreshVehicles.isRefreshing = true
        viewModel.fetchVehicles(requireContext(), synchronizationType = synchronizationType)
    }

    private fun setupAddReplaceButton() {
        if (viewModel.shouldDisplayAddReplaceButton()) {
            binding.buttonVehicle.apply {
                visibility = View.VISIBLE
                setText(viewModel.getAddReplaceButtonTextResId())
                setOnClickListener {
                    if (viewModel.shouldReplaceVehicle()) {
                        VehiclePickerActivity.launchActivity(context, vehicleToDelete = viewModel.vehiclesList.first())
                    } else {
                        VehiclePickerActivity.launchActivity(requireContext())
                    }
                }
            }
        } else {
            binding.buttonVehicle.visibility = View.GONE
        }
    }

    private fun displayVehiclesList() {
        if (viewModel.vehiclesList.isNotEmpty()) {
            getVehicleListHeaderBinding().linearLayoutHeaderVehicleList.visibility = View.GONE
            binding.vehiclesList.visibility = View.VISIBLE
        } else {
            setupEmptyListLayout()
            getVehicleListHeaderBinding().linearLayoutHeaderVehicleList.visibility = View.VISIBLE
            binding.vehiclesList.visibility = View.GONE
        }
        hideProgressCircular()
    }

    private fun setupEmptyListLayout() {
        getVehicleListHeaderBinding().textViewSummaryIcon.setImageResource(com.drivequant.drivekit.common.ui.R.drawable.dk_common_warning)
        getVehicleListHeaderBinding().textViewHeaderTitle.apply {
            typeface = Typeface.DEFAULT_BOLD
            setText(R.string.dk_vehicle_list_empty)
        }
    }

    private fun hideProgressCircular() {
        binding.root.findViewById<ProgressBar>(com.drivequant.drivekit.common.ui.R.id.dk_progress_circular).apply {
            animate()
            .alpha(0f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                }
            })
        }
    }

    private fun showProgressCircular() {
        binding.root.findViewById<ProgressBar>(com.drivequant.drivekit.common.ui.R.id.dk_progress_circular).apply {
            animate()
                .alpha(1f)
                .setDuration(200L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.VISIBLE
                    }
                })
        }
    }

    @SuppressWarnings("kotlin:S6531")
    private fun getVehicleListHeaderBinding(): HeaderVehicleListBinding {
        @Suppress("USELESS_CAST")
        return binding.vehicleListHeader as HeaderVehicleListBinding // DO NOT REMOVE THIS "USELESS CAST". It's actually necessary for compilation.
    }
}
