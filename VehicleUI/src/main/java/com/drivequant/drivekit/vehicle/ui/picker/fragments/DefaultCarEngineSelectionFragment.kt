package com.drivequant.drivekit.vehicle.ui.picker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehiclePickerViewModel

internal class DefaultCarEngineSelectionFragment : Fragment() {

    private lateinit var viewModel: VehiclePickerViewModel

    companion object {
        internal fun newInstance(viewModel: VehiclePickerViewModel): DefaultCarEngineSelectionFragment {
            val fragment = DefaultCarEngineSelectionFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_vehicle_default_car_engine_selection, container, false).setDKStyle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val descriptionTextView: TextView = view.findViewById(R.id.text_view_description)
        val yesChoice: RadioButton = view.findViewById(R.id.choiceYes)
        val noChoice: RadioButton = view.findViewById(R.id.choiceNo)
        val validateButton: Button = view.findViewById(R.id.button_validate)

        descriptionTextView.bigText()
        yesChoice.normalText()
        noChoice.normalText()
        validateButton.apply {
            button()
            text = DKResource.convertToString(requireContext(), "dk_common_validate")
            setOnClickListener {
                viewModel.isElectric = yesChoice.isChecked
                viewModel.computeNextScreen(requireContext(), VehiclePickerStep.DEFAULT_CAR_ENGINE)
            }
        }
    }
}
