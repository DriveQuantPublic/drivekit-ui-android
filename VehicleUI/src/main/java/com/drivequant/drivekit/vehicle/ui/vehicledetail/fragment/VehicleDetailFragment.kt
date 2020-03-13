package com.drivequant.drivekit.vehicle.ui.vehicledetail.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
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
            manageFabAlertDialog()
        }
    }

    private fun manageFabAlertDialog(){
        val alert = DKAlertDialog.LayoutBuilder()
            .init(requireContext())
            .layout(R.layout.alert_dialog_vehicle_detail_fab)
            .cancelable(true)
            .show()

        val title = alert.findViewById<TextView>(R.id.alert_dialog_header)
        val cameraTextView = alert.findViewById<TextView>(R.id.text_view_camera)
        val galleryTextView = alert.findViewById<TextView>(R.id.text_view_gallery)
        val delete= alert.findViewById<TextView>(R.id.text_view_delete)
        val separatorCamera = alert.findViewById<View>(R.id.view_separator_camera)
        val separatorGallery = alert.findViewById<View>(R.id.view_separator_gallery)

        val primaryColor = DriveKitUI.colors.primaryColor()
        val neutralColor = DriveKitUI.colors.neutralColor()
        title?.text = DKResource.convertToString(requireContext(), "dk_update_photo_title")
        title?.normalText(DriveKitUI.colors.fontColorOnPrimaryColor())
        title?.setBackgroundColor(primaryColor)

        cameraTextView?.headLine2(primaryColor)
        galleryTextView?.headLine2(primaryColor)
        delete?.headLine2(primaryColor)

        separatorCamera?.setBackgroundColor(neutralColor)
        separatorGallery?.setBackgroundColor(neutralColor)
        cameraTextView?.let {
            it.text = DKResource.convertToString(requireContext(), "dk_take_picture")
            it.setOnClickListener {
                // TODO launch camera
            }
        }
        galleryTextView?.let {
            it.text = DKResource.convertToString(requireContext(), "dk_select_image_gallery")
            it.setOnClickListener {
                // TODO launch gallery
            }
        }
    }
}