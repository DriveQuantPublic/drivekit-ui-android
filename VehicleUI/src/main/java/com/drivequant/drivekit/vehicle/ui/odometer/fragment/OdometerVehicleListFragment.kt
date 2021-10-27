package com.drivequant.drivekit.vehicle.ui.odometer.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.common.OdometerDrawableListener
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.*
import kotlinx.android.synthetic.main.dk_fragment_odometer_vehicle_list.*


class OdometerVehicleListFragment : Fragment(), OdometerDrawableListener {

    private lateinit var viewModel: OdometerVehicleListViewModel
    private lateinit var synchronizationType: SynchronizationType
    private var shouldSyncOdometer = true

    companion object {
        fun newInstance() = OdometerVehicleListFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(OdometerVehicleListViewModel::class.java)
        }

        viewModel.filterData.observe(this, {
            vehicle_filter.setItems(viewModel.filterItems)
            initVehicleFilter()
        })

        dk_swipe_refresh_odometer.setOnRefreshListener {
            updateSwipeRefreshOdometerVisibility(true)
            viewModel.selection.value?.let {
                updateOdometer(it, SynchronizationType.DEFAULT)
            }
        }

        viewModel.vehicleOdometerData.observe(this, {
            if (it) {
                viewModel.selection.value?.let { vehicleId ->
                    val viewModel = OdometerItemViewModel(vehicleId)
                    mileage_vehicle_item.configureOdometerItem(
                        viewModel,
                        OdometerItemType.ODOMETER,
                        this@OdometerVehicleListFragment
                    )
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    DKResource.convertToString(
                        requireContext(),
                        "dk_vehicle_odometer_failed_to_sync"
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
            updateProgressVisibility(false)
            updateSwipeRefreshOdometerVisibility(false)

            if (synchronizationType == SynchronizationType.CACHE && shouldSyncOdometer) {
                shouldSyncOdometer = false
                viewModel.selection.value?.let { vehicleId ->
                    updateOdometer(vehicleId, SynchronizationType.DEFAULT)
                }
            }
        })

        viewModel.getVehicleListItems(requireContext())
        viewModel.selection.observe(this, {
            val vehicleOdometer = DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", it).queryOne().executeOne()
            synchronizationType = if (vehicleOdometer != null) SynchronizationType.CACHE else SynchronizationType.DEFAULT
            updateOdometer(it, synchronizationType)
        })
    }

    private fun updateOdometer(vehicleId: String, synchronizationType: SynchronizationType) {
        updateProgressVisibility(true)
        viewModel.getOdometer(vehicleId, synchronizationType)
    }

    private fun initVehicleFilter() {
        vehicle_filter.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                val itemId = viewModel.filterItems[position].getItemId()
                itemId?.let {
                    viewModel.selection.postValue(it as String)
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View = inflater.inflate(R.layout.dk_fragment_odometer_vehicle_list, container, false)

    private fun updateProgressVisibility(displayProgress: Boolean) {
        progress_circular?.apply {
            visibility = if (displayProgress) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun updateSwipeRefreshOdometerVisibility(display: Boolean) {
        if (display) {
            dk_swipe_refresh_odometer.isRefreshing = display
        } else {
            dk_swipe_refresh_odometer.visibility = View.VISIBLE
            dk_swipe_refresh_odometer.isRefreshing = display
        }
    }

    override fun onDrawableClicked(view: View, odometerItemType: OdometerItemType) {
        val popupMenu = PopupMenu(context, view)
        val itemsList: List<OdometerActionItem> = OdometerAction.values().toList()
        for (i in itemsList.indices) {
            popupMenu.menu.add(
                Menu.NONE,
                i,
                i,
                DKSpannable().append(itemsList[i].getTitle(requireContext()), requireContext().resSpans {
                    color(DriveKitUI.colors.mainFontColor())
                }).toSpannable()
            )
        }
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            viewModel.selection.value?.let { vehicleId ->
                context?.let { context ->
                    itemsList[menuItem.itemId].onItemClicked(context, vehicleId)
                }
            }
            return@setOnMenuItemClickListener false
        }
    }
}