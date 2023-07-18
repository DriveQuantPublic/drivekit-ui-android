package com.drivequant.drivekit.vehicle.ui.odometer.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.CustomTypefaceSpan
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerHistoryDetailActivity
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerVehicleDetailActivity
import com.drivequant.drivekit.vehicle.ui.odometer.common.OdometerDrawableListener
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerAction
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerActionItem
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerItemType
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerItemViewModel
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerVehicleListViewModel
import kotlinx.android.synthetic.main.dk_fragment_odometer_vehicle_list.dk_swipe_refresh_odometer
import kotlinx.android.synthetic.main.dk_fragment_odometer_vehicle_list.mileage_vehicle_item
import kotlinx.android.synthetic.main.dk_fragment_odometer_vehicle_list.progress_circular
import kotlinx.android.synthetic.main.dk_fragment_odometer_vehicle_list.root
import kotlinx.android.synthetic.main.dk_fragment_odometer_vehicle_list.text_view_no_vehicle
import kotlinx.android.synthetic.main.dk_fragment_odometer_vehicle_list.vehicle_filter


class OdometerVehicleListFragment : Fragment(), OdometerDrawableListener {

    private lateinit var viewModel: OdometerVehicleListViewModel
    private lateinit var synchronizationType: SynchronizationType
    private var shouldSyncOdometer = true
    private var vehicleId: String? = null

    companion object {
        fun newInstance(vehicleId: String?) : OdometerVehicleListFragment {
            val fragment = OdometerVehicleListFragment()
            fragment.vehicleId = vehicleId
            return fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("vehicleIdTag", vehicleId)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_vehicles_odometer_vehicles_list"
            ), javaClass.simpleName
        )
        savedInstanceState?.getString("vehicleIdTag")?.let {
            vehicleId = it
        }
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this,
                OdometerVehicleListViewModel.OdometerVehicleListViewModelFactory(vehicleId))[OdometerVehicleListViewModel::class.java]
        }
        dk_swipe_refresh_odometer.setOnRefreshListener {
            updateSwipeRefreshOdometerVisibility(true)
            viewModel.selection.value?.let {
                updateOdometer(it, SynchronizationType.DEFAULT)
            }
        }

        context?.let { context ->
            viewModel.getVehicleListItems(context)
            viewModel.vehicleOdometerData.observe(viewLifecycleOwner) {
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
                        context,
                        DKResource.convertToString(
                            context,
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
            }
            viewModel.selection.observe(viewLifecycleOwner) {
                it?.let {
                    val vehicleOdometer =
                        DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", it).queryOne()
                            .executeOne()
                    synchronizationType =
                        if (vehicleOdometer != null) SynchronizationType.CACHE else SynchronizationType.DEFAULT
                    updateOdometer(it, synchronizationType)
                } ?: run {
                    root.visibility = View.GONE
                    text_view_no_vehicle.apply {
                        text = DKResource.convertToString(context, "dk_vehicle_list_empty")
                        normalText()
                        visibility = View.VISIBLE
                    }
                }
            }
        }

        viewModel.filterData.observe(viewLifecycleOwner) { position ->
            vehicle_filter.setItems(viewModel.filterItems, position)
            initVehicleFilter()
        }
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
                l: Long) {
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
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dk_fragment_odometer_vehicle_list, container, false).setDKStyle(Color.WHITE)

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
        dk_swipe_refresh_odometer.isRefreshing = display
        if (!display) {
            dk_swipe_refresh_odometer.visibility = View.VISIBLE
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
                DKSpannable().append(itemsList[i].getTitle(view.context), view.context.resSpans {
                    color(DriveKitUI.colors.mainFontColor())
                    DriveKitUI.primaryFont(view.context)?.let { typeface ->
                        typeface(CustomTypefaceSpan(typeface))
                    }
                }).toSpannable()
            )
        }
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            viewModel.selection.value?.let { vehicleId ->
                activity?.let { activity ->
                    itemsList[menuItem.itemId].onItemClicked(activity, vehicleId, this@OdometerVehicleListFragment)
                }
            }
            return@setOnMenuItemClickListener false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && (requestCode == OdometerHistoryDetailActivity.UPDATE_VEHICLE_ODOMETER_LIST_REQUEST_CODE || requestCode == OdometerVehicleDetailActivity.UPDATE_VEHICLE_ODOMETER_LIST_REQUEST_CODE)) {
            viewModel.selection.value?.let {
                updateOdometer(it, SynchronizationType.CACHE)
            }
        }
    }
}
