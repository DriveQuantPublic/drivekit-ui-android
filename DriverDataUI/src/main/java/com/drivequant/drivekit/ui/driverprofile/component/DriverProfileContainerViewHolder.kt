package com.drivequant.drivekit.ui.driverprofile.component

import androidx.recyclerview.widget.RecyclerView

internal class DriverProfileContainerViewHolder<ViewModel>(private val view: DriverProfileView<ViewModel>) :
    RecyclerView.ViewHolder(view) {

    fun configure(viewModel: ViewModel) {
        this.view.configure(viewModel)
    }
}
