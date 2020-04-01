package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.component.EditableText
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.Field
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.GroupField
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.VehicleDetailViewModel
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehiclesFetchListener

class VehicleFieldViewHolder(
    itemView: View,
    var viewModel: VehicleDetailViewModel
) : RecyclerView.ViewHolder(itemView) {

    private val container: LinearLayout = itemView.findViewById(R.id.group_container)
    private lateinit var context: Context

    fun bind(groupField: GroupField){
        context = itemView.context
        viewModel.vehicle?.let {
            for (field in groupField.getFields(it)){
                container.addView(buildField(field))
            }
        }
    }

    private fun buildField(field: Field) : View {
        val editTextSettings = EditableText(context)
        FontUtils.overrideFonts(context, editTextSettings)
        viewModel.vehicle?.let {
            VehicleUtils().fetchVehiclesOrderedByDisplayName(context, SynchronizationType.CACHE, object : VehiclesFetchListener{
                override fun onVehiclesLoaded(syncStatus: VehicleSyncStatus, vehicles: List<Vehicle>) {
                    editTextSettings.setLabel(field.getTitle(context, it))
                    editTextSettings.setHint(field.getValue(context, it))
                    editTextSettings.setSettingsText(field.getValue(context, it))
                    editTextSettings.tag = field.getTitle(context, it) // TODO add interface method to retrieve keyValue ?
                }
            })
        }
        field.getKeyboardType()?.let {
            editTextSettings.setInputType(it)
        }
        editTextSettings.setIsEditable(field.isEditable())
        return editTextSettings
    }
}