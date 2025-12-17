package com.drivequant.drivekit.vehicle.ui.picker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.component.DKPrimaryButton
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleCategoryItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.CategoryConfigType
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehiclePickerViewModel

class VehicleCategoryDescriptionFragment : Fragment() {

    private lateinit var viewModel: VehiclePickerViewModel
    private lateinit var vehiclePickerCategoryItem: VehicleCategoryItem

    private val isLiteConfigOnly = DriveKitVehicleUI.categoryConfigType == CategoryConfigType.LITE_CONFIG_ONLY

    companion object {
        fun newInstance(viewModel: VehiclePickerViewModel): VehicleCategoryDescriptionFragment {
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
    ): View = if (isLiteConfigOnly) {
        R.layout.fragment_vehicle_category_description_lite_config
    } else {
        R.layout.fragment_vehicle_category_description
    }.let {
        inflater.inflate(it, container, false).setDKStyle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageViewCategory: ImageView = view.findViewById(R.id.image_view_icon)
        val textViewDescription: TextView = view.findViewById(R.id.text_view_description)
        val validateButton: ComposeView? = view.findViewById(R.id.button_validate)
        val buttonFindMyVehicle: View? = view.findViewById(R.id.button_find_my_vehicle)
        val buttonDontHaveTime: View? = view.findViewById(R.id.button_dont_have_time)

        validateButton?.setContent {
            DKPrimaryButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_validate)) {
                viewModel.computeNextScreen(requireContext(), VehiclePickerStep.CATEGORY_DESCRIPTION)
            }
        }

        buttonFindMyVehicle?.let {
            customizeButton(it, R.string.dk_vehicle_detail_category_button_title, R.string.dk_vehicle_detail_category_button_description)
            it.setOnClickListener {
                viewModel.computeNextScreen(
                    requireContext(),
                    VehiclePickerStep.CATEGORY_DESCRIPTION,
                    otherAction = true
                )
            }
        }
        buttonDontHaveTime?.let {
            customizeButton(it, R.string.dk_vehicle_quick_category_button_title, R.string.dk_vehicle_quick_category_button_description)
            it.setOnClickListener {
                viewModel.computeNextScreen(requireContext(), VehiclePickerStep.CATEGORY_DESCRIPTION)
            }
        }

        if (this::vehiclePickerCategoryItem.isInitialized) {
            textViewDescription.normalText()
            textViewDescription.text = vehiclePickerCategoryItem.description
            imageViewCategory.setImageDrawable(vehiclePickerCategoryItem.icon2)
        }
    }

    private fun customizeButton(button: View, @StringRes titleId: Int, @StringRes descriptionId: Int) {
        button.background = ContextCompat.getDrawable(button.context, R.drawable.dk_vehicle_category_button_action_border)
        button.background.tintDrawable(DKColors.secondaryColor)
        button.findViewById<TextView>(R.id.buttonTitle).run {
            isAllCaps = true
            headLine2()
            setText(titleId)
        }
        button.findViewById<TextView>(R.id.buttonDescription).run {
            smallText()
            setText(descriptionId)
        }
    }
}
