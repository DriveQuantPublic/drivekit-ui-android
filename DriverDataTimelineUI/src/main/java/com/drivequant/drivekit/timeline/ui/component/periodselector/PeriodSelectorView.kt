package com.drivequant.drivekit.timeline.ui.component.periodselector

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.timeline.ui.R

class PeriodSelectorView(
    context: Context,
    periods: List<DKTimelinePeriod>
) : LinearLayout(context) {

    private var view: LinearLayout
    private lateinit var viewModel: PeriodSelectorViewModel
    private var buttons = mutableListOf<PeriodSelectorItemView>()

    init {
        view = View.inflate(context, R.layout.dk_timeline_period_selector, null).setDKStyle() as LinearLayout
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        periods.map { period ->
            val item = PeriodSelectorItemView(context, period, object : PeriodSelectorItemListener {
                override fun onPeriodSelected(period: DKTimelinePeriod) {
                    buttonSelected(period)
                    viewModel.onPeriodSelected(period)
                }
            })
            buttons.add(item)
            view.addView(item)
        }
    }

    fun configure(viewModel: PeriodSelectorViewModel) {
        this.viewModel = viewModel
        buttonSelected(viewModel.selectedPeriod)
    }

    fun buttonSelected(period: DKTimelinePeriod) {
        buttons.forEach {
            it.setPeriodSelected(it.timelinePeriod == period)
        }
    }
}