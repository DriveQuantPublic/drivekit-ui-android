package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewholder

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
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

    fun bind(groupField: GroupField) {
        viewModel.vehicle?.let {
            container.removeAllViews()
            for (field in groupField.getFields(it)) {
                container.addView(buildField(itemView.context, field))
            }
        }
    }

    private fun buildField(context: Context, field: Field) : EditableText {
        val editTextSettings = EditableText(context)
        FontUtils.overrideFonts(context, editTextSettings)
        viewModel.vehicle?.let {
            editTextSettings.setLabel(field.getTitle(context, it))
            editTextSettings.setDescription(field.getDescription(context, it))
            editTextSettings.setHint(field.getValue(context, it))
            editTextSettings.setSettingsText(field.getValue(context, it))
            editTextSettings.tag = field.getTitle(context, it)
        }
        field.getKeyboardType()?.let {
            editTextSettings.setInputType(it)
        }
        editTextSettings.setIsEditable(field.isEditable())
        if (field.isEditable()) {
            viewModel.newEditableFieldObserver.value = EditableField(field, editTextSettings)
        }
        return editTextSettings
    }
}
