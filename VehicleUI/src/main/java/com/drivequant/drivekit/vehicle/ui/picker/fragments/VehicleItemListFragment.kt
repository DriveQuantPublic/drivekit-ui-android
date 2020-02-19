package com.drivequant.drivekit.vehicle.ui.picker.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity
import com.drivequant.drivekit.vehicle.ui.picker.adapter.ItemRecyclerViewAdapter
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.*
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

            fun getAdapterTypeByPickerStep(vehiclePickerStep: VehiclePickerStep) : AdapterType {
                return when (vehiclePickerStep) {
                    CATEGORY,
                    BRANDS_ICONS -> TEXT_IMAGE_ITEM

                    TYPE,
                    ENGINE,
                    YEARS,
                    MODELS,
                    VERSIONS,
                    BRANDS_FULL,
                    CATEGORY_DESCRIPTION,
                    NAME -> {
                        TEXT_ITEM
                    }
                }
            }
        }
    }

    private lateinit var viewModel : VehiclePickerViewModel
    private lateinit var vehiclePickerViewConfig: VehiclePickerViewConfig
    private lateinit var vehiclePickerStep: VehiclePickerStep

    private var listener: OnListFragmentInteractionListener? = null
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
        val view = inflater.inflate(R.layout.fragment_item_list, container,false)
        recyclerView = view?.findViewById(R.id.list) as RecyclerView
        adapterType = AdapterType.getAdapterTypeByPickerStep(vehiclePickerStep).value
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
        if (listener != null) {
            recyclerView.adapter = ItemRecyclerViewAdapter(vehiclePickerStep, fetchItems(), listener)
        }
    }

    private fun fetchItems(): List<VehiclePickerItem> { // TODO move in VM
        return when (vehiclePickerStep){
            TYPE -> {
                viewModel.fetchVehicleTypes(requireContext(), vehiclePickerViewConfig)
            }
            CATEGORY -> {
                viewModel.fetchVehicleCategories(requireContext(), (activity as VehiclePickerActivity).vehicleType)
            }
            CATEGORY_DESCRIPTION -> listOf()
            BRANDS_ICONS -> {
                viewModel.fetchVehicleBrands(requireContext(), (activity as VehiclePickerActivity).vehicleType, withIcons = true)
            }
            BRANDS_FULL -> {
                viewModel.fetchVehicleBrands(requireContext(), (activity as VehiclePickerActivity).vehicleType)
            }
            ENGINE -> {
                viewModel.fetchVehicleEngines(requireContext(), (activity as VehiclePickerActivity).vehicleType)
            }
            MODELS -> listOf()
            YEARS -> listOf()
            VERSIONS -> listOf()
            NAME -> listOf()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            // TODO
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // TODO handle lifecycle
    }

    interface OnListFragmentInteractionListener {
        fun onSelectedItem(vehiclePickerStep: VehiclePickerStep, item: VehiclePickerItem )
    }
}