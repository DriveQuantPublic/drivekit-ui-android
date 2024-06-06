package com.drivequant.drivekit.common.ui.component.contextcard.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCard
import com.drivequant.drivekit.common.ui.component.contextcard.adapter.ContextCardItemListAdapter
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.smallText

class DKContextCardView(context: Context) : LinearLayout(context) {

    private var contextCard: DKContextCard? = null
    private var adapter: ContextCardItemListAdapter? = null
    private val emptyView: View
    private val contextCardContainer: View
    private val titleView: TextView
    private val contextCardBar: ContextCardBar
    private val recyclerView: RecyclerView
    private val noDataTitleView: TextView
    private val noDataDescriptionView: TextView

    init {
        val view = inflate(context, R.layout.dk_context_card_view, null).setDKStyle()
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        this.emptyView = view.findViewById(R.id.empty_context_card_view)
        this.contextCardContainer = view.findViewById(R.id.context_card_view_container)
        this.titleView = view.findViewById(R.id.context_card_title)
        this.contextCardBar = view.findViewById(R.id.context_card_bar)
        this.recyclerView = view.findViewById(R.id.recycler_view)
        this.noDataTitleView = view.findViewById(R.id.text_view_no_data_title)
        this.noDataDescriptionView = view.findViewById(R.id.text_view_no_data_description)
    }

    fun configure(contextCard: DKContextCard) {
        this.contextCard = contextCard
        update()
    }

    private fun update() {
        updateContextCardContainer()
        updateProgressItems()
        displayContextCardItems()
    }

    private fun displayContextCardItems() {
        context?.let { context ->
            contextCard?.let { contextCard ->
                adapter?.update(contextCard) ?: run {
                    recyclerView.layoutManager = GridLayoutManager(context, 2)
                    adapter = ContextCardItemListAdapter(
                        context,
                        contextCard.getItems(),
                    )
                    recyclerView.adapter = adapter
                }
            }
        }
    }

    private fun updateProgressItems() {
        this.contextCard?.let { contextCard ->
            this.contextCardBar.update(contextCard.getItems())
        }
    }

    private fun updateContextCardContainer() {
        if (this.contextCard?.getItems().isNullOrEmpty()) {
            displayEmptyContextCardUI()
        } else {
            displayContextCardUI()
        }
    }

    private fun displayContextCardUI() {
        with(this.titleView) {
            text = contextCard?.getTitle(context)
            headLine2()
            visibility = View.VISIBLE
        }
        this.emptyView.visibility = GONE
        this.contextCardContainer.visibility = VISIBLE
    }

    private fun displayEmptyContextCardUI() {
        this.emptyView.visibility = View.VISIBLE
        with(this.noDataTitleView) {
            headLine2()
            text = contextCard?.getTitle(context)
        }
        with(this.noDataDescriptionView) {
            smallText()
            text = contextCard?.getEmptyDataDescription(context)
        }
        this.contextCardContainer.visibility = View.GONE
        this.titleView.visibility = GONE
    }
}
