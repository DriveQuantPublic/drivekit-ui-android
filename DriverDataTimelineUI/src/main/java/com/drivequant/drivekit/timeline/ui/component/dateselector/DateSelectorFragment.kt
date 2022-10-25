package com.drivequant.drivekit.timeline.ui.component.dateselector

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.timeline.ui.R
import kotlinx.android.synthetic.main.fragment_date_selector.*

class DateSelectorFragment : Fragment() {

    private lateinit var viewModel: DateSelectorViewModel

    companion object {
        fun newInstance(
            viewModel: DateSelectorViewModel
        ): DateSelectorFragment {
            val fragment = DateSelectorFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.hasPreviousDate.observe(this) {
            context?.let { context ->
                DKResource.convertToDrawable(context, "dk_timeline_previous")?.let { drawable ->
                    if (!it) {
                        drawable.tintDrawable(DriveKitUI.colors.complementaryFontColor())
                    }
                    layout_image_view_previous.isEnabled = it
                    image_view_previous.setImageDrawable(drawable)
                }
            }
        }

        viewModel.hasNextDate.observe(this) {
            context?.let { context ->
                DKResource.convertToDrawable(context, "dk_timeline_next")?.let { drawable ->
                    if (!it) {
                        drawable.tintDrawable(DriveKitUI.colors.complementaryFontColor())
                    }
                    layout_image_view_next.isEnabled = it
                    image_view_next.setImageDrawable(drawable)
                }
            }
        }

        viewModel.dateRange.observe(this) {
            context?.let { context ->
                val fromDatePrefix = DKResource.convertToString(context, "dk_timeline_from_date")
                val toDatePrefix = DKResource.convertToString(context, "dk_timeline_to_date")

                text_view_date_range.text = DKSpannable().append(fromDatePrefix, context.resSpans {
                    typeface(Typeface.BOLD)
                    color(DriveKitUI.colors.complementaryFontColor())
                }).space().append(
                    it.first.take(10),
                    context.resSpans {
                        typeface(Typeface.BOLD)
                        color(DriveKitUI.colors.primaryColor())
                    }).space().append(toDatePrefix, context.resSpans {
                    typeface(Typeface.BOLD)
                    color(DriveKitUI.colors.complementaryFontColor())
                }).space().append(
                   it.second.take(10),
                    context.resSpans {
                        typeface(Typeface.BOLD)
                        color(DriveKitUI.colors.primaryColor())
                    }).toSpannable()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_date_selector, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO
        //checkViewModelInitialization()

        layout_image_view_previous.setOnClickListener {
            viewModel.updateDate(DirectionType.PREVIOUS)
        }

        layout_image_view_next.setOnClickListener {
            viewModel.updateDate(DirectionType.NEXT)
        }

    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(DateSelectorViewModel::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        //checkViewModelInitialization()
    }
}

