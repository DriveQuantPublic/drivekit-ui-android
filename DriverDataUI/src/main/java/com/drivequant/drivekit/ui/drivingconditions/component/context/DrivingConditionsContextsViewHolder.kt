package com.drivequant.drivekit.ui.drivingconditions.component.context

import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCard
import com.drivequant.drivekit.common.ui.component.contextcard.view.DKContextCardView

internal class DrivingConditionsContextsViewHolder(private val contextCardView: DKContextCardView) : RecyclerView.ViewHolder(contextCardView) {

    fun configure(contextCard: DKContextCard) {
        this.contextCardView.configure(contextCard)
    }

}
