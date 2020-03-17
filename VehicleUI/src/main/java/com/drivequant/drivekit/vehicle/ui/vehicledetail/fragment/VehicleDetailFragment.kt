package com.drivequant.drivekit.vehicle.ui.vehicledetail.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKitSharedPreferencesUtils
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.computeTitle
import com.drivequant.drivekit.vehicle.ui.listener.OnCameraPictureTakenCallback
import com.drivequant.drivekit.vehicle.ui.vehicledetail.adapter.VehicleFieldsListAdapter
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.CameraGalleryPickerHelper
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
    private var fieldsAdapter : VehicleFieldsListAdapter? = null

    private var cameraFilePath : String? = null

    private lateinit var onCameraCallback: OnCameraPictureTakenCallback

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
        cameraFilePath = DriveKitSharedPreferencesUtils.getString("VEHICLE_PICTURE_PREF_%s") // TODO: %s = vehicleId
        onCameraCallback = object : OnCameraPictureTakenCallback {
            override fun pictureTaken(filePath: String) {
                cameraFilePath = filePath
            }
        }

        Glide.with(this)
            .load(if (!TextUtils.isEmpty(cameraFilePath)) cameraFilePath else R.drawable.dk_vehicle_default)
            .placeholder(R.drawable.dk_vehicle_default)
            .into(image_view_vehicle)

        collapsing_toolbar.title = viewModel.vehicle?.computeTitle(requireContext(), listOf()) // TODO: create a method to retrieve all vehicles, everywhere
        collapsing_toolbar.setExpandedTitleColor(DriveKitUI.colors.fontColorOnPrimaryColor())

        fab.setBackgroundColor(DriveKitUI.colors.secondaryColor())
        fab.setOnClickListener {
            manageFabAlertDialog()
        }

        if (fieldsAdapter != null){
            fieldsAdapter?.setGroupFields(viewModel.groupFields)
            fieldsAdapter?.notifyDataSetChanged()
        } else {
            fieldsAdapter = VehicleFieldsListAdapter(requireContext(), viewModel, viewModel.groupFields)
            vehicle_fields.adapter = fieldsAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(view.context)
        vehicle_fields.layoutManager = linearLayoutManager
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
            it.text = DKResource.convertToString(requireActivity(), "dk_take_picture")
            it.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                        // TODO: rationale alert dialog
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(),
                            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 12)
                    }
                } else {
                    launchCameraIntent()
                }
            }
        }
        galleryTextView?.let {
            it.text = DKResource.convertToString(requireContext(), "dk_select_image_gallery")
            it.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // TODO: rationale alert dialog
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 13)
                    }
                } else {
                    launchGalleryIntent()
                }
            }
        }
    }

    private fun launchCameraIntent(){
        viewModel.vehicle?.let {
            CameraGalleryPickerHelper.openCamera(requireActivity(), it.vehicleId, onCameraCallback)
        }
    }

    private fun launchGalleryIntent(){
        viewModel.vehicle?.let {
            CameraGalleryPickerHelper.openGallery(requireActivity())
        }
    }

    private fun updatePicture(uri: Uri) {
        Glide.with(this).load(uri).placeholder(R.drawable.dk_vehicle_default).into(image_view_vehicle)
    }

    private fun saveVehiclePictureLocalPath(path: String?){
        path?.let {
            DriveKitSharedPreferencesUtils.setString("VEHICLE_PICTURE_PREF_%s", path)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CameraGalleryPickerHelper.REQUEST_GALLERY -> {
                    CameraGalleryPickerHelper.buildUriFromIntentData(data)?.let {
                        updatePicture(it)
                        val path = CameraGalleryPickerHelper.retrieveAbsolutePathFromUri(requireContext(), it)
                        saveVehiclePictureLocalPath(path)
                    }
                }
                CameraGalleryPickerHelper.REQUEST_CAMERA -> {
                    updatePicture(Uri.parse(cameraFilePath))
                    saveVehiclePictureLocalPath(cameraFilePath)
                }
                //REQUEST_PERMISSIONS_OPEN_SETTINGS -> checkRequiredPermissions()
            }
        } else {
            cameraFilePath = null
        }
    }
}