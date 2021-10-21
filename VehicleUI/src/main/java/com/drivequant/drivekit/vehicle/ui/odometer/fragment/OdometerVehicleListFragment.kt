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
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerAction
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerActionItem
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerViewModel
import kotlinx.android.synthetic.main.dk_fragment_odometer_vehicle_list.*


class OdometerVehicleListFragment : Fragment() {

    private lateinit var viewModel: OdometerViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateProgressVisibility(true)
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(OdometerViewModel::class.java)
        }

        viewModel.filterData.observe(this, {
            configureFilter()
            updateProgressVisibility(false)
        })
        initFilter(SynchronizationType.DEFAULT)
        dk_swipe_refresh_odometer.setOnRefreshListener {
            initFilter(SynchronizationType.DEFAULT)
        }

        viewModel.odometerData.observe(this, {
            if (it) {
                updateOdometerData()
            } else {
                Toast.makeText(
                    requireContext(),
                    DKResource.convertToString(
                        requireContext(),
                        "dk_challenge_score_alert_message"
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        //TODO show filter and data in the same time / after odometer data is ready
        viewModel.getFilterItems(requireContext())
        setupPopup()
    }

    private fun setupPopup() {
        image_view_popup.apply {
            setImageDrawable(DKResource.convertToDrawable(requireContext(), "dk_common_dots"))
            setColorFilter(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                val popupMenu = PopupMenu(context, it)
                val itemsList: List<OdometerActionItem> = OdometerAction.values().toList()
                for (i in itemsList.indices) {
                    popupMenu.menu.add(
                        Menu.NONE,
                        i,
                        i,
                        DKSpannable().append(itemsList[i].getTitle(context), context.resSpans {
                            color(DriveKitUI.colors.mainFontColor())
                        }).toSpannable()
                    )
                }
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    viewModel.currentVehicleId?.let { vehicleId ->
                        context?.let { context ->
                            itemsList[menuItem.itemId].onItemClicked(context, vehicleId)
                        }
                    }
                    return@setOnMenuItemClickListener false
                }
            }
        }
    }

    private fun updateOdometerData() {
        text_total_distance_title.text = viewModel.getDistance(requireContext())
        text_total_distance_description.text = viewModel.getDate(requireContext())
        dk_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.dk_fragment_odometer_vehicle_list, container, false)
    }

    companion object {
        fun newInstance(): OdometerVehicleListFragment {
            return OdometerVehicleListFragment()
        }
    }

    private fun initFilter(synchronizationType: SynchronizationType = SynchronizationType.CACHE) {
        filter_view.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                val itemId = viewModel.filterItems[position].getItemId()
                itemId?.let {
                    viewModel.updateVehicleMileageData((it as String), synchronizationType)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun configureFilter() {
        filter_view.setItems(viewModel.filterItems)
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
        } else {
            progress_circular.visibility = View.GONE
        }
    }
}