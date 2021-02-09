package com.drivequant.drivekit.vehicle.ui.vehicles.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.AdapterView
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicles.viewholder.VehicleViewHolder
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.DetectionModeType
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehiclesListViewModel

class VehiclesListAdapter(
    var context: Context,
    private val viewModel: VehiclesListViewModel,
    private var vehicles: List<Vehicle>,
    private var touched: Boolean = false
) : RecyclerView.Adapter<VehicleViewHolder>() {

    override fun onCreateViewHolder(viewgroup: ViewGroup, position: Int): VehicleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_vehicle_item_list, viewgroup, false)
        FontUtils.overrideFonts(context, view)
        return VehicleViewHolder(view, viewModel)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.spinnerDetectionMode.setOnTouchListener(OnTouchListener setOnTouchListener@{ v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_UP) {
                touched  = true
                v.performClick()
                return@setOnTouchListener true
            } else {
                touched = false
                return@setOnTouchListener false
            }
        })


        holder.spinnerDetectionMode.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (touched) {
                    val detectionModes = viewModel.buildDetectionModeSpinnerItems(view.context)
                    val detectionMode = DetectionMode.getEnumByName(detectionModes[i].detectionModeType.name)
                    DetectionModeType.getEnumByDetectionMode(detectionMode).detectionModeSelected(view.context, viewModel, vehicles[position])
                    touched = false
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        holder.bind(vehicles[position])
        holder.selectDetectionMode(context, vehicles[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }

    fun setTouched(touched: Boolean){
        this.touched = touched
    }

    fun setVehicles(vehicles: List<Vehicle>){
        this.vehicles = vehicles.toMutableList()
    }
}