package com.drivequant.drivekit.vehicle.ui.vehicles.viewholder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.extension.tint
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.CustomTypefaceSpan
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.isConfigured
import com.drivequant.drivekit.vehicle.ui.vehicles.adapter.DetectionModeSpinnerAdapter
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.DetectionModeType
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehicleActionItem
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehiclesListViewModel

class VehicleViewHolder(itemView: View, var viewModel: VehiclesListViewModel) : RecyclerView.ViewHolder(itemView) {
    private val cardContainer: LinearLayout = itemView.findViewById(R.id.card_container)
    private val textViewTitle: TextView = itemView.findViewById(R.id.text_view_title)
    private val textViewSubtitle: TextView = itemView.findViewById(R.id.text_view_subtitle)
    private val popup: ImageView = itemView.findViewById(R.id.image_view_popup)
    private val linearLayoutDetectionMode: LinearLayout = itemView.findViewById(R.id.detection_mode_container)
    val spinnerDetectionMode: Spinner = itemView.findViewById(R.id.spinner_vehicle_detection_mode)
    private val textViewDetectionModeTitle: TextView = itemView.findViewById(R.id.text_view_detection_mode_title)
    private val textViewDetectionModeDescription: TextView = itemView.findViewById(R.id.text_view_detection_mode_description)
    private val buttonSetup: Button = itemView.findViewById(R.id.text_view_setup_button)

    fun bind(vehicle: Vehicle) {
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
        textViewTitle.headLine1()
        textViewSubtitle.smallText()
        textViewDetectionModeTitle.headLine2()
        textViewDetectionModeDescription.normalText()

        popup.setImageResource(com.drivequant.drivekit.common.ui.R.drawable.dk_common_dots)
        popup.setColorFilter(DKColors.secondaryColor)
        buttonSetup.button(
            textColor = DKColors.secondaryColor,
            backgroundColor = Color.parseColor("#00ffffff")
        )
    }

    private fun setupPopup(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle) {
        popup.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            val itemsList : List<VehicleActionItem> = DriveKitVehicleUI.vehicleActions
            for (i in itemsList.indices) {
                if (itemsList[i].isDisplayable(vehicle)) {
                    popupMenu.menu.add(Menu.NONE, i, i, DKSpannable().append(itemsList[i].getTitle(context),context.resSpans {
                        color(DKColors.mainFontColor)
                        DriveKitUI.primaryFont(context)?.let { typeface ->
                            typeface(CustomTypefaceSpan(typeface))
                        }
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
        if (DriveKitVehicleUI.detectionModes.size == 1) {
            linearLayoutDetectionMode.visibility = View.GONE
        } else {
            linearLayoutDetectionMode.visibility = View.VISIBLE
            textViewDetectionModeTitle.setText(R.string.dk_vehicle_detection_mode_title)
        }

        textViewDetectionModeDescription.text = DetectionModeType.getEnumByDetectionMode(vehicle.detectionMode).getDescription(context, vehicle)
        if (vehicle.isConfigured()){
            textViewDetectionModeDescription.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        } else {
            ContextCompat.getDrawable(context, com.drivequant.drivekit.common.ui.R.drawable.dk_common_warning)?.let { infoDrawable ->
                val bitmap = (infoDrawable as BitmapDrawable).bitmap
                val size = context.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_ic_medium).toInt()
                val resizedDrawable: Drawable = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(bitmap, size, size, true))
                resizedDrawable.tint(context, com.drivequant.drivekit.common.ui.R.color.criticalColor)
                textViewDetectionModeDescription.setCompoundDrawablesRelativeWithIntrinsicBounds(resizedDrawable, null, null, null)
            }
        }

        val detectionModes = viewModel.buildDetectionModeSpinnerItems(context)
        val adapter = DetectionModeSpinnerAdapter(context, detectionModes)
        spinnerDetectionMode.adapter = adapter
    }

    fun selectDetectionMode(context: Context, vehicle: Vehicle) {
        val detectionModes = viewModel.buildDetectionModeSpinnerItems(context)
        for (i in detectionModes.indices) {
            val detectionMode = DetectionMode.valueOf(detectionModes[i].detectionModeType.name)
            if (detectionMode == vehicle.detectionMode) {
                spinnerDetectionMode.setSelection(i, false)
            }
        }
    }

    private fun setupConfigureButton(context: Context, vehicle: Vehicle) {
        val configureText = DetectionModeType.getEnumByDetectionMode(vehicle.detectionMode).getConfigureButtonText(context)
        var removeBottomCardPadding = false
        if (configureText.isEmpty()) {
            buttonSetup.visibility = View.GONE
        } else {
            removeBottomCardPadding = true
            buttonSetup.text = configureText
            buttonSetup.visibility = View.VISIBLE
            buttonSetup.setOnClickListener {
                DetectionModeType.getEnumByDetectionMode(vehicle.detectionMode).onConfigureButtonClicked(context, viewModel, vehicle)
            }
        }

        val dkCardPadding = itemView.context.resources.getDimensionPixelSize(com.drivequant.drivekit.common.ui.R.dimen.dk_margin)
        val dkCardBottomPadding = if (removeBottomCardPadding) 0 else dkCardPadding
        this.cardContainer.setPadding(dkCardPadding, dkCardPadding, dkCardPadding, dkCardBottomPadding)
    }
}
