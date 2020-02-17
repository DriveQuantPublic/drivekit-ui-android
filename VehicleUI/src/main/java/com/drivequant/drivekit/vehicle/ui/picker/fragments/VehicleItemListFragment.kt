package com.drivequant.drivekit.vehicle.ui.picker.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.adapter.ItemRecyclerViewAdapter
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehiclePickerViewModel
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehiclePickerViewModelFactory

class VehicleItemListFragment : Fragment() {

    enum class AdapterType(val value: Int) {
        TEXT_ITEM(0),
        TEXT_IMAGE_ITEM(1);

        companion object {
            fun getEnum(value: Int): AdapterType {
                for (x in values()) {
                    if (x.value == value) return x
                }
                return TEXT_ITEM
            }
        }
    }

    private lateinit var viewModel : VehiclePickerViewModel
    private lateinit var vehiclePickerViewConfig: VehiclePickerViewConfig
    private lateinit var vehiclePickerStep: VehiclePickerStep

    private var adapterType = 0
    private lateinit var recyclerView: RecyclerView
    private var adapter: Any? = null
    private var items: List<VehiclePickerItem> = listOf()


    companion object {
        fun newInstance(vehiclePickerStep: VehiclePickerStep,
                        vehiclePickerViewConfig: VehiclePickerViewConfig)
                : VehicleItemListFragment {
            val fragment = VehicleItemListFragment()
            fragment.vehiclePickerStep = vehiclePickerStep
            fragment.vehiclePickerViewConfig = vehiclePickerViewConfig
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // TODO test

        val view = inflater.inflate(R.layout.fragment_item_list, container,false)
        recyclerView = view?.findViewById(R.id.list) as RecyclerView
        if (adapterType == AdapterType.TEXT_ITEM.value){
            recyclerView.layoutManager = LinearLayoutManager(context)
        } else {
            recyclerView.layoutManager = GridLayoutManager(context, 2)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO handle lifecycle

        viewModel = ViewModelProviders.of(this,
            VehiclePickerViewModelFactory()).get(VehiclePickerViewModel::class.java)

        val items = viewModel.fetchDataByStep(requireContext(), vehiclePickerStep, vehiclePickerViewConfig)
        recyclerView.adapter = ItemRecyclerViewAdapter(items)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // TODO handle lifecycle
    }
}