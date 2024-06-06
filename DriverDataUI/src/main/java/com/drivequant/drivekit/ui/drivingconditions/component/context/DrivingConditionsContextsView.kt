package com.drivequant.drivekit.ui.drivingconditions.component.context

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.ui.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

internal class DrivingConditionsContextsView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    var onContextCardScrollStateChangeCallback: ((state: ContextCardScrollState) -> Unit)? = null
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var viewModel: DrivingConditionsContextsViewModel? = null
    private val adapter = DrivingConditionsContextsViewPagerAdapter()

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.viewPager = findViewById(R.id.viewPager)
        this.tabLayout = findViewById(R.id.tabLayout)

        configureViewPager()
        configureTabLayout()
    }

    fun configure(viewModel: DrivingConditionsContextsViewModel) {
        this.viewModel = viewModel
        viewModel.onViewModelUpdate = this::update
        update()
    }

    internal fun update() {
        this.viewModel?.contextCards?.let {
            this.adapter.configure(it)
        }
    }

    private fun configureViewPager() {
        this.viewPager.adapter = this.adapter

        this.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                val contextCardScrollState: ContextCardScrollState = if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    ContextCardScrollState.IDLE
                } else {
                    ContextCardScrollState.MOVING
                }
                onContextCardScrollStateChangeCallback?.invoke(contextCardScrollState)
            }
        })
    }

    private fun configureTabLayout() {
        this.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateTabLayout(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Nothing to do.
            }
            override fun onTabReselected(tab: TabLayout.Tab) {
                // Nothing to do.
            }
        })

        TabLayoutMediator(this.tabLayout, this.viewPager) { tab, _ ->
            tab.text = null
        }.attach()

        updateTabLayout(0)
    }

    private fun updateTabLayout(position: Int) {
        for (i in 0 until this.tabLayout.tabCount) {
            this.tabLayout.getTabAt(i)?.let { tab ->
                if (i == position) {
                    val drawable = ContextCompat.getDrawable(context,
                        com.drivequant.drivekit.common.ui.R.drawable.dk_tab_indicator_selected)
                    drawable?.tintDrawable(DKColors.secondaryColor)
                    tab.icon = drawable
                } else {
                    val drawable = ContextCompat.getDrawable(context,
                        com.drivequant.drivekit.common.ui.R.drawable.dk_tab_indicator_default)
                    drawable?.tintDrawable(DKColors.complementaryFontColor)
                    tab.icon = drawable
                }
            }
        }
    }

}

internal enum class ContextCardScrollState {
    IDLE, MOVING
}
