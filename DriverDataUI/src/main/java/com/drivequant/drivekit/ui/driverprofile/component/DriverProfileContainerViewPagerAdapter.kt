package com.drivequant.drivekit.ui.driverprofile.component

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

internal class DriverProfileContainerViewPagerAdapter<ViewModel>(
    @LayoutRes private val viewResourceId: Int,
    private val viewModels: List<ViewModel>
) : RecyclerView.Adapter<DriverProfileContainerViewHolder<ViewModel>>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DriverProfileContainerViewHolder<ViewModel> {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        @Suppress("UNCHECKED_CAST") val view = layoutInflater.inflate(this.viewResourceId, parent, false) as DriverProfileView<ViewModel>
        return DriverProfileContainerViewHolder(view)
    }

    override fun getItemCount(): Int = this.viewModels.size

    override fun onBindViewHolder(holder: DriverProfileContainerViewHolder<ViewModel>, position: Int) {
        holder.configure(this.viewModels[position])
    }
}
