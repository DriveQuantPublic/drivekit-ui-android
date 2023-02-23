package com.drivequant.drivekit.ui.transportationmode.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.CircularButtonItemView
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.driverdata.trip.TransportationModeUpdateStatus
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.transportationmode.viewmodel.TransportationModeViewModel
import com.drivequant.drivekit.ui.transportationmode.viewmodel.TransportationProfile
import kotlinx.android.synthetic.main.dk_fragment_transportation_mode.*


internal class TransportationModeFragment : Fragment(){

    private lateinit var viewModel : TransportationModeViewModel
    private lateinit var itinId: String

    private val transportationModesViews = mutableListOf<CircularButtonItemView>()
    private val transportationProfilesViews = mutableListOf<CircularButtonItemView>()

    companion object {
        fun newInstance(itinId: String): TransportationModeFragment {
            val fragment = TransportationModeFragment()
            fragment.itinId = itinId
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dk_fragment_transportation_mode, container, false).setDKStyle()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(requireContext(), "dk_tag_trips_detail_transportation_mode"), javaClass.simpleName)
        savedInstanceState?.getString("itinId")?.let{
            itinId = it
        }
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this,
                TransportationModeViewModel.TransportationModeViewModelFactory(itinId)
            )[TransportationModeViewModel::class.java]
        }

        (description_title.background as GradientDrawable).setColor(DriveKitUI.colors.warningColor())
        description_title.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
        description_title.text = DKResource.convertToString(requireContext(), "dk_driverdata_transportation_mode_declaration_text")

        transportation_profile_title.normalText()
        transportation_profile_title.text = DKResource.convertToString(requireContext(), "dk_driverdata_transportation_mode_passenger_driver")

        comment_title.normalText()
        comment_title.text = DKResource.convertToString(requireContext(), "dk_driverdata_transportation_mode_declaration_comment")

        val editTextBackground = edit_text_comment.background as GradientDrawable
        editTextBackground.setStroke(4, DriveKitUI.colors.neutralColor())
        edit_text_comment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text_comment_error.visibility = if (viewModel.isCommentValid(s.toString())){
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        })
        text_comment_error.smallText(DriveKitUI.colors.criticalColor())
        text_comment_error.text = DKResource.convertToString(requireContext(), "dk_driverdata_transportation_mode_declaration_comment_error")

        button_validate.button()
        button_validate.text = DKResource.convertToString(requireContext(), "dk_common_validate")

        updateTransportationProfileVisibility()
        bindTransportationModeItems()
        bindTransportationProfileItems()
        viewModel.updateObserver.observe(this, { status ->
            hideProgressCircular()
            if (status != null){
                when (status){
                    TransportationModeUpdateStatus.NO_ERROR -> {
                        requireActivity().apply {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                    TransportationModeUpdateStatus.FAILED_TO_UPDATE_STATUS -> {
                        Toast.makeText(
                            requireContext(),
                            DKResource.convertToString(requireContext(), "dk_driverdata_failed_to_declare_transportation"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    TransportationModeUpdateStatus.COMMENT_TOO_LONG -> {
                        Toast.makeText(
                            requireContext(),
                            DKResource.convertToString(requireContext(), "dk_driverdata_transportation_mode_declaration_comment_error"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
        initDefaultValues()
    }

    private fun updateTransportationProfileVisibility() {
        if (viewModel.displayPassengerOption()) {
            container_transportation_profile.alpha = 0f
            container_transportation_profile.visibility = View.VISIBLE
            container_transportation_profile.animate().alpha(1f)
                .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        container_transportation_profile.visibility = View.VISIBLE
                    }
                })
        } else {
            container_transportation_profile.alpha = 1f
            container_transportation_profile.animate().alpha(0f)
                .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        container_transportation_profile.visibility = View.GONE
                    }
                })
        }
    }

    private fun bindTransportationModeItems() {
        for (item in viewModel.buildTransportationModes()) {
            val itemView: CircularButtonItemView? = view?.findViewById(getItemIdByTransportationMode(item))
            itemView?.let {
                transportationModesViews.add(it)
            }
        }
    }

    private fun bindTransportationProfileItems() {
        for (item in viewModel.buildTransportationProfiles()) {
            val itemView: CircularButtonItemView? = view?.findViewById(getItemIdByTransportationProfile(item))
            itemView?.let {
                transportationProfilesViews.add(it)
            }
        }
    }

    fun onTransportationModeClicked(view: View) {
        for (transportationModeItemView in transportationModesViews) {
            val transportationMode: TransportationMode? = getTransportationModeByItemId(transportationModeItemView.id)
            if (view.id == transportationModeItemView.id) {
                if (transportationMode != null) {
                    if (transportationMode != TransportationMode.CAR) {
                        viewModel.selectedProfileDriver = null
                    }
                    viewModel.selectedTransportationMode  = transportationMode
                    transportationModeItemView.setItemSelectedState(true)
                    transportation_mode_title.text = viewModel.buildSelectedTransportationModeTitle(requireContext())
                }
            } else {
                transportationModeItemView.setItemSelectedState(false)
            }
        }
        updateTransportationProfileVisibility()
    }

    fun onTransportationProfileClicked(view: View) {
        for (transportationProfileItemView in transportationProfilesViews) {
            if (view.id == transportationProfileItemView.id) {
                val transportationProfile: TransportationProfile? = getTransportationProfileByItemId(transportationProfileItemView.id)
                if (transportationProfile != null) {
                    viewModel.selectedProfileDriver = transportationProfile
                    transportationProfileItemView.setItemSelectedState(true)
                }
            } else {
                transportationProfileItemView.setItemSelectedState(false)
            }
        }
    }

    fun onValidate(){
        viewModel.comment = edit_text_comment.text.toString()
        if (viewModel.checkFieldsValidity()) {
            showProgressCircular()
            viewModel.updateInformations()
        } else {
            Toast.makeText(
                requireContext(),
                DKResource.convertToString(requireContext(), "dk_driverdata_failed_to_declare_transportation"),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initDefaultValues() {
        viewModel.trip?.let { trip ->
            val declaredTransportationMode = trip.declaredTransportationMode?.transportationMode
            if (declaredTransportationMode != null) {
                for (transportationViewItem in transportationModesViews) {
                    if (declaredTransportationMode === getTransportationModeByItemId(transportationViewItem.id)) {
                        viewModel.selectedTransportationMode = declaredTransportationMode
                        transportationViewItem.setItemSelectedState(true)
                    } else {
                        transportationViewItem.setItemSelectedState(false)
                    }
                }
                if (viewModel.displayPassengerOption()) {
                    updateTransportationProfileVisibility()
                    trip.declaredTransportationMode?.passenger?.let {
                        val transportationProfile = if (it) TransportationProfile.PASSENGER else TransportationProfile.DRIVER
                        for (declaredTransportationProfileItem in transportationProfilesViews) {
                            if (transportationProfile == getTransportationProfileByItemId(declaredTransportationProfileItem.id)) {
                                viewModel.selectedProfileDriver = transportationProfile
                                declaredTransportationProfileItem.setItemSelectedState(true)
                            } else {
                                declaredTransportationProfileItem.setItemSelectedState(false)
                            }
                        }
                    }
                }

                trip.declaredTransportationMode?.comment?.let {
                    edit_text_comment.setText(it)
                }
            }
            transportation_mode_title.text = viewModel.buildSelectedTransportationModeTitle(requireContext())
        }
    }

    private fun getItemIdByTransportationMode(transportationMode: TransportationMode): Int {
        return when (transportationMode) {
            TransportationMode.CAR -> R.id.transportation_mode_car
            TransportationMode.MOTO -> R.id.transportation_mode_motorcycle
            TransportationMode.TRUCK -> R.id.transportation_mode_truck
            TransportationMode.BUS -> R.id.transportation_mode_bus
            TransportationMode.TRAIN ->  R.id.transportation_mode_train
            TransportationMode.BOAT -> R.id.transportation_mode_boat
            TransportationMode.BIKE -> R.id.transportation_mode_bike
            TransportationMode.FLIGHT ->  R.id.transportation_mode_flight
            TransportationMode.SKIING -> R.id.transportation_mode_skiing
            TransportationMode.ON_FOOT -> R.id.transportation_mode_on_foot
            TransportationMode.IDLE -> R.id.transportation_mode_idle
            TransportationMode.OTHER -> R.id.transportation_mode_other
            else -> -1
        }
    }

    private fun getTransportationModeByItemId(itemId: Int): TransportationMode? {
        return when (itemId) {
            R.id.transportation_mode_car -> TransportationMode.CAR
            R.id.transportation_mode_motorcycle -> TransportationMode.MOTO
            R.id.transportation_mode_truck -> TransportationMode.TRUCK
            R.id.transportation_mode_bus -> TransportationMode.BUS
            R.id.transportation_mode_train -> TransportationMode.TRAIN
            R.id.transportation_mode_boat -> TransportationMode.BOAT
            R.id.transportation_mode_bike -> TransportationMode.BIKE
            R.id.transportation_mode_flight -> TransportationMode.FLIGHT
            R.id.transportation_mode_skiing -> TransportationMode.SKIING
            R.id.transportation_mode_on_foot -> TransportationMode.ON_FOOT
            R.id.transportation_mode_idle -> TransportationMode.IDLE
            R.id.transportation_mode_other -> TransportationMode.OTHER
            else -> null
        }
    }

    private fun getItemIdByTransportationProfile(transportationProfile: TransportationProfile): Int {
        return when (transportationProfile) {
            TransportationProfile.PASSENGER -> R.id.transportation_profile_passenger
            TransportationProfile.DRIVER -> R.id.transportation_profile_driver
        }
    }

    private fun getTransportationProfileByItemId(itemId: Int): TransportationProfile? {
        when (itemId) {
            R.id.transportation_profile_passenger -> return TransportationProfile.PASSENGER
            R.id.transportation_profile_driver -> return TransportationProfile.DRIVER
        }
        return null
    }

    private fun showProgressCircular() {
        progress_circular?.apply {
            animate()
            .alpha(1f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.VISIBLE
                }
            })
        }
    }

    private fun hideProgressCircular() {
        progress_circular?.apply {
            animate()
                .alpha(0f)
                .setDuration(200L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.GONE
                    }
                })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("itinId", itinId)
        super.onSaveInstanceState(outState)
    }
}
