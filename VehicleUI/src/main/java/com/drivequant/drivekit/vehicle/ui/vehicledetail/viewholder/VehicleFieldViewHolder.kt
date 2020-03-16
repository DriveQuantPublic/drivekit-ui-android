package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewholder

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
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
        val textInputLayout: TextInputLayout = LayoutInflater.from(itemView.context).inflate(R.layout.vehicle_field_item, null) as TextInputLayout
        textInputLayout.hint = "hint"

        viewModel.vehicle?.let {
            textInputLayout.helperText = field.getTitle(context, it)
        }
        textInputLayout.editText?.isEnabled = true

        return textInputLayout
    }
}