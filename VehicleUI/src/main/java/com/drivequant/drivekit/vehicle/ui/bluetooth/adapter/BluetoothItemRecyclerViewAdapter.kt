package com.drivequant.drivekit.vehicle.ui.bluetooth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.tripanalysis.bluetooth.BluetoothData
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleListQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.bluetooth.viewmodel.BluetoothViewModel

class BluetoothItemRecyclerViewAdapter(
    var context: Context,
    private val viewModel: BluetoothViewModel
) : RecyclerView.Adapter<BluetoothItemRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(viewgroup: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_item_bluetooth_device, viewgroup, false)
        FontUtils.overrideFonts(context, view)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentDevice = viewModel.bluetoothDevices[position]

        holder.textViewName.normalText()
        holder.textViewName.text = currentDevice.name

        holder.arrow.drawable.tintDrawable(DKColors.secondaryColor)

        if (position == viewModel.bluetoothDevices.indices.last && itemCount > 1) {
            holder.separator.visibility = View.GONE
        } else {
            holder.separator.visibility = View.VISIBLE
        }
        holder.container.setOnClickListener {
            it.context?.let { context ->
                val bluetoothDevice = viewModel.bluetoothDevices[position]
                DriveKitVehicle.getVehiclesOrderByNameAsc(object : VehicleListQueryListener{
                    override fun onResponse(status: VehicleSyncStatus, vehicles: List<Vehicle>) {
                        if (viewModel.isBluetoothAlreadyPaired(bluetoothDevice.macAddress, vehicles)) {
                            showDeviceAlreadyPaired(context, bluetoothDevice)
                        } else {
                            viewModel.addBluetoothToVehicle(bluetoothDevice)
                        }
                    }
                }, SynchronizationType.CACHE)
            }
        }
    }

    override fun getItemCount(): Int {
        return viewModel.bluetoothDevices.size
    }

    private fun showDeviceAlreadyPaired(context: Context, bluetoothDevice: BluetoothData){
        val alert = DKAlertDialog.LayoutBuilder().init(context)
            .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
            .cancelable(true)
            .positiveButton(context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_close)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        val title = alert.findViewById<TextView>(R.id.text_view_alert_title)
        val description = alert.findViewById<TextView>(R.id.text_view_alert_description)

        val btDeviceName = bluetoothDevice.name ?: bluetoothDevice.macAddress

        title?.setText(R.string.app_name)
        val text = DKResource.buildString(
            context, DKColors.mainFontColor,
            DKColors.mainFontColor,
            R.string.dk_vehicle_bluetooth_already_paired,
            btDeviceName,
            viewModel.vehicleName
        )
        description?.text = text

        title?.headLine1()
        description?.normalText()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container: LinearLayout = itemView.findViewById(R.id.container)
        val textViewName: TextView = itemView.findViewById(R.id.text_view_title)
        val arrow: ImageView = itemView.findViewById(R.id.image_view_arrow)
        val separator: View = itemView.findViewById(R.id.view_separator)
    }
}
