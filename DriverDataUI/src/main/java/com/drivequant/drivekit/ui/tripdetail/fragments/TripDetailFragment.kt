package com.drivequant.drivekit.ui.tripdetail.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.analytics.DKAnalyticsEvent
import com.drivequant.drivekit.common.ui.analytics.DKAnalyticsEventKey
import com.drivequant.drivekit.common.ui.extension.capitalizeFirstLetter
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.databinding.FragmentTripDetailBinding
import com.drivequant.drivekit.ui.extension.toDKTripItem
import com.drivequant.drivekit.ui.tripdetail.adapter.TripDetailFragmentPagerAdapter
import com.drivequant.drivekit.ui.tripdetail.viewholder.TripGoogleMapViewHolder
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKMapItem
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapItem
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModelFactory
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfiguration
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.tabs.TabLayout
import java.util.Locale

class TripDetailFragment : Fragment() {

    private lateinit var viewModel: TripDetailViewModel

    private var itinId: String? = null
    private var tripListConfiguration: TripListConfiguration = TripListConfiguration.MOTORIZED()
    private var openAdvice: Boolean = false

    private var adviceAlertDialog: AlertDialog? = null
    private var feedbackAlertDialog: AlertDialog? = null

    private lateinit var tripMapViewHolder: TripGoogleMapViewHolder
    private var mapFragment: SupportMapFragment? = null

