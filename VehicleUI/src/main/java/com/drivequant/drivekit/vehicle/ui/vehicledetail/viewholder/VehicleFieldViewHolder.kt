package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.component.EditableText
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.Field
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.GroupField
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.VehicleDetailViewModel

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
        viewModel.vehicle?.let {
            editTextSettings.setLabel(field.getTitle(context, it))
            editTextSettings.setHint(field.getValue(context, it, listOf())) // TODO listOf
            editTextSettings.setSettingsText(field.getValue(context, it, listOf())) // TODO listOf
            editTextSettings.tag = field.getTitle(context, it) // TODO add interface method to retrieve keyValue ?
        }
        field.getKeyboardType()?.let {
            editTextSettings.setInputType(it)
        }
        editTextSettings.setIsEditable(field.isEditable())
        return editTextSettings
    }
}