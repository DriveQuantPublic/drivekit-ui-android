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
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.databinding.FragmentItemListBinding
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity
import com.drivequant.drivekit.vehicle.ui.picker.adapter.ItemRecyclerViewAdapter
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
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
            fun getAdapterTypeByPickerStep(vehiclePickerStep: VehiclePickerStep): AdapterType {
                return when (vehiclePickerStep) {
                    VehiclePickerStep.TYPE -> TEXT_ITEM_PADDING
                    VehiclePickerStep.TRUCK_TYPE -> TRUCK_TYPE_ITEM
                    VehiclePickerStep.CATEGORY,
                    VehiclePickerStep.DEFAULT_CAR_ENGINE -> TEXT_IMAGE_ITEM
                    VehiclePickerStep.BRANDS_ICONS -> TEXT_OR_IMAGE_ITEM
                    VehiclePickerStep.ENGINE,
                    VehiclePickerStep.YEARS,
                    VehiclePickerStep.MODELS,
                    VehiclePickerStep.VERSIONS,
                    VehiclePickerStep.BRANDS_FULL,
                    VehiclePickerStep.CATEGORY_DESCRIPTION,
                    VehiclePickerStep.NAME -> TEXT_ITEM
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

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    companion object {
        fun newInstance(
            viewModel: VehiclePickerViewModel,
            vehiclePickerStep: VehiclePickerStep,
            items: List<VehiclePickerItem>
        ): VehicleItemListFragment {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        binding.root.setDKStyle()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            vehiclePickerStep = it.getSerializableCompat("vehiclePickerStep", VehiclePickerStep::class.java)!!
        }

        if (vehiclePickerStep == VehiclePickerStep.TYPE) {
            binding.textViewDescription.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        }

        adapterType = AdapterType.getAdapterTypeByPickerStep(vehiclePickerStep)

        binding.itemsRecyclerView.layoutManager = when (adapterType) {
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
            binding.textViewDescription.text = it
            binding.textViewDescription.visibility = View.VISIBLE
        } ?: run {
            binding.textViewDescription.visibility = View.GONE
        }
        binding.textViewDescription.bigText()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        fetchItems()
        if (activity is VehiclePickerActivity) {
            (activity as VehiclePickerActivity).updateTitle(getString(R.string.dk_vehicle_my_vehicle))
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
            binding.itemsRecyclerView.adapter = adapter
            binding.itemsRecyclerView.adapter?.notifyDataSetChanged()
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
