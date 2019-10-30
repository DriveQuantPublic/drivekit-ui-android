package com.drivequant.drivekit.ui.tripdetail.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.text.HtmlCompat
import android.support.v4.view.ViewPager
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.databaseutils.entity.TripAdvice
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.TripDetailViewConfig
import com.drivequant.drivekit.ui.TripsViewConfig
import com.drivequant.drivekit.ui.extension.formatHeaderDay
import com.drivequant.drivekit.ui.tripdetail.adapter.TripDetailFragmentPagerAdapter
import com.drivequant.drivekit.ui.tripdetail.viewholder.TripGoogleMapViewHolder
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModelFactory
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.fragment_trip_detail.*

class TripDetailFragment : Fragment() {

    private lateinit var viewModel : TripDetailViewModel
    private lateinit var tripDetailViewConfig: TripDetailViewConfig
    private lateinit var tripsViewConfig: TripsViewConfig

    private lateinit var itinId: String
    private var openAdvice: Boolean = false

    private lateinit var tripMapViewHolder: TripGoogleMapViewHolder
    private var mapFragment: SupportMapFragment? = null

    private lateinit var viewContentTrip: View

    companion object {
        fun newInstance(itinId: String,
                        tripDetailViewConfig: TripDetailViewConfig,
                        tripsViewConfig: TripsViewConfig,
                        openAdvice: Boolean = false): TripDetailFragment {
            val fragment = TripDetailFragment()
            fragment.itinId = itinId
            fragment.openAdvice = openAdvice
            fragment.tripDetailViewConfig = tripDetailViewConfig
            fragment.tripsViewConfig = tripsViewConfig
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_trip_detail, container, false)
        viewContentTrip = rootView.findViewById(R.id.container_trip)
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.trip_menu_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.trip_delete -> {
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.app_name))
                    .setMessage(tripDetailViewConfig.deleteText)
                    .setCancelable(true)
                    .setPositiveButton(tripDetailViewConfig.okText) { dialog, _ ->
                        showProgressCircular()
                        deleteTrip()
                    }
                    .setNegativeButton(tripDetailViewConfig.cancelText) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getSerializable("config") as TripsViewConfig?)?.let{
            tripsViewConfig = it
        }
        (savedInstanceState?.getSerializable("detailConfig") as TripDetailViewConfig?)?.let{
            tripDetailViewConfig = it
        }
        savedInstanceState?.getString("itinId")?.let{
            itinId = it
        }
        progress_circular.visibility = View.VISIBLE
        viewModel = ViewModelProviders.of(this,
            TripDetailViewModelFactory(
                itinId,
                tripDetailViewConfig.mapItems
            )
        ).get(TripDetailViewModel::class.java)
        activity?.title = tripDetailViewConfig.viewTitleText
        container_header_trip.setBackgroundColor(tripsViewConfig.primaryColor)
        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as? SupportMapFragment
        if (tripDetailViewConfig.enableDeleteTrip) {
            setHasOptionsMenu(true)
        }
        loadTripData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("config", tripsViewConfig)
        outState.putSerializable("detailConfig", tripDetailViewConfig)
        outState.putString("itinId", itinId)
        super.onSaveInstanceState(outState)
    }

    private fun deleteTrip(){
        viewModel.deleteTripObserver.observe(this, Observer {
            hideProgressCircular()
            if (it != null){
                if (it){
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.app_name))
                        .setMessage(tripDetailViewConfig.tripDeleted)
                        .setCancelable(false)
                        .setPositiveButton(tripDetailViewConfig.okText) { dialog, _ ->
                            dialog.dismiss()
                            activity?.onBackPressed()
                        }
                        .show()
                } else {
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.app_name))
                        .setMessage(tripDetailViewConfig.failedToDeleteTrip)
                        .setCancelable(true)
                        .setPositiveButton(tripDetailViewConfig.okText) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        })
        viewModel.deleteTrip()
    }

    private fun loadTripData(){
        viewModel.tripEventsObserver.observe(this, Observer {
            if (viewModel.events.isNotEmpty()) {
                setMapController()
                setViewPager()
                setHeaderSummary()
                hideProgressCircular()
            } else {
                displayErrorMessageAndGoBack(R.string.dk_trip_detail_data_error)
            }
        })
        viewModel.unScoredTrip.observe(this, Observer{ routeAvailable ->
            routeAvailable?.let {
                if (it){
                    setMapController()
                }else{
                    displayErrorMessageAndGoBack(R.string.dk_trip_detail_get_road_failed, false)
                }
                setUnScoredTripFragment()
                setHeaderSummary()
            }

            hideProgressCircular()
        })
        viewModel.noRoute.observe(this, Observer{
            setViewPager()
            hideProgressCircular()
            setHeaderSummary()
            displayErrorMessageAndGoBack(R.string.dk_trip_detail_get_road_failed, false)
        })
        viewModel.noData.observe(this, Observer{
            displayErrorMessageAndGoBack(R.string.dk_trip_detail_data_error)
        })
        viewModel.fetchTripData(requireContext())
    }

    private fun displayErrorMessageAndGoBack(stringResId: Int, goBack: Boolean = true){
        AlertDialog.Builder(context)
            .setTitle(this.getString(R.string.app_name))
            .setMessage(stringResId)
            .setCancelable(false)
            .setPositiveButton(tripDetailViewConfig.okText) { dialog, _ ->
                dialog.dismiss()
                if (goBack){
                    activity?.onBackPressed()
                }
            }
            .show()
    }

    private fun displayAdviceFromTripInfo(){
        if (openAdvice){
            val index = viewModel.getAdviceMapItemIndex(tripsViewConfig.tripInfo)
            if (index > -1) {
                view_pager.currentItem = index
                viewModel.trip?.let {
                    val tripAdvice = viewModel.configurableMapItems[index].getAdvice(it.tripAdvices)
                    displayAdvice(tripAdvice)
                }
            }
        }
    }

    fun displayAdvice(tripAdvice: TripAdvice?){
        tripAdvice?.let {
            val adviceView = View.inflate(context, R.layout.view_trip_advice_message, null)
            val headerText = adviceView.findViewById<TextView>(R.id.text_view_advice_header)
            headerText.text = tripAdvice.title
            headerText.setBackgroundColor(tripsViewConfig.primaryColor)
            tripAdvice.message?.let {
                adviceView.findViewById<TextView>(R.id.text_view_advice_content).text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }

            AlertDialog.Builder(context)
                .setView(adviceView)
                .setPositiveButton(tripDetailViewConfig.okText) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setMapController(){
        if (mapFragment != null) {
            mapFragment!!.getMapAsync {
                    googleMap -> mapFragment
                tripMapViewHolder =
                    TripGoogleMapViewHolder(
                        this,
                        viewContentTrip,
                        viewModel,
                        tripDetailViewConfig,
                        googleMap
                    )
                tripMapViewHolder.traceRoute(viewModel.displayMapItem.value)
                tripMapViewHolder.updateCamera()
                displayAdviceFromTripInfo()
                hideProgressCircular()
            }
        }
        center_button.setOnClickListener {
            tripMapViewHolder.updateCamera()
        }
    }

    private fun setUnScoredTripFragment(){
        view_pager.visibility = View.INVISIBLE
        unscored_fragment.visibility = View.VISIBLE
        childFragmentManager.beginTransaction()
            .replace(R.id.unscored_fragment, UnscoredTripFragment.newInstance(
                viewModel.trip,
                tripsViewConfig,
                tripDetailViewConfig
            ))
            .commit()
    }

    private fun setViewPager(){
        view_pager.adapter =
            TripDetailFragmentPagerAdapter(
                childFragmentManager,
                viewModel,
                tripDetailViewConfig,
                tripsViewConfig
            )
        tab_layout.setupWithViewPager(view_pager)
        for ((index, mapItem) in viewModel.configurableMapItems.withIndex()){
            tab_layout.getTabAt(index)?.let {
                val icon = ImageView(requireContext())
                ContextCompat.getDrawable(requireContext(), mapItem.getImageResource())?.let { drawable ->
                    DrawableCompat.setTint(drawable, tripsViewConfig.primaryColor)
                    icon.setImageDrawable(drawable)
                }
                it.customView = icon
                val sizePx = (it.parent.height * 0.66).toInt()
                it.customView?.layoutParams = LinearLayout.LayoutParams(sizePx,sizePx)
            }
        }
        DrawableCompat.setTint(center_button.drawable, tripsViewConfig.primaryColor)
        view_pager.addOnPageChangeListener(
            DetailOnPageChangeListener(
                viewModel
            )
        )
    }

    private fun setHeaderSummary(){
        trip_date.text = viewModel.trip?.endDate?.formatHeaderDay()?.capitalize()
        trip_distance.text = tripDetailViewConfig.headerSummary.text(requireContext(), viewModel.trip!!)
    }

    private fun showProgressCircular() {
        progress_circular.animate()
            .alpha(255f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    progress_circular?.visibility = View.VISIBLE
                }
            })
    }

    private fun hideProgressCircular() {
        progress_circular.animate()
            .alpha(0f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    progress_circular?.visibility = View.GONE
                }
            })
    }

    private class DetailOnPageChangeListener(private var tripDetailViewModel: TripDetailViewModel) : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            tripDetailViewModel.changeMapItem(position)
        }
    }
}


