package com.drivequant.drivekit.ui.trips.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.triplist.DKTripList
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.DKHeader
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.extension.updateSubMenuItemFont
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.databinding.FragmentTripsListBinding
import com.drivequant.drivekit.ui.databinding.ViewContentNoTripsBinding
import com.drivequant.drivekit.ui.extension.toDKTripList
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfiguration
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType
import com.drivequant.drivekit.ui.trips.viewmodel.TripsListViewModel

class TripsListFragment : Fragment() {
    private lateinit var viewModel: TripsListViewModel
    private lateinit var tripsList: DKTripList
    private var shouldSyncTrips = true
    private lateinit var synchronizationType: SynchronizationType
    private var _binding: FragmentTripsListBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripsListBinding.inflate(inflater, container, false)
        binding.root.setDKStyle(Color.WHITE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_trips_list), javaClass.simpleName)
        activity?.title = context?.getString(R.string.dk_driverdata_trips_list_title)

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(
                this,
                TripsListViewModel.TripsListViewModelFactory(TripListConfiguration.MOTORIZED())
            )[TripsListViewModel::class.java]
        }

        viewModel.shouldShowFilterMenuOption.observe(viewLifecycleOwner) {
            setHasOptionsMenu(it)
        }

        viewModel.tripsData.observe(viewLifecycleOwner) {
            viewModel.getFilterItems(requireContext())
            val noTripsContainerBinding = ViewContentNoTripsBinding.bind(binding.root.findViewById(R.id.no_trips_container))
            if (viewModel.filteredTrips.isEmpty()) {
                displayNoTrips(noTripsContainerBinding)
            } else {
                displayTripsList(noTripsContainerBinding)
            }
            if (!this::tripsList.isInitialized) {
                tripsList = object : DKTripList {
                    override fun onTripClickListener(itinId: String) {
                        TripDetailActivity.launchActivity(
                            requireActivity(),
                            itinId,
                            tripListConfigurationType = TripListConfigurationType.getType(viewModel.tripListConfiguration),
                            parentFragment = this@TripsListFragment
                        )
                    }

                    override fun getTripData(): TripData = DriverDataUI.tripData
                    override fun getTripsList(): List<DKTripListItem> = it.toDKTripList()
                    override fun getCustomHeader(): DKHeader? = DriverDataUI.customHeader
                    override fun getHeaderDay(): HeaderDay = DriverDataUI.headerDay
                    override fun getDayTripDescendingOrder(): Boolean =
                        DriverDataUI.dayTripDescendingOrder

                    override fun canSwipeToRefresh(): Boolean = true
                    override fun onSwipeToRefresh() {
                        binding.filterView.spinner.setSelection(0)
                        updateTrips()
                    }
                }
            }
            binding.textViewTripsSynthesis.text = viewModel.getTripSynthesisText(requireContext())
            binding.dkTripsListView.configure(tripsList)
            if (synchronizationType == SynchronizationType.CACHE && shouldSyncTrips) {
                shouldSyncTrips = false
                updateTrips()
            }
        }

        viewModel.syncTripsError.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(
                    context,
                    context?.getString(R.string.dk_driverdata_failed_to_sync_trips),
                    Toast.LENGTH_LONG
                ).show()
            }
            binding.dkTripsListView.updateSwipeRefreshTripsVisibility(false)
        }

        synchronizationType = if (viewModel.hasLocalTrips()) {
            SynchronizationType.CACHE
        } else {
            SynchronizationType.DEFAULT
        }
        updateTrips(synchronizationType)
        initFilter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.dk_trip_list_menu_bar, menu)
        context?.let { context ->
            menu.forEach {
                it.icon?.mutate()?.tintDrawable(DriveKitUI.colors.fontColorOnPrimaryColor())
                it.subMenu?.updateSubMenuItemFont(context)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.trips_vehicle -> {
                viewModel.filterTrips(TripListConfiguration.MOTORIZED())
                binding.filterView.spinner.setSelection(0, false)
                true
            }
            R.id.trips_alternative -> {
                viewModel.filterTrips(TripListConfiguration.ALTERNATIVE())
                binding.filterView.spinner.setSelection(0, false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initFilter() {
        binding.filterView.spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long) {
                val itemId = viewModel.filterItems[position].getItemId()
                when (viewModel.tripListConfiguration){
                    is TripListConfiguration.MOTORIZED -> {
                        viewModel.filterTrips(TripListConfiguration.MOTORIZED(itemId as String?))
                    }
                    is TripListConfiguration.ALTERNATIVE -> {
                        viewModel.filterTrips(TripListConfiguration.ALTERNATIVE(itemId as TransportationMode?))
                    }
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun configureFilter() {
        if (viewModel.getFilterVisibility()) {
            binding.filterView.setItems(viewModel.filterItems)
            binding.filterView.visibility = View.VISIBLE
        } else {
            binding.filterView.visibility = View.GONE
        }
    }

    private fun updateTrips(synchronizationType: SynchronizationType = SynchronizationType.DEFAULT) {
        binding.dkTripsListView.updateSwipeRefreshTripsVisibility(true)
        viewModel.fetchTrips(synchronizationType)
    }

    private fun displayNoTrips(noTripsContainerBinding: ViewContentNoTripsBinding) {
        configureFilter()
        noTripsContainerBinding.noTripsRecordedText.text = getString(viewModel.getNoTripsTextResId())
        noTripsContainerBinding.root.visibility = View.VISIBLE
        binding.dkTripsListView.setTripsListEmptyView(noTripsContainerBinding.root)
        noTripsContainerBinding.noTripsRecordedText.normalText()

        noTripsContainerBinding.noTrips.apply {
            view?.resources?.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_margin_half)?.let { cornerRadius ->
                roundCorners(cornerRadius, cornerRadius, cornerRadius, cornerRadius)
            }
            DrawableCompat.setTint(this.background, DriveKitUI.colors.neutralColor())
        }
        binding.dkTripsListView.updateSwipeRefreshTripsVisibility(false)
    }

    private fun displayTripsList(noTripsContainerBinding: ViewContentNoTripsBinding) {
        configureFilter()
        noTripsContainerBinding.root.visibility = View.GONE
        binding.dkTripsListView.visibility = View.VISIBLE
        binding.dkTripsListView.updateSwipeRefreshTripsVisibility(false)
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == TripDetailActivity.UPDATE_TRIPS_REQUEST_CODE) {
            updateTrips(SynchronizationType.CACHE)
            binding.filterView.spinner.setSelection(0, false)
        }
    }
}
