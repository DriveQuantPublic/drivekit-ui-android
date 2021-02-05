package com.drivequant.drivekit.vehicle.ui.picker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleCategoryItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.CategoryConfigType
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehiclePickerViewModel

class VehicleCategoryDescriptionFragment : Fragment() {

    private lateinit var viewModel: VehiclePickerViewModel
    private lateinit var vehiclePickerCategoryItem: VehicleCategoryItem

    companion object {
        fun newInstance(viewModel: VehiclePickerViewModel)
                : VehicleCategoryDescriptionFragment {
            val fragment = VehicleCategoryDescriptionFragment()
            fragment.viewModel = viewModel
            fragment.vehiclePickerCategoryItem = viewModel.selectedCategory
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_vehicle_category_description, container,false).setDKStyle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageViewCategory = view.findViewById(R.id.image_view_icon2) as ImageView
        val textViewDescription = view.findViewById(R.id.text_view_description) as TextView
        val buttonValidate = view.findViewById(R.id.button_validate) as Button
        val textViewBrands = view.findViewById(R.id.text_view_brands) as TextView

        buttonValidate.button()
        buttonValidate.text = DKResource.convertToString(requireContext(), "dk_common_validate")
        buttonValidate.setOnClickListener {
            viewModel.computeNextScreen(requireContext(), VehiclePickerStep.CATEGORY_DESCRIPTION)
        }

        textViewBrands.headLine2(DriveKitUI.colors.secondaryColor())
        textViewBrands.text = DKResource.convertToString(requireContext(), "dk_vehicle_category_display_brands")

        textViewDescription.normalText()
        textViewDescription.text = vehiclePickerCategoryItem.description

        imageViewCategory.setImageDrawable(vehiclePickerCategoryItem.icon2)

        if (DriveKitVehicleUI.categoryConfigType != CategoryConfigType.LITE_CONFIG_ONLY) {
            textViewBrands.visibility = View.VISIBLE
            textViewBrands.setOnClickListener {
                viewModel.computeNextScreen(requireContext(), VehiclePickerStep.CATEGORY_DESCRIPTION, otherAction = true)
            }
        } else {
            textViewBrands.visibility = View.GONE
        }
    }
}