    private var _binding: FragmentTripDetailBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    companion object {
        fun newInstance(
            itinId: String,
            openAdvice: Boolean = false,
            tripListConfigurationType: TripListConfigurationType = TripListConfigurationType.MOTORIZED
        ): TripDetailFragment {
            DriveKitUI.analyticsListener?.trackEvent(
                DKAnalyticsEvent.TRIP_OPEN,
                mapOf(DKAnalyticsEventKey.ITIN_ID to itinId)
            )

            val fragment = TripDetailFragment()
            fragment.itinId = itinId
            fragment.openAdvice = openAdvice
            fragment.tripListConfiguration = tripListConfigurationType.getTripListConfiguration()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripDetailBinding.inflate(inflater, container, false)
        binding.root.setDKStyle(android.R.color.white)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.dk_trip_menu_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.trip_delete -> {
                context?.let {
                 val alert = DKAlertDialog.LayoutBuilder().init(it)
                        .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                        .cancelable(true)
                        .positiveButton(positiveListener = { _, _ ->
                                showProgressCircular()
                                deleteTrip() })
                        .negativeButton()
                        .show()

                    val title = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
                    val description = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)

                    title?.text = getString(R.string.app_name)
                    description?.text = getString(R.string.dk_driverdata_confirm_delete_trip)
                    title?.headLine1()
                    description?.normalText()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_trips_detail), javaClass.simpleName)
        savedInstanceState?.getString("itinId")?.let{
            this.itinId = it
        }

        val itinId = this.itinId
        if (itinId == null) {
            activity?.finish()
            return
        }

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this,
                TripDetailViewModelFactory(itinId, tripListConfiguration)
            )[TripDetailViewModel::class.java]
        }
        binding.progressCircular.visibility = View.VISIBLE
        activity?.title = context?.getString(R.string.dk_driverdata_trip_detail_title)
        binding.centerButton.setColorFilter(DKColors.primaryColor)

        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as? SupportMapFragment
        if (DriverDataUI.enableDeleteTrip) {
            setHasOptionsMenu(true)
        }
        loadTripData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        this.itinId?.let {
            outState.putString("itinId", it)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun deleteTrip() {
        viewModel.deleteTripObserver.observe(this) {
            hideProgressCircular()
            context?.let { context ->
                if (it != null) {
                    val alert = DKAlertDialog.LayoutBuilder()
                        .init(context)
                        .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                        .positiveButton(positiveListener = { dialog, _ ->
                            dialog.dismiss()
                            val data = Intent()
                            requireActivity().apply {
                                setResult(RESULT_OK, data)
                                finish()
                            }
                        })
                        .cancelable(false)
                        .show()

                    val title = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
                    val description = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)
                    title?.text = getString(R.string.app_name)
                    description?.text = if (it) {
                        getString(R.string.dk_driverdata_trip_deleted)
                    } else {
                        getString(R.string.dk_driverdata_failed_to_delete_trip)
                    }
                    title?.headLine1()
                    description?.normalText()
                }
            }
        }
        viewModel.deleteTrip()
    }

    private fun sendTripAdviceFeedback(
        mapItem: DKMapItem,
        evaluation: Boolean,
        feedback: Int,
        comment: String? = null) {
        viewModel.sendAdviceFeedbackObserver.observe(viewLifecycleOwner) { status ->
            hideProgressCircular()
            if (status != null) {
                adviceAlertDialog?.hide()
                Toast.makeText(
                    context,
                    if (status) context?.getString(R.string.dk_driverdata_advice_feedback_success) else context?.getString(
                        R.string.dk_driverdata_advice_feedback_error
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        viewModel.sendTripAdviceFeedback(mapItem, evaluation, feedback, comment)
    }

    private fun loadTripData() {
        viewModel.tripEventsObserver.observe(viewLifecycleOwner) {
            if (viewModel.events.isNotEmpty()) {
                setMapController()
                setViewPager()
                setHeaderSummary()
                hideProgressCircular()
            } else {
                displayErrorMessageAndGoBack(R.string.dk_driverdata_trip_detail_data_error)
            }
        }
        viewModel.unScoredTrip.observe(viewLifecycleOwner) { routeAvailable ->
            routeAvailable?.let {
                if (it) {
                    setMapController()
                } else {
                    displayErrorMessageAndGoBack(
                        R.string.dk_driverdata_trip_detail_get_road_failed,
                        false
                    )
                }
                setUnScoredTripFragment()
                setHeaderSummary()
            }

            hideProgressCircular()
        }
        viewModel.noRoute.observe(viewLifecycleOwner) {
            setViewPager()
            hideProgressCircular()
            setHeaderSummary()
            displayErrorMessageAndGoBack(R.string.dk_driverdata_trip_detail_get_road_failed, false)
        }
        viewModel.noData.observe(viewLifecycleOwner) {
            displayErrorMessageAndGoBack(R.string.dk_driverdata_trip_detail_data_error)
        }
        viewModel.reverseGeocoderObserver.observe(viewLifecycleOwner) {
            if (it) {
                requireActivity().setResult(RESULT_OK, Intent())
            }
        }
        viewModel.fetchTripData(requireContext())
    }

    private fun displayErrorMessageAndGoBack(stringResId: Int, goBack: Boolean = true) {
        AlertDialog.Builder(context, com.drivequant.drivekit.common.ui.R.style.DKAlertDialog)
            .setTitle(this.getString(R.string.app_name))
            .setMessage(stringResId)
            .setCancelable(false)
            .setPositiveButton(context?.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_ok)) { dialog, _ ->
                dialog.dismiss()
                if (goBack){
                    activity?.onBackPressed()
                }
            }
            .show()
    }

    private fun displayAdviceFromTripInfo() {
        if (openAdvice) {
            val index = viewModel.getFirstMapItemIndexWithAdvice()
            if (index > -1) {
                binding.viewPager.currentItem = index
                viewModel.trip?.let {
                    displayAdvice(viewModel.configurableMapItems[index])
                }
            }
        }
    }

    fun displayAdvice(mapItem: DKMapItem) {
        if (viewModel.shouldDisplayAdvice(mapItem)) {
            val adviceView = View.inflate(context, R.layout.view_trip_advice_message, null)
            val headerText = adviceView.findViewById<TextView>(R.id.text_view_advice_header)
            val feedbackButtonsLayout = adviceView.findViewById<LinearLayout>(R.id.linear_layout_advice_feedback)
            val builder = AlertDialog.Builder(context, com.drivequant.drivekit.common.ui.R.style.DKAlertDialog).setView(adviceView)

            headerText.apply {
                text = viewModel.getAdviceTitle(mapItem)
                setTypeface(DriveKitUI.primaryFont(context), typeface.style)
            }
            viewModel.getAdviceMessage(mapItem)?.let {
                adviceView.findViewById<TextView>(R.id.text_view_advice_content).apply {
                    setTypeface(DriveKitUI.primaryFont(context), typeface.style)
                    text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
                }
            }

            if (viewModel.shouldDisplayFeedbackButtons(mapItem)) {
                val disagreeButton = adviceView.findViewById<LinearLayout>(R.id.linear_layout_advice_negative)
                val disagreeText = adviceView.findViewById<TextView>(R.id.advice_disagree_textview)
                val disagreeImage = adviceView.findViewById<ImageView>(R.id.advice_disagree_image)

                val agreeButton = adviceView.findViewById<LinearLayout>(R.id.linear_layout_advice_positive)
                val agreeText = adviceView.findViewById<TextView>(R.id.advice_agree_textview)
                val agreeImage = adviceView.findViewById<ImageView>(R.id.advice_agree_image)

                disagreeText.headLine2()
                agreeText.headLine2()
                agreeImage.drawable.tintDrawable(DKColors.primaryColor)

                disagreeText.text = context?.getString(R.string.dk_driverdata_advice_disagree)
                disagreeImage.drawable.tintDrawable(DKColors.primaryColor)
                disagreeButton.setOnClickListener {
                    displayAdviceFeedback(mapItem)
                }

                agreeText.text = context?.getString(R.string.dk_driverdata_advice_agree)
                agreeButton.setOnClickListener {
                    showProgressCircular()
                    sendTripAdviceFeedback(mapItem, true, 0)
                }
                feedbackButtonsLayout.visibility = View.VISIBLE
            } else {
                builder.setPositiveButton(context?.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_ok)) { dialog, _ ->
                    dialog.dismiss()
                }
            }
            adviceAlertDialog = builder.show()
            adviceAlertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                setTextColor(DKColors.secondaryColor)
                setTypeface(DriveKitUI.primaryFont(context), typeface.style)
            }
        }
    }

    fun displayTripPassenger() {
        DriverDataUI.startTripListActivity(requireContext())
    }

    private fun displayAdviceFeedback(mapItem: DKMapItem) {
        val feedbackView = View.inflate(context, R.layout.view_trip_advice_feedback, null)
        FontUtils.overrideFonts(context,feedbackView)
        val header = feedbackView.findViewById<TextView>(R.id.alert_dialog_trip_feedback_header)
        val radioGroup = feedbackView.findViewById<RadioGroup>(R.id.radio_group_trip_feedback)

        header.text = context?.getString(R.string.dk_driverdata_advice_feedback_disagree_title)?.uppercase(Locale.getDefault())
        feedbackView.findViewById<TextView>(R.id.alert_dialog_feedback_text).hint = context?.getString(R.string.dk_driverdata_advice_feedback_disagree_desc)
        feedbackView.findViewById<AppCompatRadioButton>(R.id.radio_button_choice_01).text = context?.getString(R.string.dk_driverdata_advice_feedback_01)
        feedbackView.findViewById<AppCompatRadioButton>(R.id.radio_button_choice_02).text = context?.getString(R.string.dk_driverdata_advice_feedback_02)
        feedbackView.findViewById<AppCompatRadioButton>(R.id.radio_button_choice_03).text = context?.getString(R.string.dk_driverdata_advice_feedback_03)
        feedbackView.findViewById<AppCompatRadioButton>(R.id.radio_button_choice_04).text = context?.getString(R.string.dk_driverdata_advice_feedback_04)
        feedbackView.findViewById<AppCompatRadioButton>(R.id.radio_button_choice_05).text = context?.getString(R.string.dk_driverdata_advice_feedback_05)
        feedbackView.findViewById<EditText>(R.id.edit_text_feedback).isEnabled = false

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            this.feedbackAlertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
            handleClassicFeedbackAnswer(checkedId, feedbackView)
        }

        val builder = AlertDialog.Builder(context, com.drivequant.drivekit.common.ui.R.style.DKAlertDialog)
            .setView(feedbackView)
            .setNegativeButton(context?.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_cancel)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(context?.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_ok)) { _, _ ->
                if (radioGroup.checkedRadioButtonId == R.id.radio_button_choice_05 && feedbackView.findViewById<EditText>(R.id.edit_text_feedback).text.isEmpty()) {
                    context?.let {
                        Toast.makeText(it, com.drivequant.drivekit.common.ui.R.string.dk_common_error_empty_field, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    buildFeedbackData(mapItem, feedbackView, radioGroup)
                }
            }

        feedbackAlertDialog = builder.show()
        feedbackAlertDialog?.apply {
            getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                isEnabled = false
                setTypeface(DriveKitUI.primaryFont(context), typeface.style)
            }
            getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                setTextColor(DKColors.secondaryColor)
                setTypeface(DriveKitUI.primaryFont(context), typeface.style)
            }
        }
    }

    private fun handleClassicFeedbackAnswer(checkedId: Int, feedbackView: View) {
        if (checkedId == R.id.radio_button_choice_05) {
            feedbackAlertDialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            val scrollView = feedbackView.findViewById<ScrollView>(R.id.scrollView_feedback)
            val editText = feedbackView.findViewById<EditText>(R.id.edit_text_feedback)
            editText.apply {
                isEnabled = true
                requestFocus()
                setOnClickListener {
                    scrollView.smoothScrollTo(0, scrollView.bottom)
                }
            }
            feedbackAlertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
                ?.apply {
                    setTextColor(DKColors.secondaryColor)
                    setTypeface(DriveKitUI.primaryFont(context), typeface.style)
                }
        } else {
            feedbackView.findViewById<EditText>(R.id.edit_text_feedback).isEnabled = false
        }
    }

    private fun buildFeedbackData(mapItem: DKMapItem, feedbackView: View, radioGroup: RadioGroup) {
        showProgressCircular()
        var comment: String? = null
        val feedback = when (radioGroup.checkedRadioButtonId) {
            R.id.radio_button_choice_01 -> 1
            R.id.radio_button_choice_02 -> 2
            R.id.radio_button_choice_03 -> 3
            R.id.radio_button_choice_04 -> 4
            R.id.radio_button_choice_05 -> 5
            else -> 0
        }
        if (feedback == 5) {
            comment = feedbackView.findViewById<EditText>(R.id.edit_text_feedback).text.toString()
        }

        sendTripAdviceFeedback(mapItem, false, feedback, comment)
    }

    private fun setMapController() {
        mapFragment?.getMapAsync {
                googleMap -> mapFragment
            tripMapViewHolder =
                TripGoogleMapViewHolder(
                    this,
                    binding.containerTrip,
                    viewModel,
                    googleMap
                )
            tripMapViewHolder.traceRoute(viewModel.displayMapItem.value)
            tripMapViewHolder.updateCamera()
            displayAdviceFromTripInfo()
            hideProgressCircular()
        }
        binding.centerButton.setOnClickListener {
            if (this::tripMapViewHolder.isInitialized) {
                tripMapViewHolder.updateCamera()
            }
        }
    }

    private fun setUnScoredTripFragment() {
        binding.viewPager.visibility = View.INVISIBLE
        binding.unscoredFragment.visibility = View.VISIBLE

        val fragment = DriverDataUI.customMapItem?.let { item ->
            if (item.overrideShortTrip()) {
                setViewPager()
                item.getFragment(viewModel.trip, viewModel)
            } else {
                UnscoredTripFragment.newInstance(
                    viewModel.trip
                )
            }
        } ?: run {
            UnscoredTripFragment.newInstance(
                viewModel.trip
            )
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.unscored_fragment, fragment)
            .commit()
    }

    private fun setViewPager() {
        binding.viewPager.adapter =
            TripDetailFragmentPagerAdapter(
                childFragmentManager,
                viewModel)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        if (viewModel.configurableMapItems.size == MapItem.values().size) {
            binding.tabLayout.tabMode = TabLayout.MODE_FIXED
        }
        for ((index, mapItem) in viewModel.configurableMapItems.withIndex()) {
            binding.tabLayout.getTabAt(index)?.let {
                val icon = ImageView(requireContext())
                ContextCompat.getDrawable(requireContext(), mapItem.getImageResource())?.let { drawable ->
                    drawable.tintDrawable(DKColors.primaryColor)
                    icon.setImageDrawable(drawable)
                }
                it.parent?.let { _ ->
                    it.customView = icon
                }
            }
        }
        binding.centerButton.drawable.tintDrawable(DKColors.primaryColor)
        binding.viewPager.addOnPageChangeListener(
            DetailOnPageChangeListener(
                viewModel
            )
        )
    }

    private fun setHeaderSummary() {
        binding.tripDate.text = viewModel.trip?.endDate?.formatDate(DKDatePattern.WEEK_LETTER)?.capitalizeFirstLetter()

        val headerValue =
            DriverDataUI.customHeader?.let {
                it.customTripDetailHeader(requireContext(), viewModel.trip!!.toDKTripItem()) ?: run {
                    it.tripDetailHeader().text(requireContext(), viewModel.trip!!.toDKTripItem())
                }
            }
        binding.tripHeader.text = headerValue ?: run {
            DriverDataUI.headerDay.text(requireContext(), viewModel.trip!!.toDKTripItem())
        }
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

    private class DetailOnPageChangeListener(private var tripDetailViewModel: TripDetailViewModel) : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            tripDetailViewModel.changeMapItem(position)
        }
    }
}
