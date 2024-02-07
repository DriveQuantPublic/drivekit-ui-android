package com.drivequant.drivekit.ui.transportationmode.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
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
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.CircularButtonItemView
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.driverdata.trip.TransportationModeUpdateStatus
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.databinding.DkFragmentTransportationModeBinding
import com.drivequant.drivekit.ui.transportationmode.viewmodel.TransportationModeViewModel
import com.drivequant.drivekit.ui.transportationmode.viewmodel.TransportationProfile

internal class TransportationModeFragment : Fragment() {

    private lateinit var viewModel: TransportationModeViewModel
    private lateinit var itinId: String
    private var _binding: DkFragmentTransportationModeBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

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
        _binding = DkFragmentTransportationModeBinding.inflate(inflater, container, false)
        binding.root.setDKStyle()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_trips_detail_transportation_mode), javaClass.simpleName)
        savedInstanceState?.getString("itinId")?.let{
            itinId = it
        }
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this,
                TransportationModeViewModel.TransportationModeViewModelFactory(itinId)
            )[TransportationModeViewModel::class.java]
        }

        (binding.descriptionTitle.background as GradientDrawable).setColor(DriveKitUI.colors.warningColor())
        binding.descriptionTitle.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
        binding.descriptionTitle.setText(R.string.dk_driverdata_transportation_mode_declaration_text)

        binding.transportationProfileTitle.normalText()
        binding.transportationProfileTitle.setText(R.string.dk_driverdata_transportation_mode_passenger_driver)

        binding.commentTitle.normalText()
        binding.commentTitle.setText(R.string.dk_driverdata_transportation_mode_declaration_comment)

        val editTextBackground = binding.editTextComment.background as GradientDrawable
        editTextBackground.setStroke(4, DriveKitUI.colors.neutralColor())
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
        binding.textCommentError.smallText(DriveKitUI.colors.criticalColor())
        binding.textCommentError.setText(R.string.dk_driverdata_transportation_mode_declaration_comment_error)

        binding.buttonValidate.button()
        binding.buttonValidate.setText(com.drivequant.drivekit.common.ui.R.string.dk_common_validate)

        updateTransportationProfileVisibility()
        bindTransportationModeItems()
        bindTransportationProfileItems()
        viewModel.updateObserver.observe(viewLifecycleOwner) { status ->
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
                            R.string.dk_driverdata_failed_to_declare_transportation,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    TransportationModeUpdateStatus.COMMENT_TOO_LONG -> {
                        Toast.makeText(
                            requireContext(),
                            R.string.dk_driverdata_transportation_mode_declaration_comment_error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        initDefaultValues()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateTransportationProfileVisibility() {
        if (viewModel.displayPassengerOption()) {
            binding.containerTransportationProfile.alpha = 0f
            binding.containerTransportationProfile.visibility = View.VISIBLE
            binding.containerTransportationProfile.animate().alpha(1f)
                .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        binding.containerTransportationProfile.visibility = View.VISIBLE
                    }
                })
        } else {
            binding.containerTransportationProfile.alpha = 1f
            binding.containerTransportationProfile.animate().alpha(0f)
                .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        binding.containerTransportationProfile.visibility = View.GONE
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
                    binding.transportationModeTitle.text = viewModel.buildSelectedTransportationModeTitle(requireContext())
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
        viewModel.comment = binding.editTextComment.text.toString()
        if (viewModel.checkFieldsValidity()) {
            showProgressCircular()
            viewModel.updateInformations()
        } else {
            Toast.makeText(
                requireContext(),
                R.string.dk_driverdata_failed_to_declare_transportation,
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
                    binding.editTextComment.setText(it)
                }
            }
            binding.transportationModeTitle.text = viewModel.buildSelectedTransportationModeTitle(requireContext())
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
        outState.putString("itinId", itinId)
        super.onSaveInstanceState(outState)
    }
}
