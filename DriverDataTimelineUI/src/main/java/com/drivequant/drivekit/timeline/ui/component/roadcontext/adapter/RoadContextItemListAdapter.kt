package com.drivequant.drivekit.timeline.ui.component.roadcontext.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextViewModel

internal class RoadContextItemListAdapter(
    private var context: Context,
    private val viewModel: RoadContextViewModel
) : RecyclerView.Adapter<RoadContextItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoadContextItemViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.dk_road_context_list_item, parent, false)
        return RoadContextItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoadContextItemViewHolder, position: Int) {
        holder.bind(viewModel.getRoadContextList()[position])
    }

    override fun getItemCount() = viewModel.getRoadContextList().size
}
