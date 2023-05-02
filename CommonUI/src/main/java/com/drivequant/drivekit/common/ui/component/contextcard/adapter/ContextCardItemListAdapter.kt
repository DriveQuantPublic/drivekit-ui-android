package com.drivequant.drivekit.common.ui.component.contextcard.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCard
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCardItem
import com.drivequant.drivekit.common.ui.component.contextcard.viewholder.ContextCardItemViewHolder

internal class ContextCardItemListAdapter(
    private var context: Context,
    private var items: List<DKContextCardItem>
) : RecyclerView.Adapter<ContextCardItemViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun update(contextCard: DKContextCard) {
        this.items = contextCard.getItems()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContextCardItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dk_context_card_list_item, parent, false)
        return ContextCardItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContextCardItemViewHolder, position: Int) {
        this.items.getOrNull(position)?.let {
            holder.bind(it.getColorResId(), it.getTitle(context), it.getSubtitle(context))
        }
    }

    override fun getItemCount() = this.items.size

}
