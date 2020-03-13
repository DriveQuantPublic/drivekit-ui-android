package com.drivequant.drivekit.vehicle.ui.vehicledetail.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.computeTitle
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.VehicleDetailViewModel
import kotlinx.android.synthetic.main.fragment_vehicle_detail.*

class VehicleDetailFragment : Fragment() {

    companion object {
        fun newInstance(vehicleId: String) : VehicleDetailFragment {
            val fragment = VehicleDetailFragment()
            fragment.vehicleId = vehicleId
            return fragment
        }
    }

    private lateinit var viewModel : VehicleDetailViewModel
    private lateinit var vehicleId : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  = inflater.inflate(R.layout.fragment_vehicle_detail, container, false).setDKStyle()


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("vehicleDetail", vehicleId)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getSerializable("vehicleDetail") as String?)?.let{
            vehicleId = it
        }
        viewModel = ViewModelProviders.of(this,
            VehicleDetailViewModel.VehicleDetailViewModelFactory(vehicleId)
        ).get(VehicleDetailViewModel::class.java)


        collapsing_toolbar.title = viewModel.vehicle?.computeTitle(requireContext(), listOf()) // TODO: create a method to retrieve all vehicles, everywhere
        collapsing_toolbar.setExpandedTitleColor(DriveKitUI.colors.fontColorOnPrimaryColor())

        fab.setBackgroundColor(DriveKitUI.colors.secondaryColor())
        fab.setOnClickListener {

        }

    }
}