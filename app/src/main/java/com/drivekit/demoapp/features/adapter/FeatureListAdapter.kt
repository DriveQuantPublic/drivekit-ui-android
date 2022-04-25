package com.drivekit.demoapp.features.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drivekit.demoapp.features.enum.FeatureType
import com.drivekit.demoapp.features.viewholder.FeatureViewHolder
import com.drivekit.drivekitdemoapp.R

internal class FeatureListAdapter(
    private var context: Context,
    private val items: List<FeatureType>
) : RecyclerView.Adapter<FeatureViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.feature_list_item, parent, false)
        return FeatureViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}