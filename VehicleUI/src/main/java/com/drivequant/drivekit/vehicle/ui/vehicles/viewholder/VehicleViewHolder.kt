package com.drivequant.drivekit.vehicle.ui.vehicles.viewholder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.View
import android.widget.*
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.DriverVehicleUI
import com.drivequant.drivekit.vehicle.ui.extension.isConfigured
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.DetectionModeType
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.MenuItem
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.PopupMenuItem
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehiclesListViewModel
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicles.adapter.DetectionModeSpinnerAdapter


class VehicleViewHolder(itemView: View, var viewModel: VehiclesListViewModel) : RecyclerView.ViewHolder(itemView) {
    private val textViewTitle: TextView = itemView.findViewById(R.id.text_view_title)
    private val textViewSubtitle: TextView = itemView.findViewById(R.id.text_view_subtitle)
    private val popup: ImageView = itemView.findViewById(R.id.image_view_popup)
    private val linearLayoutDetectionMode: LinearLayout = itemView.findViewById(R.id.detection_mode_container)
    val spinnerDetectionMode: Spinner = itemView.findViewById(R.id.spinner_vehicle_detection_mode)
    private val textViewDetectionModeTitle: TextView = itemView.findViewById(R.id.text_view_detection_mode_title)
    private val textViewDetectionModeDescription: TextView = itemView.findViewById(R.id.text_view_detection_mode_description)
    private val buttonSetup: Button = itemView.findViewById(R.id.text_view_setup_button)
    private val viewSeparator = itemView.findViewById<View>(R.id.view_separator)

    fun bind(vehicle: Vehicle){
        val context = itemView.context
        val subTitle = viewModel.getSubtitle(context, vehicle)
        textViewTitle.text = viewModel.getTitle(context, vehicle)

        subTitle?.let {
            textViewSubtitle.visibility = View.VISIBLE
            textViewSubtitle.text = subTitle
        }?: run {
            textViewSubtitle.visibility = View.GONE
        }

        setupPopup(context, viewModel, vehicle)
        setupDetectionModeContainer(context, vehicle)
        setupConfigureButton(context, vehicle)
        setupUI()
    }

    private fun setupUI() {
        viewSeparator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        textViewTitle.headLine1()
        textViewSubtitle.smallText()
        textViewDetectionModeTitle.smallText()
        textViewDetectionModeDescription.normalText()

        popup.setColorFilter(DriveKitUI.colors.secondaryColor())
        buttonSetup.setTextColor(DriveKitUI.colors.secondaryColor())
    }

    private fun setupPopup(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle){
        popup.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            val itemsList : List<MenuItem> = PopupMenuItem.values().toList()
            for (i in itemsList.indices){
                if (itemsList[i].isDisplayable(vehicle, viewModel.vehiclesList)) {
                    popupMenu.menu.add(Menu.NONE, i, i, DKSpannable().append(itemsList[i].getTitle(context),context.resSpans {
                        //TODO WIP : SET FONT
                        color(DriveKitUI.colors.mainFontColor())
                    }).toSpannable())
                }
            }
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { menuItem ->
                itemsList[menuItem.itemId].onItemClicked(context, viewModel, vehicle)
                return@setOnMenuItemClickListener false
            }
        }
    }

    private fun setupDetectionModeContainer(context: Context, vehicle: Vehicle){
        if (DriverVehicleUI.detectionModes.size == 1){
            linearLayoutDetectionMode.visibility = View.GONE
        } else {
            linearLayoutDetectionMode.visibility = View.VISIBLE
            textViewDetectionModeTitle.text = context.getString(R.string.dk_vehicle_detection_mode_title)
        }

        textViewDetectionModeDescription.text = DetectionModeType.getEnumByDetectionMode(vehicle.detectionMode).getDescription(context, vehicle)
        if (vehicle.isConfigured()){
            textViewDetectionModeDescription.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        } else {
            DKResource.convertToDrawable(context, "dk_common_info_filled")?.let { infoDrawable ->
                val bitmap = (infoDrawable as BitmapDrawable).bitmap
                val size = context.resources.getDimension(R.dimen.dk_ic_medium).toInt()
                val resizedDrawable: Drawable = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(bitmap, size, size, true))
                DrawableCompat.setTint(resizedDrawable, DriveKitUI.colors.criticalColor())
                textViewDetectionModeDescription.setCompoundDrawablesRelativeWithIntrinsicBounds(resizedDrawable, null, null, null)
            }
        }

        val detectionModes = viewModel.buildDetectionModeSpinnerItems(context)
        val adapter = DetectionModeSpinnerAdapter(context, R.id.text_view, detectionModes)
        spinnerDetectionMode.adapter = adapter
    }

    fun selectDetectionMode(context: Context, vehicle: Vehicle){
        val detectionModes = viewModel.buildDetectionModeSpinnerItems(context)
        for (i in detectionModes.indices){
            val detectionMode = DetectionMode.getEnumByName(detectionModes[i].detectionModeType.name)
            if (detectionMode == vehicle.detectionMode){
                spinnerDetectionMode.setSelection(i, false)
            }
        }
    }

    private fun setupConfigureButton(context: Context, vehicle: Vehicle){
        val configureText = DetectionModeType.getEnumByDetectionMode(vehicle.detectionMode).getConfigureButtonText(context)
        if (configureText.isEmpty()){
            buttonSetup.visibility = View.GONE
        } else {
            buttonSetup.text = configureText
            buttonSetup.visibility = View.VISIBLE
            buttonSetup.setOnClickListener {
                DetectionModeType.getEnumByDetectionMode(vehicle.detectionMode).onConfigureButtonClicked(context, viewModel, vehicle)
            }
        }
    }
}