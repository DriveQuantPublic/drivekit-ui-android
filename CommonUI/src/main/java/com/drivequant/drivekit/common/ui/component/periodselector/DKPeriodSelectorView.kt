package com.drivequant.drivekit.common.ui.component.periodselector

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.databaseutils.entity.DKPeriod

class DKPeriodSelectorView(context: Context) : LinearLayout(context) {

    private lateinit var viewModel: DKPeriodSelectorViewModel
    private val view: ViewGroup
    private var buttons: List<PeriodSelectorItemView> = listOf()

    init {
        this.view = View.inflate(context, R.layout.dk_period_selector, null).setDKStyle() as LinearLayout
        addView(
            this.view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun configure(viewModel: DKPeriodSelectorViewModel) {
        this.viewModel = viewModel

        this.view.removeAllViews()
        this.buttons = viewModel.periods.map { period ->
            PeriodSelectorItemView(context, period, object : DKPeriodSelectorItemListener {
                override fun onPeriodSelected(period: DKPeriod) {
                    buttonSelected(period)
                    viewModel.onPeriodSelected(period)
                }
            })
        }.onEach { this.view.addView(it) }

        buttonSelected(viewModel.selectedPeriod)
    }

    fun buttonSelected(period: DKPeriod) {
        this.buttons.forEach {
            it.setPeriodSelected(it.period == period)
        }
    }
}
