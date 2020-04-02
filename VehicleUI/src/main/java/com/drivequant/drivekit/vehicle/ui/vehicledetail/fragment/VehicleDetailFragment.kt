package com.drivequant.drivekit.vehicle.ui.vehicledetail.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.EditableText
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKitSharedPreferencesUtils
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.listener.OnCameraPictureTakenCallback
import com.drivequant.drivekit.vehicle.ui.vehicledetail.adapter.VehicleFieldsListAdapter
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.CameraGalleryPickerHelper
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.CameraGalleryPickerHelper.REQUEST_CAMERA
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.CameraGalleryPickerHelper.REQUEST_GALLERY
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.EditableField
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.VehicleDetailViewModel
import kotlinx.android.synthetic.main.fragment_vehicle_detail.*

class VehicleDetailFragment : Fragment() {

    companion object {
        fun newInstance(viewModel: VehicleDetailViewModel, vehicleId: String) : VehicleDetailFragment {
            val fragment = VehicleDetailFragment()
            fragment.viewModel = viewModel
            fragment.vehicleId = vehicleId
            return fragment
        }
    }

    private lateinit var vehicleId : String
    private lateinit var viewModel : VehicleDetailViewModel
    private var fieldsAdapter : VehicleFieldsListAdapter? = null

    private var editableFields: MutableList<EditableField> = mutableListOf()
    private var hasChangesToUpdate = false

    private var imageView: ImageView? = null

    private var cameraFilePath : String? = null

    private lateinit var onCameraCallback: OnCameraPictureTakenCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_vehicle_detail, container, false).setDKStyle()

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("vehicleDetailTag", vehicleId)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        val menuInflater = activity?.menuInflater
        menuInflater?.inflate(R.menu.vehicle_menu_bar, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (savedInstanceState?.getSerializable("vehicleDetailTag") as String?)?.let{
            vehicleId = it
        }

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        val collapsingToolbar = activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbar?.let {
            it.title = viewModel.vehicleName
            it.setExpandedTitleColor(DriveKitUI.colors.fontColorOnPrimaryColor())
            it.setCollapsedTitleTypeface(DriveKitUI.primaryFont(requireContext()))
            it.setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.let {
            DKResource.convertToDrawable(requireContext(), "dk_gallery_image")?.let {drawable ->
                val wrapped = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(wrapped, DriveKitUI.colors.fontColorOnSecondaryColor())
                it.setImageDrawable(wrapped)
            }
            it.backgroundTintList = ColorStateList.valueOf(DriveKitUI.colors.secondaryColor())
            it.setOnClickListener {
                manageFabAlertDialog()
            }
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
        onCameraCallback = object : OnCameraPictureTakenCallback {
            override fun pictureTaken(filePath: String) {
                cameraFilePath = filePath
            }
        }
        cameraFilePath = DriveKitSharedPreferencesUtils.getString(String.format("VEHICLE_PICTURE_PREF_%s", vehicleId))
        imageView = activity?.findViewById(R.id.image_view_vehicle)
        imageView?.let {
            Glide.with(this)
                .load(if (!TextUtils.isEmpty(cameraFilePath)) cameraFilePath else R.drawable.dk_vehicle_default)
                .placeholder(R.drawable.dk_vehicle_default)
                .into(it)
        }

        val linearLayoutManager = LinearLayoutManager(view.context)
        vehicle_fields.layoutManager = linearLayoutManager

        viewModel.newEditableFieldObserver.observe(this, Observer {
            it?.let { newEditableField ->
               if (!editableFields.contains(newEditableField)){
                   editableFields.add(newEditableField)
                   setupTextListener(newEditableField)
               }
            }
        })
    }

    private fun setupTextListener(editableField: EditableField){
        editableField.editableText.setOnTextChangedListener(object : EditableText.OnTextChangedListener{
            override fun onTextChanged(editableText: EditableText, text: String?) {
                hasChangesToUpdate = true
                if (text != null && editableField.field.isValid(text)){
                    editableField.editableText.getTextInputLayout()?.isErrorEnabled = false
                } else {
                    editableField.editableText.getTextInputLayout()?.isErrorEnabled = true
                    editableField.editableText.getTextInputLayout()?.error = editableField.field.getErrorDescription(requireContext())
                }
            }
        })
    }

    fun onBackPressed(){
        updateInformations()
    }

    fun updateInformations(){
        if (hasChangesToUpdate) {
            if (allFieldsValid()){
                viewModel.vehicle?.let { vehicle ->
                    for (item in editableFields){
                        item.field.onFieldUpdated(item.editableText.text, vehicle)
                    }
                }
            } else {
                Toast.makeText(requireContext(), DKResource.convertToString(requireContext(), "dk_fields_not_valid"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allFieldsValid() : Boolean {
        for (item in editableFields){
            if (!item.field.isValid(item.editableText.text)){
                return false
            }
        }
        return true
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
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                        displayRationaleAlert( "dk_common_permission_camera_rationale")
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(),
                            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA)
                    }
                } else {
                    if (alert.isShowing){
                        alert.dismiss()
                    }
                    launchCameraIntent()
                }
            }
        }
        galleryTextView?.let {
            it.text = DKResource.convertToString(requireContext(), "dk_select_image_gallery")
            it.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        displayRationaleAlert( "dk_common_permission_storage_rationale")
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_GALLERY)
                    }
                } else {
                    if (alert.isShowing){
                        alert.dismiss()
                    }
                    launchGalleryIntent()
                }
            }
        }
    }

    private fun displayRationaleAlert(descriptionIdentifier: String){
        val cameraDialog = DKAlertDialog.LayoutBuilder().init(requireContext())
            .layout(R.layout.template_alert_dialog_layout)
            .cancelable(false)
            .positiveButton(DKResource.convertToString(requireContext(), "dk_common_settings"),
                DialogInterface.OnClickListener { _, _ ->
                    launchSettings()
                })
            .negativeButton(DKResource.convertToString(requireContext(), "dk_common_close"),
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
            .show()

        val titleTextView = cameraDialog.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView = cameraDialog.findViewById<TextView>(R.id.text_view_alert_description)

        titleTextView?.text = DKResource.convertToString(requireContext(), "dk_common_permissions")
        descriptionTextView?.text = DKResource.convertToString(requireContext(), descriptionIdentifier)
        titleTextView?.headLine1()
        descriptionTextView?.normalText()
    }

    private fun launchSettings(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity?.packageName, null)
        intent.data = uri
        startActivity(intent)
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

    private fun updatePicture(uri: Uri?) {
        imageView?.let {
            Glide.with(this).load(uri).placeholder(R.drawable.dk_vehicle_default).into(it)
        }
    }

    private fun saveVehiclePictureLocalPath(path: String?){
        path?.let {
            DriveKitSharedPreferencesUtils.setString(String.format("VEHICLE_PICTURE_PREF_%s", vehicleId), path)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            if (requestCode == REQUEST_GALLERY) {
                launchGalleryIntent()
            } else {
                launchCameraIntent()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    CameraGalleryPickerHelper.buildUriFromIntentData(data)?.let {
                        updatePicture(it)
                        val path = CameraGalleryPickerHelper.retrieveAbsolutePathFromUri(requireContext(), it)
                        saveVehiclePictureLocalPath(path)
                    }
                }
                REQUEST_CAMERA -> {
                    updatePicture(Uri.parse(cameraFilePath))
                    saveVehiclePictureLocalPath(cameraFilePath)
                }
            }
        } else {
            cameraFilePath = null
        }
    }
}