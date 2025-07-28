package com.drivequant.drivekit.ui.driverpassengermode.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.component.CircularButtonItemView
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.driverdata.trip.TransportationModeUpdateStatus
import com.drivequant.drivekit.driverdata.trip.UpdateDriverPassengerModeStatus
import com.drivequant.drivekit.driverdata.trip.driverpassengermode.DriverPassengerMode
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.databinding.DkFragmentDriverPassengerModeBinding
import com.drivequant.drivekit.ui.driverpassengermode.viewmodel.DriverPassengerModeViewModel

internal class DriverPassengerModeFragment : Fragment() {

    private lateinit var viewModel: DriverPassengerModeViewModel
    private lateinit var itinId: String
    private var _binding: DkFragmentDriverPassengerModeBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    private val carOccupantRoleViews = mutableListOf<CircularButtonItemView>()
    private val transportationModesViews = mutableListOf<CircularButtonItemView>()

    companion object {
        fun newInstance(itinId: String): DriverPassengerModeFragment {
            val fragment = DriverPassengerModeFragment()
            fragment.itinId = itinId
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DkFragmentDriverPassengerModeBinding.inflate(inflater, container, false)
        binding.root.setDKStyle()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_trips_detail_transportation_mode), javaClass.simpleName)
        savedInstanceState?.getString("itinId")?.let{
            itinId = it
        }
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this,
                DriverPassengerModeViewModel.DriverPassengerModeViewModelFactory(itinId)
            )[DriverPassengerModeViewModel::class.java]
        }

        (binding.descriptionTitle.background as GradientDrawable).setColor(DKColors.warningColor)
        binding.descriptionTitle.normalText()

        binding.transportationProfileTitle.normalText()

        binding.commentTitle.normalText()

        val editTextBackground = binding.editTextComment.background as GradientDrawable
        editTextBackground.setStroke(4, DKColors.neutralColor)
        binding.editTextComment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.textCommentError.visibility = if (viewModel.isCommentValid(s.toString())){
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        })
        binding.textCommentError.smallText()
        bindTransportationModeItems()
        bindTransportationProfileItems()
        viewModel.updateCarOccupantRoleObserver.observe(viewLifecycleOwner) { status ->
            hideProgressCircular()
            if (status != null) {
                when (status) {
                    UpdateDriverPassengerModeStatus.SUCCESS -> displaySuccessMessage()
                    UpdateDriverPassengerModeStatus.FAILED_TO_UPDATE_MODE -> {
                        Toast.makeText(
                            requireContext(),
                            R.string.dk_driverdata_failed_to_declare_transportation, //TODO missing string resource
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    UpdateDriverPassengerModeStatus.COMMENT_TOO_LONG -> {
                        displayCommentTooLongMessage()
                    }

                    UpdateDriverPassengerModeStatus.USER_NOT_CONNECTED,
                    UpdateDriverPassengerModeStatus.INVALID_TRANSPORTATION_MODE,
                    UpdateDriverPassengerModeStatus.INVALID_ITINERARY_ID -> displayErrorMessage()
                }
            }
        }

        viewModel.updatePassengerTransportationModeObserver.observe(viewLifecycleOwner) { status ->
            hideProgressCircular()
            if (status != null) {
                when (status) {
                    TransportationModeUpdateStatus.NO_ERROR -> displaySuccessMessage()
                    TransportationModeUpdateStatus.FAILED_TO_UPDATE_STATUS -> displayErrorMessage()
                    TransportationModeUpdateStatus.COMMENT_TOO_LONG -> displayCommentTooLongMessage()
                }
            }
        }
        initDefaultValues()
    }

    //TODO should we display the message when we go back to a trip detail which has a declared data?
    private fun displaySuccessMessage() {
        binding.descriptionTitle.text =
            getString(R.string.dk_driverdata_ocupant_declaration_thanks)
    }

    private fun displayCommentTooLongMessage() {
        Toast.makeText(
            requireContext(),
            R.string.dk_driverdata_transportation_mode_declaration_comment_error,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun displayErrorMessage() {
        Toast.makeText(
            requireContext(),
            R.string.dk_driverdata_failed_to_declare_transportation, //TODO missing string resource
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        for (item in viewModel.buildDriverPassengerModes()) {
            val itemView: CircularButtonItemView? = view?.findViewById(getItemIdByTransportationProfile(item))
            itemView?.let {
                carOccupantRoleViews.add(it)
            }
        }
    }

    fun onTransportationModeClicked(view: View) {
        for (transportationModeItemView in transportationModesViews) {
            val transportationMode: TransportationMode? = getTransportationModeByItemId(transportationModeItemView.id)
            if (view.id == transportationModeItemView.id) {
                if (transportationMode != null) {
                    viewModel.selectTransportationMode(transportationMode)
                    transportationModeItemView.setItemSelectedState(true)
                }
            } else {
                transportationModeItemView.setItemSelectedState(false)
            }
        }
        clearCarOccupantViewsSelection()
    }

    fun onCarOccupantRoleClicked(view: View) {
        for (carOccupantRoleItemView in carOccupantRoleViews) {
            if (view.id == carOccupantRoleItemView.id) {
                getDriverPassengerModeByItemId(carOccupantRoleItemView.id)?.let { driverPassengerMode ->
                    viewModel.selectCarOccupantRole(driverPassengerMode)
                    carOccupantRoleItemView.setItemSelectedState(true)
                }
            } else {
                carOccupantRoleItemView.setItemSelectedState(false)
            }
        }
        clearTransportationModeViewsSelection()
    }

    private fun clearCarOccupantViewsSelection() {
        carOccupantRoleViews.forEach { it.setItemSelectedState(false) }
    }

    private fun clearTransportationModeViewsSelection() {
        transportationModesViews.forEach { it.setItemSelectedState(false) }
    }

    fun onChangeButtonClicked(){
        viewModel.comment = binding.editTextComment.text.toString()
        if (viewModel.checkFieldsValidity()) {
            showProgressCircular()
            viewModel.updateDriverPassengerMode()
        } else {
            //TODO should we disable the Validate button, when enable it once an item is selected?
            //TODO Missing error message
            displayErrorMessage()
        }
    }

    private fun initDefaultValues() {
        viewModel.trip?.let { trip ->
            trip.declaredTransportationMode?.let { declaredTransportationMode ->
                if (declaredTransportationMode.transportationMode == TransportationMode.CAR) {
                    val carOccupantRole = if (declaredTransportationMode.passenger == true) DriverPassengerMode.PASSENGER else DriverPassengerMode.DRIVER
                    for (occupantRoleItem in carOccupantRoleViews) {
                        if (carOccupantRole == getDriverPassengerModeByItemId(occupantRoleItem.id)) {
                            viewModel.selectedDriverPassengerMode = carOccupantRole
                            occupantRoleItem.setItemSelectedState(true)
                        } else {
                            occupantRoleItem.setItemSelectedState(false)
                        }
                    }
                } else {
                    for (transportationViewItem in transportationModesViews) {
                        if (declaredTransportationMode.transportationMode === getTransportationModeByItemId(transportationViewItem.id)) {
                            viewModel.selectedTransportationMode = declaredTransportationMode.transportationMode
                            transportationViewItem.setItemSelectedState(true)
                        } else {
                            transportationViewItem.setItemSelectedState(false)
                        }
                    }
                }

                trip.declaredTransportationMode?.comment?.let {
                    binding.editTextComment.setText(it)
                }
            }
        }
    }

    private fun getItemIdByTransportationMode(transportationMode: TransportationMode): Int =
        when (transportationMode) {
            TransportationMode.FLIGHT -> R.id.transportation_mode_flight
            TransportationMode.BUS -> R.id.transportation_mode_bus
            TransportationMode.TRAIN -> R.id.transportation_mode_train
            TransportationMode.BOAT -> R.id.transportation_mode_boat
            TransportationMode.BIKE -> R.id.transportation_mode_bike
            TransportationMode.ON_FOOT -> R.id.transportation_mode_on_foot
            else -> -1
        }

    private fun getTransportationModeByItemId(itemId: Int): TransportationMode? = when (itemId) {
        R.id.transportation_mode_flight -> TransportationMode.FLIGHT
        R.id.transportation_mode_bus -> TransportationMode.BUS
        R.id.transportation_mode_train -> TransportationMode.TRAIN
        R.id.transportation_mode_boat -> TransportationMode.BOAT
        R.id.transportation_mode_bike -> TransportationMode.BIKE
        R.id.transportation_mode_on_foot -> TransportationMode.ON_FOOT
        else -> null
    }

    private fun getItemIdByTransportationProfile(transportationProfile: DriverPassengerMode): Int =
        when (transportationProfile) {
            DriverPassengerMode.PASSENGER -> R.id.transportation_profile_passenger
            DriverPassengerMode.DRIVER -> R.id.transportation_profile_driver
        }

    private fun getDriverPassengerModeByItemId(itemId: Int): DriverPassengerMode? = when (itemId) {
        R.id.transportation_profile_passenger -> DriverPassengerMode.PASSENGER
        R.id.transportation_profile_driver -> DriverPassengerMode.DRIVER
        else -> null
    }

    private fun showProgressCircular() {
        binding.progressCircular.apply {
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
        binding.progressCircular.apply {
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
        if (this::itinId.isInitialized) {
            outState.putString("itinId", itinId)
        }
        super.onSaveInstanceState(outState)
    }
}
