package com.drivequant.drivekit.tripanalysis.workinghours.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.tripanalysis.workinghours.viewholder.WorkingHoursDayViewHolder
import com.drivequant.drivekit.tripanalysis.workinghours.viewmodel.WorkingHoursViewModel

internal class WorkingHoursListAdapter(
    val context: Context,
    private val viewModel: WorkingHoursViewModel
) : RecyclerView.Adapter<WorkingHoursDayViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkingHoursDayViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.dk_working_hours_day_item, parent, false)
        return WorkingHoursDayViewHolder(view, viewModel)
    }

    override fun getItemCount() =
        viewModel.config?.dayConfiguration?.size ?: 0

    override fun onBindViewHolder(parent: WorkingHoursDayViewHolder, position: Int) {
        parent.bind(position)
    }
}