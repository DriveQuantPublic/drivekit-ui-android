package com.drivequant.drivekit.ui.tripdetail.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.text.HtmlCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.AppCompatRadioButton
import android.view.*
import android.widget.*
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.adapter.TripDetailFragmentPagerAdapter
import com.drivequant.drivekit.ui.tripdetail.viewholder.TripGoogleMapViewHolder
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapItem
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModelFactory
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.fragment_trip_detail.*
import java.util.*

class TripDetailFragment : Fragment() {

    private lateinit var viewModel : TripDetailViewModel

    private lateinit var itinId: String
    private var openAdvice: Boolean = false

    private var adviceAlertDialog: AlertDialog? = null
    private var feedbackAlertDialog: AlertDialog? = null

    private lateinit var tripMapViewHolder: TripGoogleMapViewHolder
    private var mapFragment: SupportMapFragment? = null

    private lateinit var viewContentTrip: View

    companion object {
        fun newInstance(itinId: String,
                        openAdvice: Boolean = false): TripDetailFragment {
            val fragment = TripDetailFragment()
            fragment.itinId = itinId
            fragment.openAdvice = openAdvice
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trip_detail, container, false).setDKStyle()
        viewContentTrip = view.findViewById(R.id.container_trip)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.trip_menu_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.trip_delete -> {
                context?.let {
                 val alert = DKAlertDialog.LayoutBuilder().init(it)
                        .layout(R.layout.template_alert_dialog_layout)
                        .cancelable(true)
                        .positiveButton(getString(R.string.dk_common_ok),
                            DialogInterface.OnClickListener { _, _ ->
                                showProgressCircular()
                                deleteTrip() })
                        .negativeButton(getString(R.string.dk_common_cancel),
                            DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                        .show()

                    val title = alert.findViewById<TextView>(R.id.text_view_alert_title)
                    val description = alert.findViewById<TextView>(R.id.text_view_alert_description)

                    title?.text = getString(R.string.app_name)
                    description?.text = getString(R.string.dk_driverdata_confirm_delete_trip)
                    title?.headLine1()
                    description?.normalText()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(requireContext(), "dk_tag_trips_detail"), javaClass.simpleName)
        savedInstanceState?.getString("itinId")?.let{
            itinId = it
        }
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this,
                TripDetailViewModelFactory(itinId, DriverDataUI.mapItems)
            ).get(TripDetailViewModel::class.java)
        }
        progress_circular.visibility = View.VISIBLE
        activity?.title =  context?.getString(R.string.dk_driverdata_trip_detail_title)
        container_header_trip.setBackgroundColor(DriveKitUI.colors.primaryColor())
        center_button.setColorFilter(DriveKitUI.colors.primaryColor())

        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as? SupportMapFragment
        if (DriverDataUI.enableDeleteTrip) {
            setHasOptionsMenu(true)
        }
        loadTripData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("itinId", itinId)
        super.onSaveInstanceState(outState)
    }

    private fun deleteTrip(){
        viewModel.deleteTripObserver.observe(this, Observer {
            hideProgressCircular()
            context?.let { context ->
                if (it != null){
                    val alert = DKAlertDialog.LayoutBuilder()
                        .init(context)
                        .layout(R.layout.template_alert_dialog_layout)
                        .positiveButton(
                            getString(R.string.dk_common_ok),
                            DialogInterface.OnClickListener { dialog, _ ->
                                dialog.dismiss()
                                val data = Intent()
                                requireActivity().apply {
                                    setResult(RESULT_OK, data)
                                    finish()
                                }
                            })
                        .cancelable(false)
                        .show()

                    val title = alert.findViewById<TextView>(R.id.text_view_alert_title)
                    val description = alert.findViewById<TextView>(R.id.text_view_alert_description)
                    title?.text = getString(R.string.app_name)

                    if (it){
                        description?.text = getString(R.string.dk_driverdata_trip_deleted)
                        title?.headLine1()
                        description?.normalText()
                    } else {
                        description?.text = getString(R.string.dk_driverdata_failed_to_delete_trip)
                        title?.headLine1()
                        description?.normalText()
                    }
                }
            }
        })
        viewModel.deleteTrip()
    }

