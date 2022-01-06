package com.drivequant.drivekit.tripanalysis.workinghours.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.tripanalysis.service.workinghours.TripStatus
import com.drivequant.drivekit.tripanalysis.workinghours.adapter.WorkingHoursSpinnerAdapter
import com.drivequant.drivekit.tripanalysis.workinghours.viewholder.HoursSpinnerItem

internal class WorkingHoursSpinnerSettings : LinearLayout {

    private var items: List<HoursSpinnerItem> = buildTripStatusItems()
    private var touched: Boolean = false
    private lateinit var textViewTitle: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var spinnerContainer: LinearLayout
    private lateinit var spinner: Spinner
    private var listener: SpinnerListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_layout_spinner_settings, null)
        textViewTitle = view.findViewById(R.id.spinner_settings_title)
        textViewDescription = view.findViewById(R.id.spinner_settings_description)
        spinnerContainer = view.findViewById(R.id.spinner_container)
        spinner = view.findViewById(R.id.spinner)

        spinner.adapter = WorkingHoursSpinnerAdapter(context, items)

        spinnerContainer.setBackgroundColor(DriveKitUI.colors.neutralColor())

        spinner.setOnTouchListener(OnTouchListener setOnTouchListener@{ v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_UP) {
                touched  = true
                v.performClick()
                return@setOnTouchListener true
            } else {
                touched = false
                return@setOnTouchListener false
            }
        })

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = items[position].tripStatus
                if (selected == TripStatus.DISABLED) {
                    setDescription(DKResource.convertToString(context, "dk_working_hours_slot_disabled_desc"))
                } else {
                    setDescription(null)
                }
                if (touched) {
                    listener?.onItemSelected(selected)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        textViewTitle.apply {
            bigText(DriveKitUI.colors.primaryColor())
            setTypeface(DriveKitUI.primaryFont(context), Typeface.NORMAL)
        }

        textViewDescription.apply {
            smallText(DriveKitUI.colors.warningColor())
            setTypeface(DriveKitUI.primaryFont(context), Typeface.BOLD)
        }

        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun buildTripStatusItems() = listOf(
        HoursSpinnerItem(context, TripStatus.DISABLED),
        HoursSpinnerItem(context, TripStatus.PERSONAL),
        HoursSpinnerItem(context, TripStatus.BUSINESS),
    )

    fun setTitle(title: String) {
        textViewTitle.text = title
    }

    fun setDescription(description: String?) {
        if (description != null) {
            textViewDescription.text = description
            textViewDescription.visibility = View.VISIBLE
        } else {
            textViewDescription.visibility = View.GONE
        }
    }

    fun setListener(listener: SpinnerListener) {
        this.listener = listener
    }

    fun getSelectedTripStatus() = (spinner.selectedItem as HoursSpinnerItem).tripStatus

    fun selectItem(tripStatus: TripStatus) {
        items.forEachIndexed { index, hoursSpinnerItem ->
            if (hoursSpinnerItem.tripStatus == tripStatus) {
                spinner.setSelection(index, false)
            }
        }
    }

    interface SpinnerListener {
        fun onItemSelected(item: TripStatus)
    }
}