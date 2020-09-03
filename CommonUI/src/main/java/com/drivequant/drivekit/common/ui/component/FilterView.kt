package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Spinner
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.adapter.FilterAdapter
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.common.ui.extension.setDKStyle

class FilterView : LinearLayout {

    lateinit var spinner: Spinner

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_filter_view_layout, null).setDKStyle()
        spinner = view.findViewById(R.id.spinner_filter)
        addView(view, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT))
    }

    fun setItems(filterItems: List<FilterItem>) {
        val adapter = FilterAdapter(context, R.layout.dk_simple_list_item_spinner, filterItems)
        spinner.adapter = adapter
        spinner.setSelection(0, false)
    }
}