    private fun sendTripAdviceFeedback(mapItem: MapItem, evaluation: Boolean, feedback: Int, comment: String? = null ){
        viewModel.sendAdviceFeedbackObserver.observe(this, Observer { status ->
            hideProgressCircular()
            if (status != null){
                adviceAlertDialog?.hide()
                Toast.makeText(context, if (status) context?.getString(R.string.dk_driverdata_advice_feedback_success) else context?.getString(R.string.dk_driverdata_advice_feedback_error), Toast.LENGTH_LONG).show()
            }
        })
        viewModel.sendTripAdviceFeedback(mapItem, evaluation, feedback, comment)
    }

    private fun loadTripData(){
        viewModel.tripEventsObserver.observe(this, Observer {
            if (viewModel.events.isNotEmpty()) {
                setMapController()
                setViewPager()
                setHeaderSummary()
                hideProgressCircular()
            } else {
                displayErrorMessageAndGoBack(R.string.dk_driverdata_trip_detail_data_error)
            }
        })
        viewModel.unScoredTrip.observe(this, Observer{ routeAvailable ->
            routeAvailable?.let {
                if (it){
                    setMapController()
                }else{
                    displayErrorMessageAndGoBack(R.string.dk_driverdata_trip_detail_get_road_failed, false)
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
            displayErrorMessageAndGoBack(R.string.dk_driverdata_trip_detail_get_road_failed, false)
        })
        viewModel.noData.observe(this, Observer{
            displayErrorMessageAndGoBack(R.string.dk_driverdata_trip_detail_data_error)
        })
        viewModel.fetchTripData(requireContext())
    }

    private fun displayErrorMessageAndGoBack(stringResId: Int, goBack: Boolean = true){
        AlertDialog.Builder(context)
            .setTitle(this.getString(R.string.app_name))
            .setMessage(stringResId)
            .setCancelable(false)
            .setPositiveButton(context?.getString(R.string.dk_common_ok)) { dialog, _ ->
                dialog.dismiss()
                if (goBack){
                    activity?.onBackPressed()
                }
            }
            .show()
    }

    private fun displayAdviceFromTripInfo(){
        if (openAdvice){
            val index = viewModel.getFirstMapItemIndexWithAdvice()
            if (index > -1) {
                view_pager.currentItem = index
                viewModel.trip?.let {
                    displayAdvice(viewModel.configurableMapItems[index])
                }
            }
        }
    }

    fun displayAdvice(mapItem: MapItem){
        if (viewModel.shouldDisplayAdvice(mapItem)){
            val adviceView = View.inflate(context, R.layout.view_trip_advice_message, null)
            val headerText = adviceView.findViewById<TextView>(R.id.text_view_advice_header)
            val feedbackButtonsLayout = adviceView.findViewById<LinearLayout>(R.id.linear_layout_advice_feedback)
            val builder = AlertDialog.Builder(context).setView(adviceView)

            headerText.text = viewModel.getAdviceTitle(mapItem)
            headerText.setBackgroundColor(DriveKitUI.colors.primaryColor())

            viewModel.getAdviceMessage(mapItem)?.let {
                adviceView.findViewById<TextView>(R.id.text_view_advice_content).text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }

            if (viewModel.shouldDisplayFeedbackButtons(mapItem)){
                val disagreeButton = adviceView.findViewById<LinearLayout>(R.id.linear_layout_advice_negative)
                val disagreeText = adviceView.findViewById<TextView>(R.id.advice_disagree_textview)
                val disagreeImage = adviceView.findViewById<ImageView>(R.id.advice_disagree_image)

                val agreeButton = adviceView.findViewById<LinearLayout>(R.id.linear_layout_advice_positive)
                val agreeText = adviceView.findViewById<TextView>(R.id.advice_agree_textview)
                val agreeImage = adviceView.findViewById<ImageView>(R.id.advice_agree_image)

                disagreeText.headLine2(DriveKitUI.colors.primaryColor())
                agreeText.headLine2(DriveKitUI.colors.primaryColor())
                DrawableCompat.setTint(agreeImage.drawable, DriveKitUI.colors.primaryColor())

                disagreeText.text = context?.getString(R.string.dk_driverdata_advice_disagree)
                DrawableCompat.setTint(disagreeImage.drawable, DriveKitUI.colors.primaryColor())
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
                builder.setPositiveButton(context?.getString(R.string.dk_common_ok)) { dialog, _ ->
                    dialog.dismiss()
                }
            }
            adviceAlertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(DriveKitUI.colors.secondaryColor())
            adviceAlertDialog = builder.show()
        }
    }

    private fun displayAdviceFeedback(mapItem: MapItem) {
        val feedbackView = View.inflate(context, R.layout.view_trip_advice_feedback, null)
        FontUtils.overrideFonts(context,feedbackView)
        val header = feedbackView.findViewById<TextView>(R.id.alert_dialog_trip_feedback_header)
        val radioGroup = feedbackView.findViewById<RadioGroup>(R.id.radio_group_trip_feedback)

        header.setBackgroundColor(DriveKitUI.colors.primaryColor())
        header.text =  context?.getString(R.string.dk_driverdata_advice_feedback_disagree_title)
            ?.toUpperCase(Locale.getDefault())
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

        val builder = AlertDialog.Builder(context)
            .setView(feedbackView)
            .setNegativeButton(context?.getString(R.string.dk_common_cancel)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(context?.getString(R.string.dk_common_ok)) { _, _ ->
                if (feedbackView.findViewById<EditText>(R.id.edit_text_feedback).text.isNotEmpty()) {
                    buildFeedbackData(mapItem, feedbackView, radioGroup)
                } else {
                    context?.let {
                        val emptyFieldText = DKResource.convertToString(it, "dk_common_error_empty_field")
                        Toast.makeText(it, emptyFieldText, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        feedbackAlertDialog = builder.show()
        feedbackAlertDialog?.apply {
            getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
            getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(DriveKitUI.colors.secondaryColor())
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
                ?.setTextColor(DriveKitUI.colors.secondaryColor())
        } else {
            feedbackView.findViewById<EditText>(R.id.edit_text_feedback).isEnabled = false
        }
    }

    private fun buildFeedbackData(mapItem: MapItem, feedbackView: View, radioGroup: RadioGroup){
        showProgressCircular()
        var comment: String? = null
        val feedback = when (radioGroup.checkedRadioButtonId){
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

    private fun setMapController(){
        mapFragment?.getMapAsync {
                googleMap -> mapFragment
            tripMapViewHolder =
                TripGoogleMapViewHolder(
                    this,
                    viewContentTrip,
                    viewModel,
                    googleMap
                )
            tripMapViewHolder.traceRoute(viewModel.displayMapItem.value)
            tripMapViewHolder.updateCamera()
            displayAdviceFromTripInfo()
            hideProgressCircular()
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
                viewModel.trip
            ))
            .commit()
    }

    private fun setViewPager(){
        view_pager.adapter =
            TripDetailFragmentPagerAdapter(
                childFragmentManager,
                viewModel)
        tab_layout.setupWithViewPager(view_pager)
        for ((index, mapItem) in viewModel.configurableMapItems.withIndex()){
            tab_layout.getTabAt(index)?.let {
                val icon = ImageView(requireContext())
                ContextCompat.getDrawable(requireContext(), mapItem.getImageResource())?.let { drawable ->
                    DrawableCompat.setTint(drawable, DriveKitUI.colors.primaryColor())
                    icon.setImageDrawable(drawable)
                }
                it.customView = icon
                val sizePx = (it.parent.height * 0.66).toInt()
                it.customView?.layoutParams = LinearLayout.LayoutParams(sizePx,sizePx)
            }
        }
        DrawableCompat.setTint(center_button.drawable, DriveKitUI.colors.primaryColor())
        view_pager.addOnPageChangeListener(
            DetailOnPageChangeListener(
                viewModel
            )
        )
    }

    private fun setHeaderSummary(){
        trip_date.text = viewModel.trip?.endDate?.formatDate(DKDatePattern.WEEK_LETTER)?.capitalize()
        trip_date.setTextColor(DriveKitUI.colors.fontColorOnPrimaryColor())
        trip_distance.text = DriverDataUI.headerDay.text(requireContext(), viewModel.trip!!)
        trip_distance.setTextColor(DriveKitUI.colors.fontColorOnPrimaryColor())
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