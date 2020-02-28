package com.drivequant.drivekit.vehicle.ui.picker.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.CategoryType
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehicleCategoryItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehiclePickerViewModel

class VehicleCategoryDescriptionFragment : Fragment() {

    private lateinit var viewModel: VehiclePickerViewModel
    private lateinit var viewConfig: VehiclePickerViewConfig
    private lateinit var vehiclePickerCategoryItem: VehicleCategoryItem

    companion object {
        fun newInstance(
            viewModel: VehiclePickerViewModel,
            vehiclePickerViewConfig: VehiclePickerViewConfig)
                : VehicleCategoryDescriptionFragment {
            val fragment = VehicleCategoryDescriptionFragment()
            fragment.viewModel = viewModel
            fragment.vehiclePickerCategoryItem = viewModel.selectedCategory
            fragment.viewConfig = vehiclePickerViewConfig
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vehicle_category_description, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageViewCategory = view.findViewById(R.id.image_view_icon2) as ImageView
        val textViewDescription = view.findViewById(R.id.text_view_description) as TextView
        val buttonValidate = view.findViewById(R.id.button_validate) as Button
        val textViewBrands = view.findViewById(R.id.text_view_brands) as TextView

        imageViewCategory.setImageDrawable(vehiclePickerCategoryItem.icon2)
        textViewDescription.text = vehiclePickerCategoryItem.description

        buttonValidate.setOnClickListener {
            viewModel.computeNextScreen(requireContext(), VehiclePickerStep.CATEGORY_DESCRIPTION, viewConfig)
        }

        if (viewConfig.categoryTypes != CategoryType.LITE_CONFIG_ONLY) {
            textViewBrands.visibility = View.VISIBLE
            textViewBrands.setOnClickListener {
                viewModel.computeNextScreen(requireContext(), VehiclePickerStep.CATEGORY_DESCRIPTION, viewConfig, otherAction = true)
            }
        } else {
            textViewBrands.visibility = View.GONE
        }
    }
}