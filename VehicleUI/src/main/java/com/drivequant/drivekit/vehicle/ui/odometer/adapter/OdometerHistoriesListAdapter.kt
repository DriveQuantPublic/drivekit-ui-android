package com.drivequant.drivekit.vehicle.ui.odometer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.capitalizeFirstLetter
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerHistoriesViewModel
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerHistoryData

internal class OdometerHistoriesListAdapter(
    private val context: Context,
    private val viewModel: OdometerHistoriesViewModel,
    private val listener: OdometerHistoriesListener
) :
    RecyclerView.Adapter<OdometerHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): OdometerHistoryViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dk_layout_history_list_item, parent, false)
        return OdometerHistoryViewHolder(view)
    }

    override fun getItemCount(): Int = viewModel.getOdometerHistoriesList().size

    override fun onBindViewHolder(parent: OdometerHistoryViewHolder, position: Int) {
        val history = viewModel.getOdometerHistoriesList()[position]
        parent.apply {
            bind(history)
            itemView.setOnClickListener {
                listener.onHistoryClicked(history.historyId, context)
            }
        }
    }
}

internal class OdometerHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val distance = itemView.findViewById<TextView>(R.id.text_view_distance_km)
    private val date = itemView.findViewById<TextView>(R.id.text_view_date)

    fun bind(historyData: OdometerHistoryData) {
        date.apply {
            text = historyData.getUpdateDate()?.capitalizeFirstLetter()
            smallText(DriveKitUI.colors.complementaryFontColor())
        }

        distance.apply {
            text = historyData.getRealDistance(itemView.context)
            headLine1()
        }
    }
}

internal interface OdometerHistoriesListener {
    fun onHistoryClicked(historyId: Int, context: Context)
}

