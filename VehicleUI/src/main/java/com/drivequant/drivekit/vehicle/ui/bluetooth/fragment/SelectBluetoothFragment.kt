package com.drivequant.drivekit.vehicle.ui.bluetooth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.bluetooth.adapter.BluetoothItemRecyclerViewAdapter
import com.drivequant.drivekit.vehicle.ui.bluetooth.viewmodel.BluetoothViewModel
import kotlinx.android.synthetic.main.fragment_bluetooth_select.text_view_select_description
import kotlinx.android.synthetic.main.fragment_bluetooth_select.text_view_select_list_title

class SelectBluetoothFragment: Fragment() {

    companion object {
        fun newInstance(viewModel: BluetoothViewModel, vehicleId: String): SelectBluetoothFragment {
            val fragment = SelectBluetoothFragment()
            fragment.vehicleId = vehicleId
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel : BluetoothViewModel
    private lateinit var vehicleId : String
    private lateinit var globalView: View
    private lateinit var adapter: BluetoothItemRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        globalView = inflater.inflate(R.layout.fragment_bluetooth_select, container, false).setDKStyle()
        return globalView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_view_select_description.normalText(DriveKitUI.colors.mainFontColor())
        text_view_select_description.text = DKResource.convertToString(requireContext(), "dk_vehicle_select_bluetooth_description")

        text_view_select_list_title.headLine1(DriveKitUI.colors.primaryColor())
        text_view_select_list_title.text = DKResource.convertToString(requireContext(), "dk_vehicle_bluetooth_devices_list_title")

        setupRecyclerView()

        viewModel.addBluetoothObserver.observe(viewLifecycleOwner) { identifier ->
            identifier?.let {
                val message = DKResource.convertToString(requireContext(), it)
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView? = globalView.findViewById(R.id.recycler_view)
        recyclerView?.let {
            it.layoutManager =
                LinearLayoutManager(context)
            adapter = BluetoothItemRecyclerViewAdapter(requireContext(), viewModel)
            recyclerView.adapter = adapter
        }
    }
}
