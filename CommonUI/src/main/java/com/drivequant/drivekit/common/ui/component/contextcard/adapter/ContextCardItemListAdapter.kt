package com.drivequant.drivekit.common.ui.component.contextcard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCard
import com.drivequant.drivekit.common.ui.component.contextcard.viewholder.ContextCardItemViewHolder

internal class ContextCardItemListAdapter(
    private var context: Context,
    private val contextCard: DKContextCard
) : RecyclerView.Adapter<ContextCardItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContextCardItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dk_context_card_list_item, parent, false)
        return ContextCardItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContextCardItemViewHolder, position: Int) {
        this.contextCard.getItems().getOrNull(position)?.let {
            holder.bind(it.getColorResId(), it.getTitleResId())
        }
    }

    override fun getItemCount() = this.contextCard.getItems().size

}
