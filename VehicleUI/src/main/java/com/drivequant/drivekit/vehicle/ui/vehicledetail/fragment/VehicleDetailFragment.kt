package com.drivequant.drivekit.vehicle.ui.vehicledetail.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import androidx.lifecycle.Observer
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.drivequant.drivekit.vehicle.ui.extension.getDefaultImage
import com.drivequant.drivekit.vehicle.ui.listener.OnCameraPictureTakenCallback
import com.drivequant.drivekit.vehicle.ui.vehicledetail.adapter.VehicleFieldsListAdapter
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.CameraGalleryPickerHelper
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.CameraGalleryPickerHelper.REQUEST_CAMERA
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.CameraGalleryPickerHelper.REQUEST_GALLERY
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.EditableField
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.FieldUpdatedListener
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
    private var defaultVehicleImage: Int = 0

    private var imageUri : Uri? = null

    private lateinit var onCameraCallback: OnCameraPictureTakenCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_vehicle_detail, container, false).setDKStyle()

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("vehicleDetailTag", vehicleId)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val menuInflater = activity?.menuInflater
        menuInflater?.inflate(R.menu.vehicle_menu_bar, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState?.let { bundle ->
            val vehicleId = bundle.getSerializable("vehicleDetailTag") as String?

            vehicleId?.let {
                this.vehicleId = it
                viewModel = VehicleDetailViewModel(it)
            }
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
                imageUri = Uri.parse(filePath)
            }
        }
        val vehicleUriSharedPrefs = DriveKitSharedPreferencesUtils.getString(String.format("drivekit-vehicle-picture_%s", vehicleId))
        if (vehicleUriSharedPrefs != null) {
            imageUri = Uri.parse(vehicleUriSharedPrefs)
        }
        imageView = activity?.findViewById(R.id.image_view_vehicle)

        viewModel.vehicle?.let {
            defaultVehicleImage = it.getDefaultImage()
        }

        imageView?.let {
            Glide.with(this)
                .load(imageUri ?: defaultVehicleImage)
                .placeholder(defaultVehicleImage)
                .into(it)
        }

        val linearLayoutManager =
            LinearLayoutManager(view.context)
        vehicle_fields.layoutManager = linearLayoutManager

        viewModel.newEditableFieldObserver.observe(this, Observer {
            it?.let { newEditableField ->
               if (!editableFields.contains(newEditableField)){
                   editableFields.add(newEditableField)
                   setupTextListener(newEditableField)
               }
            }
        })
        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(requireContext(), "dk_tag_vehicles_detail"), javaClass.simpleName)
    }

    private fun setupTextListener(editableField: EditableField){
        editableField.editableText.setOnTextChangedListener(object : EditableText.OnTextChangedListener{
            override fun onTextChanged(editableText: EditableText, text: String?) {
                hasChangesToUpdate = true
                viewModel.vehicle?.let { vehicle ->
                    if (text != null) {
                        if (editableField.field.isValid(text, vehicle)) {
                            editableField.editableText.getTextInputLayout()?.isErrorEnabled = false
                        } else {
                            editableField.editableText.getTextInputLayout()?.isErrorEnabled = true
                            editableField.editableText.getTextInputLayout()?.error = editableField.field.getErrorDescription(requireContext(), text, vehicle)
                        }
                    }
                }
            }
        })
    }

    fun onBackPressed(){
        if (hasChangesToUpdate){
            val alert = DKAlertDialog.LayoutBuilder().init(requireContext())
                .layout(R.layout.template_alert_dialog_layout)
                .cancelable(false)
                .positiveButton(getString(R.string.dk_common_confirm),
                    DialogInterface.OnClickListener { _, _ ->
                        updateInformations(true)
                    })
                .negativeButton(negativeListener = DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        activity?.finish()
                    }
                )
                .show()

            val title = alert.findViewById<TextView>(R.id.text_view_alert_title)
            val description = alert.findViewById<TextView>(R.id.text_view_alert_description)

            title?.text = getString(R.string.app_name)
            description?.text = DKResource.convertToString(requireContext(), "dk_vehicle_detail_back_edit_alert")
        } else {
            activity?.finish()
        }
    }

    fun updateInformations(fromBackButton: Boolean = false){
        if (hasChangesToUpdate) {
            if (allFieldsValid()){
                viewModel.progressBarObserver.postValue(true)
                viewModel.vehicle?.let { vehicle ->
                    for (item in editableFields){
                        item.field.onFieldUpdated(requireContext(), item.editableText.text, vehicle, object : FieldUpdatedListener {
                            override fun onFieldUpdated(success: Boolean, message: String) {
                                viewModel.progressBarObserver.postValue(false)
                                hasChangesToUpdate = false
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                if (fromBackButton){
                                    activity?.finish()
                                }
                            }
                        })
                    }
                }
            } else {
                Toast.makeText(requireContext(), DKResource.convertToString(requireContext(), "dk_fields_not_valid"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allFieldsValid() : Boolean {
        viewModel.vehicle?.let { vehicle ->
            for (item in editableFields){
                if (!item.field.isValid(item.editableText.text, vehicle)){
                    return false
                }
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
        title?.text = DKResource.convertToString(requireContext(), "dk_common_update_photo_title")
        title?.normalText(DriveKitUI.colors.fontColorOnPrimaryColor())
        title?.setBackgroundColor(primaryColor)

        cameraTextView?.headLine2(primaryColor)
        galleryTextView?.headLine2(primaryColor)
        delete?.headLine2(primaryColor)

        separatorCamera?.setBackgroundColor(neutralColor)
        separatorGallery?.setBackgroundColor(neutralColor)
        cameraTextView?.let {
            it.text = DKResource.convertToString(requireActivity(), "dk_common_take_picture")
            it.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA)
                } else {
                    if (alert.isShowing){
                        alert.dismiss()
                    }
                    launchCameraIntent()
                }
            }
        }
        galleryTextView?.let {
            it.text = DKResource.convertToString(requireContext(), "dk_common_select_image_gallery")
            it.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_GALLERY)
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
            .negativeButton(DKResource.convertToString(requireContext(), "dk_common_close"))
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
        imageView?.let { imageView ->
            viewModel.vehicle?.let { vehicle ->
                defaultVehicleImage = vehicle.getDefaultImage()
            }
            Glide.with(this).load(uri).placeholder(defaultVehicleImage).into(imageView)
        }
    }

    private fun saveVehiclePictureUri(uri: Uri?){
        uri?.let {
            DriveKitSharedPreferencesUtils.setString(String.format("drivekit-vehicle-picture_%s", vehicleId), it.toString())
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            REQUEST_GALLERY -> {
                if ((grantResults.isNotEmpty()) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchGalleryIntent()
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    displayRationaleAlert( "dk_common_permission_storage_rationale")
                } else {
                    Toast.makeText(requireContext(), DKResource.convertToString(requireContext(), "dk_common_permission_storage_rationale"), Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty()) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCameraIntent()
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                    displayRationaleAlert( "dk_common_permission_camera_rationale")
                } else {
                    Toast.makeText(requireContext(), DKResource.convertToString(requireContext(),"dk_common_permission_camera_rationale"), Toast.LENGTH_SHORT).show()
                }
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
                        saveVehiclePictureUri(it)
                    }
                }
                REQUEST_CAMERA -> {
                    val uri = imageUri
                    updatePicture(uri)
                    saveVehiclePictureUri(uri)
                }
            }
        } else {
            imageUri = null
        }
    }
}