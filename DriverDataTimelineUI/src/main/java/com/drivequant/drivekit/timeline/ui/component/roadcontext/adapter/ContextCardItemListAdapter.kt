package com.drivequant.drivekit.timeline.ui.component.roadcontext.adapter

//import android.content.Context
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.drivequant.drivekit.common.ui.component.contextcards.viewholder.ContextCardItemViewHolder
//import com.drivequant.drivekit.timeline.ui.R
//import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextViewModel
//import com.drivequant.drivekit.timeline.ui.component.roadcontext.getColorResId
//import com.drivequant.drivekit.timeline.ui.component.roadcontext.getTitleResId
//
//internal class ContextCardItemListAdapter(
//    private var context: Context,
//    private val viewModel: RoadContextViewModel
//) : RecyclerView.Adapter<ContextCardItemViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContextCardItemViewHolder {
//        val view =
//            LayoutInflater.from(context).inflate(R.layout.dk_context_card_list_item, parent, false)
//        return ContextCardItemViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ContextCardItemViewHolder, position: Int) {
//        val roadContext = viewModel.distanceByContext.keys.toTypedArray()[position]
//        holder.bind(roadContext.getColorResId(), roadContext.getTitleResId())
//    }
//
//    override fun getItemCount() = viewModel.distanceByContext.size
//}
