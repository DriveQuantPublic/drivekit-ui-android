package com.drivequant.drivekit.vehicle.ui.vehicledetail.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Typeface.BOLD
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.EditableText
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.getImageByTypeIndex
import com.drivequant.drivekit.vehicle.ui.listener.OnCameraPictureTakenCallback
import com.drivequant.drivekit.vehicle.ui.vehicledetail.adapter.VehicleFieldsListAdapter
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.VehicleCustomImageHelper
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.VehicleCustomImageHelper.REQUEST_CAMERA
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.EditableField
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.FieldUpdatedListener
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.VehicleDetailViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class VehicleDetailFragment : Fragment() {

    companion object {
        fun newInstance(
            viewModel: VehicleDetailViewModel,
            vehicleId: String
        ): VehicleDetailFragment {
            val fragment = VehicleDetailFragment()
            fragment.viewModel = viewModel
            fragment.vehicleId = vehicleId
            return fragment
        }
    }

    private var vehicleId: String? = null
    private lateinit var viewModel: VehicleDetailViewModel
    private var fieldsAdapter: VehicleFieldsListAdapter? = null

    private var editableFields: MutableList<EditableField> = mutableListOf()
    private var hasChangesToUpdate = false

    private var imageView: ImageView? = null

    private var cameraImageFilePath: String? = null

    private lateinit var onCameraCallback: OnCameraPictureTakenCallback
    private lateinit var menu: Menu
    private lateinit var alert: AlertDialog

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerGalleryLauncher()
    }

    private fun registerGalleryLauncher() {
        // Registers a photo picker activity launcher in single-select mode.
        this.pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            val vehicleId = vehicleId
            if (uri != null && vehicleId != null) {
                DriveKitVehicleUI.coroutineScope.launch {
                    val success = VehicleCustomImageHelper.saveImage(
                        this@VehicleDetailFragment.requireContext(),
                        VehicleCustomImageHelper.getVehicleFileName(vehicleId),
                        uri
                    )
                    if (success) {
                        updateVehicleImage()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_vehicle_detail, container, false).setDKStyle()

    override fun onSaveInstanceState(outState: Bundle) {
        this.vehicleId?.let {
            outState.putString("vehicleDetailTag", it)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.vehicle_menu_bar, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save) {
            updateContent()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        savedInstanceState?.getString("vehicleDetailTag")?.let {
            this.vehicleId = it
            viewModel = VehicleDetailViewModel(it)
        }
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val collapsingToolbar = activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        val appBarLayout = activity?.findViewById<AppBarLayout>(R.id.app_bar_layout)
        collapsingToolbar?.let { collapsingToolbarLayout ->
            collapsingToolbarLayout.setCollapsedTitleTypeface(
                DriveKitUI.secondaryFont(requireContext())
            )
            collapsingToolbarLayout.setExpandedTitleTypeface(
                DriveKitUI.primaryFont(requireContext())
            )
            appBarLayout?.let {
                viewModel.vehicleName?.let { vehicleName ->
                    it.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                        if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                            collapsingToolbarLayout.title = vehicleName
                        } else {
                            context?.let { context ->
                                collapsingToolbarLayout.title =
                                    DKSpannable().append(vehicleName, context.resSpans {
                                        typeface(BOLD)
                                    }).toSpannable()

                            }
                        }
                    }
                }
            }
            collapsingToolbarLayout.setExpandedTitleColor(DKColors.fontColorOnPrimaryColor)
        }

        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.let {
            ContextCompat.getDrawable(requireContext(), R.drawable.dk_gallery_image)?.let { drawable ->
                drawable.tintDrawable(DKColors.fontColorOnSecondaryColor)
                it.setImageDrawable(drawable)
            }
            it.backgroundTintList = ColorStateList.valueOf(DKColors.secondaryColor)
            it.setOnClickListener {
                context?.let { context ->
                    manageFabAlertDialog(context)
                }
            }
        }

        val vehicleFields = activity?.findViewById<RecyclerView>(R.id.vehicle_fields)
        fieldsAdapter?.apply {
            setGroupFields(viewModel.groupFields)
            notifyDataSetChanged()
        } ?: run {
            fieldsAdapter = VehicleFieldsListAdapter(requireContext(), viewModel)
            vehicleFields?.adapter = fieldsAdapter
        }

        onCameraCallback = object : OnCameraPictureTakenCallback {
            override fun pictureTaken(filePath: String) {
                this@VehicleDetailFragment.context?.let { context ->
                    val vehicleId = vehicleId
                    if (vehicleId != null) {
                        DriveKitVehicleUI.coroutineScope.launch() {
                            val success = VehicleCustomImageHelper.saveImage(
                                context,
                                VehicleCustomImageHelper.getVehicleFileName(vehicleId),
                                Uri.parse(filePath)
                            )
                            if (success) {
                                updateVehicleImage()
                            }
                        }
                    }
                }
            }

            override fun onFilePathReady(filePath: String) {
                cameraImageFilePath = filePath
            }
        }

        imageView = activity?.findViewById(R.id.image_view_vehicle)

        updateVehicleImage()

        vehicleFields?.layoutManager = LinearLayoutManager(view.context)
        viewModel.newEditableFieldObserver.observe(viewLifecycleOwner) {
            it?.let { newEditableField ->
                if (!editableFields.contains(newEditableField)) {
                    editableFields.add(newEditableField)
                    setupTextListener(newEditableField)
                }
            }
        }
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_vehicles_detail), javaClass.simpleName)
    }

    private fun updateVehicleImage() {
        val customImage = viewModel.vehicle?.vehicleId?.let { VehicleCustomImageHelper.getImageUri(it) } ?: run { null }

        if (customImage != null) {
            imageView?.apply {
                setImageURI(null)
                setImageURI(customImage)
            }
        } else {
            imageView?.setImageResource(viewModel.vehicle.getImageByTypeIndex())
        }
    }

    private fun setupTextListener(editableField: EditableField) {
        editableField.editableText.setOnTextChangedListener(object :
            EditableText.OnTextChangedListener {
            override fun onTextChanged(editableText: EditableText, text: String?) {
                updateSubmitButtonVisibility(true)
                viewModel.vehicle?.let { vehicle ->
                    if (text != null) {
                        if (editableField.field.isValid(text, vehicle)) {
                            editableField.editableText.getTextInputLayout().isErrorEnabled = false
                        } else {
                            editableField.editableText.getTextInputLayout().isErrorEnabled = true
                            editableField.editableText.getTextInputLayout().error = editableField.field.getErrorDescription(requireContext(), text, vehicle)
                        }
                    }
                }
            }
        })
    }

    private fun updateSubmitButtonVisibility(displaySubmitButton: Boolean) {
        hasChangesToUpdate = displaySubmitButton
        menu.findItem(R.id.action_save)?.isVisible = displaySubmitButton
    }

    fun onBackPressed() {
        context?.let { context ->
            if (hasChangesToUpdate) {
                val alert = DKAlertDialog.LayoutBuilder().init(context)
                    .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                    .cancelable(false)
                    .positiveButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_confirm)) { _, _ ->
                        updateContent(true)
                    }
                    .negativeButton(negativeListener = { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        activity?.finish()
                    }
                    )
                    .show()

                val title = alert.findViewById<TextView>(R.id.text_view_alert_title)
                val description = alert.findViewById<TextView>(R.id.text_view_alert_description)

                title?.setText(R.string.app_name)
                description?.setText(R.string.dk_vehicle_detail_back_edit_alert)
            } else {
                activity?.finish()
            }
        }
    }

    private fun updateContent(fromBackButton: Boolean = false) {
        context?.let { context ->
            if (hasChangesToUpdate) {
                if (allFieldsValid()) {
                    viewModel.progressBarObserver.postValue(true)
                    viewModel.vehicle?.let { vehicle ->
                        for (item in editableFields) {
                            item.field.onFieldUpdated(context, item.editableText.text, vehicle, object : FieldUpdatedListener {
                                    override fun onFieldUpdated(success: Boolean, message: String) {
                                        viewModel.progressBarObserver.postValue(false)
                                        if (success) {
                                            updateSubmitButtonVisibility(false)
                                        }
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        if (fromBackButton) {
                                            activity?.finish()
                                        }
                                    }
                                })
                        }
                    }
                } else {
                    Toast.makeText(context, R.string.dk_fields_not_valid, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun allFieldsValid(): Boolean {
        viewModel.vehicle?.let { vehicle ->
            for (item in editableFields) {
                if (!item.field.isValid(item.editableText.text, vehicle)) {
                    return false
                }
            }
        }
        return true
    }


    private fun manageFabAlertDialog(context: Context) {
        alert = DKAlertDialog.LayoutBuilder()
            .init(context)
            .layout(R.layout.alert_dialog_vehicle_detail_fab)
            .cancelable(true)
            .show()

        val title = alert.findViewById<TextView>(R.id.alert_dialog_header)
        val cameraTextView = alert.findViewById<TextView>(R.id.text_view_camera)
        val galleryTextView = alert.findViewById<TextView>(R.id.text_view_gallery)

        title?.setText(com.drivequant.drivekit.common.ui.R.string.dk_common_update_photo_title)
        title?.normalText()

        cameraTextView?.headLine2()
        galleryTextView?.headLine2()

        cameraTextView?.apply {
            setText(com.drivequant.drivekit.common.ui.R.string.dk_common_take_picture)
            setOnClickListener {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
                } else {
                    if (alert.isShowing) {
                        alert.dismiss()
                    }
                    launchCameraIntent()
                }
            }
        }
        galleryTextView?.apply {
            setText(com.drivequant.drivekit.common.ui.R.string.dk_common_select_image_gallery)
            setOnClickListener {
                launchPhotoPicker()
            }
        }
    }

    private fun displayRationaleAlert(@StringRes descriptionIdentifier: Int) {
        context?.let { context ->
            val cameraDialog = DKAlertDialog.LayoutBuilder().init(context)
                .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                .cancelable(false)
                .positiveButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_settings)) { _, _ ->
                    launchSettings()
                }
                .negativeButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_close))
                .show()

            val titleTextView = cameraDialog.findViewById<TextView>(R.id.text_view_alert_title)
            val descriptionTextView =
                cameraDialog.findViewById<TextView>(R.id.text_view_alert_description)

            titleTextView?.setText(com.drivequant.drivekit.common.ui.R.string.dk_common_permissions)
            descriptionTextView?.setText(descriptionIdentifier)
        }
    }

    private fun launchSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity?.packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun launchCameraIntent() {
        viewModel.vehicle?.let {
            VehicleCustomImageHelper.openCamera(requireActivity(), it.vehicleId, onCameraCallback)
        }
    }

    private fun launchPhotoPicker() {
        if (this::alert.isInitialized && alert.isShowing) {
            alert.dismiss()
        }
        VehicleCustomImageHelper.openPhotoPicker(this@VehicleDetailFragment.pickMedia)
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty()) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCameraIntent()
                    if (this::alert.isInitialized) {
                        alert.dismiss()
                    }
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                    displayRationaleAlert(com.drivequant.drivekit.common.ui.R.string.dk_common_permission_camera_rationale)
                } else {
                    Toast.makeText(requireContext(), com.drivequant.drivekit.common.ui.R.string.dk_common_permission_camera_rationale, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CAMERA) {
            cameraImageFilePath?.let {
                onCameraCallback.pictureTaken(it)
            }
        }
    }
}
