package com.drivequant.drivekit.timeline.ui.component.periodselector

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.core.common.DKPeriod
import com.drivequant.drivekit.timeline.ui.R

internal class PeriodSelectorView(
    context: Context,
    periods: List<DKPeriod> = DKPeriod.values().toList()
) : LinearLayout(context) {

    private lateinit var viewModel: PeriodSelectorViewModel
    private val buttons: List<PeriodSelectorItemView>

    init {
        val view = View.inflate(context, R.layout.dk_timeline_period_selector, null).setDKStyle() as LinearLayout
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        this.buttons = periods.map { period ->
            PeriodSelectorItemView(context, period, object : PeriodSelectorItemListener {
                override fun onPeriodSelected(period: DKPeriod) {
                    buttonSelected(period)
                    viewModel.onPeriodSelected(period)
                }
            })
        }.onEach { view.addView(it) }
    }

    fun configure(viewModel: PeriodSelectorViewModel) {
        this.viewModel = viewModel
        buttonSelected(viewModel.selectedPeriod)
    }

    fun buttonSelected(period: DKPeriod) {
        buttons.forEach {
            it.setPeriodSelected(it.timelinePeriod == period)
        }
    }
}
