package com.drivequant.drivekit.vehicle.ui.vehicledetail.fragment

import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.EditableText
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.getImageByTypeIndex
import com.drivequant.drivekit.vehicle.ui.vehicledetail.adapter.VehicleFieldsListAdapter
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.EditableField
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.FieldUpdatedListener
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.VehicleDetailViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
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
    @DrawableRes private var vehicleDrawableId: Int? = null

    private lateinit var menu: Menu

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
            appBarLayout?.let {
                viewModel.vehicleName?.let { vehicleName ->
                    it.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                        if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                            collapsingToolbarLayout.setCollapsedTitleTypeface(
                                DriveKitUI.secondaryFont(requireContext())
                            )
                            collapsingToolbarLayout.title = vehicleName
                        } else {
                            collapsingToolbarLayout.setCollapsedTitleTypeface(
                                DriveKitUI.primaryFont(requireContext())
                            )
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

        val vehicleFields = activity?.findViewById<RecyclerView>(R.id.vehicle_fields)
        fieldsAdapter?.apply {
            setGroupFields(viewModel.groupFields)
            notifyDataSetChanged()
        } ?: run {
            fieldsAdapter = VehicleFieldsListAdapter(requireContext(), viewModel)
            vehicleFields?.adapter = fieldsAdapter
        }

        imageView = activity?.findViewById(R.id.image_view_vehicle)

        viewModel.vehicle?.let {
            vehicleDrawableId = it.getImageByTypeIndex()
        }

        vehicleDrawableId?.let {
            imageView?.setImageResource(it)
        }

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
}
