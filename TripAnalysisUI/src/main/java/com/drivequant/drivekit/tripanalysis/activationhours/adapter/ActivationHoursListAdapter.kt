package com.drivequant.drivekit.tripanalysis.activationhours.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.tripanalysis.activationhours.viewholder.ActivationHoursDayViewHolder
import com.drivequant.drivekit.tripanalysis.activationhours.viewmodel.ActivationHoursViewModel

internal class ActivationHoursListAdapter(
    val context: Context,
    private val viewModel: ActivationHoursViewModel
) : RecyclerView.Adapter<ActivationHoursDayViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActivationHoursDayViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.dk_activation_hours_day_item, parent, false)
        return ActivationHoursDayViewHolder(view)
    }

    override fun getItemCount() =
        viewModel.dayConfigList?.size ?: 0

    override fun onBindViewHolder(parent: ActivationHoursDayViewHolder, position: Int) {
        viewModel.dayConfigList?.get(position)?.let { dayConfig ->
            parent.apply {
                bind(dayConfig)
                itemView.setOnClickListener {

                }
            }
        }
    }
}