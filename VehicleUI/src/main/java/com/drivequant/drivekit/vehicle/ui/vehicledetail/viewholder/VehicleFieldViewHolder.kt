package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.component.EditableText
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicledetail.adapter.VehicleFieldsListAdapter
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.EditableField
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.Field
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.GroupField
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.VehicleDetailViewModel

class VehicleFieldViewHolder(
    itemView: View,
    var adapter: VehicleFieldsListAdapter,
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

    private fun buildField(field: Field) : EditableText {
        val editTextSettings = EditableText(context)
        FontUtils.overrideFonts(context, editTextSettings)
        viewModel.vehicle?.let {
            editTextSettings.setLabel(field.getTitle(context, it))
            editTextSettings.setHint(field.getValue(context, it))
            editTextSettings.setSettingsText(field.getValue(context, it))
            editTextSettings.tag = field.getTitle(context, it)
        }
        field.getKeyboardType()?.let {
            editTextSettings.setInputType(it)
        }
        editTextSettings.setIsEditable(field.isEditable())
        if (field.isEditable()){
            viewModel.newEditableFieldObserver.value = EditableField(field, editTextSettings)
        }
        return editTextSettings
    }
}