package com.drivequant.drivekit.vehicle.ui.vehicles.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.View
import android.widget.*
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.DetectionModeType
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehiclesListViewModel

class VehicleViewHolder(itemView: View, var viewModel: VehiclesListViewModel) : RecyclerView.ViewHolder(itemView) {
    private val textViewTitle: TextView = itemView.findViewById(R.id.text_view_title)
    private val textViewSubtitle: TextView = itemView.findViewById(R.id.text_view_subtitle)
    private val popup: ImageView = itemView.findViewById(R.id.image_view_popup)
    private val linearLayoutDetectionMode: LinearLayout = itemView.findViewById(R.id.detection_mode_container)
    val spinnerDetectionMode: Spinner = itemView.findViewById(R.id.spinner_vehicle_detection_mode)
    private val textViewDetectionModeTitle: TextView = itemView.findViewById(R.id.text_view_detection_mode_title)
    private val textViewDetectionModeDescription: TextView = itemView.findViewById(R.id.text_view_detection_mode_description)
    private val buttonSetup: Button = itemView.findViewById(R.id.text_view_setup_button)

    companion object {

    }

    fun bind(vehicle: Vehicle){
        val context = itemView.context
        textViewTitle.text = viewModel.getTitle(context, vehicle)
        textViewSubtitle.text = viewModel.getSubtitle(context, vehicle)
        setupPopup(context, vehicle)
        setupDetectionModeContainer(context, vehicle)
        setupConfigureButton(context, vehicle)
    }

    private fun setupPopup(context: Context, vehicle: Vehicle){
        // TODO via singleton, determinate if VM build items
        popup.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.menu.add(Menu.NONE, 1, 1, R.string.dk_vehicle_show)
            popupMenu.menu.add(Menu.NONE, 1, 1, R.string.dk_vehicle_rename)
            popupMenu.menu.add(Menu.NONE, 1, 1, R.string.dk_vehicle_replace)
            popupMenu.menu.add(Menu.NONE, 1, 1, R.string.dk_vehicle_delete)
            popupMenu.show()
        }
    }

    private fun setupDetectionModeContainer(context: Context, vehicle: Vehicle){
        // TODO via singleton, if detection mode size == 1 then View.GONE else View.VISIBLE
        linearLayoutDetectionMode.visibility = View.VISIBLE

        textViewDetectionModeTitle.text = context.getString(R.string.dk_vehicle_detection_mode_title)
        textViewDetectionModeDescription.text = DetectionModeType.getEnumByDetectionMode(vehicle.detectionMode).getDescription(context, vehicle)

        // TODO retrieve listOf from Singleton
        val detectionModes = mutableListOf<DetectionModeSpinnerItem>()
        detectionModes.add(DetectionModeSpinnerItem(context, DetectionModeType.DISABLED))
        detectionModes.add(DetectionModeSpinnerItem(context, DetectionModeType.GPS))
        detectionModes.add(DetectionModeSpinnerItem(context, DetectionModeType.BEACON))
        detectionModes.add(DetectionModeSpinnerItem(context, DetectionModeType.BLUETOOTH))

        val adapter: ArrayAdapter<DetectionModeSpinnerItem> = ArrayAdapter(context, R.layout.simple_list_item_spinner, detectionModes)
        spinnerDetectionMode.adapter = adapter
    }

    fun selectDetectionMode(context: Context?, vehicle: Vehicle){
        // TODO retrieve listOf from Singleton
        val detectionModes = mutableListOf<DetectionModeSpinnerItem>()
        detectionModes.add(DetectionModeSpinnerItem(context!!, DetectionModeType.DISABLED))
        detectionModes.add(DetectionModeSpinnerItem(context, DetectionModeType.GPS))
        detectionModes.add(DetectionModeSpinnerItem(context, DetectionModeType.BEACON))
        detectionModes.add(DetectionModeSpinnerItem(context, DetectionModeType.BLUETOOTH))

        for (i in detectionModes.indices){
            val detectionMode = DetectionMode.getEnumByName(detectionModes[i].detectionModeType.name)
            if (detectionMode == vehicle.detectionMode){
                spinnerDetectionMode.setSelection(i, false)
            }
        }
    }

    private fun setupConfigureButton(context: Context, vehicle: Vehicle){
        buttonSetup.text = DetectionModeType.getEnumByDetectionMode(vehicle.detectionMode).getConfigureButtonText(context)
        buttonSetup.setOnClickListener {
            DetectionModeType.getEnumByDetectionMode(vehicle.detectionMode).onConfigureButtonClicked(context, vehicle)
        }
        if (buttonSetup.text.isNullOrEmpty()){
            buttonSetup.visibility = View.GONE
        } else {
            buttonSetup.visibility = View.VISIBLE
        }
    }
}