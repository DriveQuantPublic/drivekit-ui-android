package com.drivequant.drivekit.vehicle.ui.picker.fragments

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
import com.drivequant.drivekit.vehicle.ui.picker.adapter.ItemRecyclerViewAdapter
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.*
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehiclePickerViewModel

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

    private lateinit var viewModel: VehiclePickerViewModel
    private lateinit var vehiclePickerViewConfig: VehiclePickerViewConfig
    private lateinit var vehiclePickerStep: VehiclePickerStep
    private lateinit var items: List<VehiclePickerItem>

    private var listener: OnListFragmentInteractionListener? = null
    private var adapterType = 0
    private lateinit var recyclerView: RecyclerView
    private var adapter: ItemRecyclerViewAdapter? = null

    companion object {
        fun newInstance(
            viewModel: VehiclePickerViewModel,
            vehiclePickerStep: VehiclePickerStep,
            items: List<VehiclePickerItem>,
            vehiclePickerViewConfig: VehiclePickerViewConfig)
                : VehicleItemListFragment {
            val fragment = VehicleItemListFragment()
            fragment.viewModel = viewModel
            fragment.vehiclePickerStep = vehiclePickerStep
            fragment.items = items
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

    override fun onResume() {
        super.onResume()
        fetchItems()
    }

    private fun fetchItems() {
        if (adapter != null){
            adapter?.notifyDataSetChanged()
        } else {
            adapter = ItemRecyclerViewAdapter(vehiclePickerStep, items, listener)
            recyclerView.adapter = adapter
            recyclerView.adapter?.notifyDataSetChanged()
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

    override fun onPause() {
        super.onPause()
        listener = null
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
        fun onSelectedItem(currentPickerStep: VehiclePickerStep, item: VehiclePickerItem)
    }
}