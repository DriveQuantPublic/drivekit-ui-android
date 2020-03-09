package com.drivequant.drivekit.vehicle.ui.vehicles.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.AdapterView
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicles.viewholder.DetectionModeSpinnerItem
import com.drivequant.drivekit.vehicle.ui.vehicles.viewholder.VehicleViewHolder
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.DetectionModeType
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehiclesListViewModel

class VehiclesListAdapter(
    var context: Context?,
    private val viewModel: VehiclesListViewModel,
    private var vehicles: MutableList<Vehicle>,
    private var touched: Boolean = false
) : RecyclerView.Adapter<VehicleViewHolder>() {

    override fun onCreateViewHolder(viewgroup: ViewGroup, position: Int): VehicleViewHolder {
        val layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_vehicle_item_list, null)
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
                    val vehicle = vehicles[position]
                    val newDetectionMode = DetectionMode.DISABLED

                    // TODO retrieve listOf from Singleton
                    val detectionModes = mutableListOf<DetectionModeSpinnerItem>()
                    detectionModes.add(DetectionModeSpinnerItem(view.context, DetectionModeType.DISABLED))
                    detectionModes.add(DetectionModeSpinnerItem(view.context, DetectionModeType.GPS))
                    detectionModes.add(DetectionModeSpinnerItem(view.context, DetectionModeType.BEACON))
                    detectionModes.add(DetectionModeSpinnerItem(view.context, DetectionModeType.BLUETOOTH))

                    val detectionMode = DetectionMode.getEnumByName(detectionModes[i].detectionModeType.name)
                    DetectionModeType.getEnumByDetectionMode(detectionMode).detectionModeSelected(view.context, vehicle)

                    touched = false
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        holder.bind(vehicles[position])
        holder.selectDetectionMode(context, vehicles[position])
    }

    fun setVehicles(vehicles: List<Vehicle>){
        this.vehicles.clear()
        this.vehicles.addAll(vehicles.toMutableList())
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }
}