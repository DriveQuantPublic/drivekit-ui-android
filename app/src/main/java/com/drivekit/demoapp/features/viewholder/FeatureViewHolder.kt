package com.drivekit.demoapp.features.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.drivekit.demoapp.component.FeatureCard
import com.drivekit.demoapp.features.enum.FeatureType
import com.drivekit.drivekitdemoapp.R

internal class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cardView = itemView.findViewById<FeatureCard>(R.id.feature_card)

    fun bind(feature: FeatureType) {
        feature.apply {
            cardView.configureTitle(getTitleResId())
            cardView.configureDescription(getDescriptionResId())
            // TODO le reste
        }
    }
}