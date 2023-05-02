package com.drivequant.drivekit.ui.drivingconditions.component.context

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCard
import com.drivequant.drivekit.common.ui.component.contextcard.view.DKContextCardView

internal class DrivingConditionsContextsViewPagerAdapter :
    RecyclerView.Adapter<DrivingConditionsContextsViewHolder>() {

    private var contextCards: List<DKContextCard>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun configure(contextCards: List<DKContextCard>) {
        this.contextCards = contextCards
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DrivingConditionsContextsViewHolder {
        val view = DKContextCardView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return DrivingConditionsContextsViewHolder(view)
    }

    override fun getItemCount(): Int = this.contextCards?.size ?: 0

    override fun onBindViewHolder(holder: DrivingConditionsContextsViewHolder, position: Int) {
        this.contextCards?.get(position)?.let {
            holder.configure(it)
        }
    }

}
