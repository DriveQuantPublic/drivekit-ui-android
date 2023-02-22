package com.drivequant.drivekit.common.ui.component.periodselector

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.core.common.DKPeriod

class DKPeriodSelectorView(
    context: Context,
    periods: List<DKPeriod>
) : LinearLayout(context) {

    private lateinit var viewModel: DKPeriodSelectorViewModel
    private val buttons: List<PeriodSelectorItemView>

    init {
        val view = View.inflate(context, R.layout.dk_period_selector, null).setDKStyle() as LinearLayout
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        this.buttons = periods.map { period ->
            PeriodSelectorItemView(context, period, object : DKPeriodSelectorItemListener {
                override fun onPeriodSelected(period: DKPeriod) {
                    buttonSelected(period)
                    viewModel.onPeriodSelected(period)
                }
            })
        }.onEach { view.addView(it) }
    }

    fun configure(viewModel: DKPeriodSelectorViewModel) {
        this.viewModel = viewModel
        buttonSelected(viewModel.selectedPeriod)
    }

    fun buttonSelected(period: DKPeriod) {
        buttons.forEach {
            it.setPeriodSelected(it.period == period)
        }
    }
}
