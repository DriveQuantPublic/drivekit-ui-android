package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.adapter.FilterAdapter
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import kotlinx.android.synthetic.main.dk_filter_view_layout.view.*

class FilterView : LinearLayout {

    private var listOfItems = listOf<FilterItem>()
    private lateinit var currentItem: FilterItem

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_filter_view_layout, null).setDKStyle()
        addView(view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT))
    }

    fun setItems(filterItems: List<FilterItem>) {
        listOfItems = filterItems
        val adapter = FilterAdapter(context, R.id.accelerate, listOfItems)
        spinner_filter.adapter = adapter
        spinner_filter.setSelection(0, false)
    }

    private fun getItemPositionInList(itemId: Any?): Any? {
        for (i in listOfItems.indices) {
            if (listOfItems[i].itemId == itemId) {
                return i
            }
        }
        return 0
    }

    private fun updateSelectedItem(position: Int) {
        currentItem = listOfItems[position]
        //TODO update filter text
    }
}