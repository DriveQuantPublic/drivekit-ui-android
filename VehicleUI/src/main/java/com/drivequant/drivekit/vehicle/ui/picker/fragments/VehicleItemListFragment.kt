package com.drivequant.drivekit.vehicle.ui.picker.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity
import com.drivequant.drivekit.vehicle.ui.picker.adapter.ItemRecyclerViewAdapter
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.BRANDS_FULL
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.BRANDS_ICONS
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.CATEGORY
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.CATEGORY_DESCRIPTION
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.ENGINE
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.MODELS
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.NAME
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.TRUCK_TYPE
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.TYPE
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.VERSIONS
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.YEARS
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehiclePickerViewModel
import kotlinx.android.synthetic.main.fragment_item_list.items_recycler_view
import kotlinx.android.synthetic.main.fragment_item_list.text_view_description

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

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::vehiclePickerStep.isInitialized) {
            outState.putSerializable("vehiclePickerStep", vehiclePickerStep)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            vehiclePickerStep = it.getSerializableCompat("vehiclePickerStep", VehiclePickerStep::class.java)!!
        }

        if (vehiclePickerStep == TYPE) {
            text_view_description.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        }

        adapterType = AdapterType.getAdapterTypeByPickerStep(vehiclePickerStep)

        items_recycler_view.layoutManager = when (adapterType) {
            AdapterType.TEXT_ITEM,
            AdapterType.TEXT_ITEM_PADDING -> LinearLayoutManager(context)
            AdapterType.TRUCK_TYPE_ITEM -> GridLayoutManager(context, 1)
            AdapterType.TEXT_IMAGE_ITEM, AdapterType.TEXT_OR_IMAGE_ITEM -> GridLayoutManager(context, 2)
        }

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this, VehiclePickerViewModel.VehiclePickerViewModelFactory())[VehiclePickerViewModel::class.java]
        }
        val descriptionText = viewModel.getDescription(requireContext(), vehiclePickerStep)
        descriptionText?.let {
            text_view_description.text = it
            text_view_description.visibility = View.VISIBLE
        }?:run {
            text_view_description.visibility = View.GONE
        }
        text_view_description.bigText()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_item_list, container,false).setDKStyle()

    override fun onResume() {
        super.onResume()
        fetchItems()
        if (activity is VehiclePickerActivity){
            (activity as VehiclePickerActivity).updateTitle(DKResource.convertToString(requireContext(), "dk_vehicle_my_vehicle"))
        }
    }

    private fun fetchItems() {
        if (adapter != null) {
            adapter?.notifyDataSetChanged()
        } else {
            if (!this::items.isInitialized) {
                items = viewModel.getItemsByStep(vehiclePickerStep)
            }
            adapter = ItemRecyclerViewAdapter(vehiclePickerStep, items, listener)
            items_recycler_view.adapter = adapter
            items_recycler_view.adapter?.notifyDataSetChanged()
        }
    }

    override fun onAttach(context: Context) {
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
