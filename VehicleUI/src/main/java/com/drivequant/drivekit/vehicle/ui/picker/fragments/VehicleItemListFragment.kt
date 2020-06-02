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
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity
import com.drivequant.drivekit.vehicle.ui.picker.adapter.ItemRecyclerViewAdapter
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.*
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehiclePickerViewModel

class VehicleItemListFragment : Fragment() {

    enum class AdapterType(val value: Int) {
        TEXT_ITEM(0),
        TEXT_ITEM_PADDING(1),
        TEXT_IMAGE_ITEM(2),
        TEXT_OR_IMAGE_ITEM(3),
        TRUCK_TYPE_ITEM(4);

        companion object {
            fun getAdapterTypeByPickerStep(vehiclePickerStep: VehiclePickerStep) : AdapterType {
                return when (vehiclePickerStep) {
                    TYPE -> TEXT_ITEM_PADDING
                    TRUCK_TYPE -> TRUCK_TYPE_ITEM
                    CATEGORY -> TEXT_IMAGE_ITEM
                    BRANDS_ICONS -> TEXT_OR_IMAGE_ITEM
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
    private lateinit var vehiclePickerStep: VehiclePickerStep
    private lateinit var items: List<VehiclePickerItem>

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var adapterType: AdapterType
    private lateinit var textViewDescription: TextView
    private lateinit var recyclerView: RecyclerView
    private var adapter: ItemRecyclerViewAdapter? = null

    companion object {
        fun newInstance(
            viewModel: VehiclePickerViewModel,
            vehiclePickerStep: VehiclePickerStep,
            items: List<VehiclePickerItem>)
                : VehicleItemListFragment {
            val fragment = VehicleItemListFragment()
            fragment.viewModel = viewModel
            fragment.vehiclePickerStep = vehiclePickerStep
            fragment.items = items
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container,false)
        textViewDescription = view.findViewById(R.id.text_view_description) as TextView
        textViewDescription.bigText()
        recyclerView = view.findViewById(R.id.list) as RecyclerView
        adapterType = AdapterType.getAdapterTypeByPickerStep(vehiclePickerStep)

        recyclerView.layoutManager = when (adapterType){
            AdapterType.TEXT_ITEM,
            AdapterType.TEXT_ITEM_PADDING -> {
                LinearLayoutManager(context)
            }
            AdapterType.TRUCK_TYPE_ITEM -> {
                GridLayoutManager(context, 1)
            }
            AdapterType.TEXT_IMAGE_ITEM,
            AdapterType.TEXT_OR_IMAGE_ITEM -> {
                GridLayoutManager(context, 2)
            }
        }
        return view.setDKStyle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val descriptionText = viewModel.getDescription(view.context, vehiclePickerStep)
        descriptionText?.let {
            textViewDescription.text = it
            textViewDescription.visibility = View.VISIBLE
        }?:run {
            textViewDescription.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        fetchItems()

        if (activity is VehiclePickerActivity){
            (activity as VehiclePickerActivity).updateTitle(DKResource.convertToString(requireContext(), "dk_vehicle_my_vehicle"))
        }
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

    interface OnListFragmentInteractionListener {
        fun onSelectedItem(currentPickerStep: VehiclePickerStep, item: VehiclePickerItem)
    }
}