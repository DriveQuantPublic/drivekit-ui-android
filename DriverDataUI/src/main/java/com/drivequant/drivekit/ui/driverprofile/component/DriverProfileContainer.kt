package com.drivequant.drivekit.ui.driverprofile.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.ui.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

internal open class DriverProfileContainer<ViewModel>(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    var adapter: DriverProfileContainerViewPagerAdapter<ViewModel>? = null
        set(value) {
            field = value
            this.viewPager.adapter = value

            if (!this.configured) {
                this.configured = true

                TabLayoutMediator(this.tabLayout, this.viewPager) { tab, _ ->
                    tab.text = null
                }.attach()

                updateTabLayout(0)
            }

            this.tabLayout.visibility = if (value != null && value.itemCount > 1) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    var onScrollStateChangeCallback: ((state: DriverProfileScrollState) -> Unit)? = null
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var configured = false

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.viewPager = findViewById(R.id.viewPager)
        this.tabLayout = findViewById(R.id.tabDots)

        configureViewPager()
        configureTabLayout()
    }

    private fun configureViewPager() {
        this.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                val scrollState: DriverProfileScrollState = if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    DriverProfileScrollState.IDLE
                } else {
                    DriverProfileScrollState.MOVING
                }
                onScrollStateChangeCallback?.invoke(scrollState)
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
    }

    private fun updateTabLayout(position: Int) {
        for (i in 0 until this.tabLayout.tabCount) {
            this.tabLayout.getTabAt(i)?.let {
                if (i == position) {
                    val drawable = ContextCompat.getDrawable(context,
                        R.drawable.dk_tab_indicator_selected)?.mutate()
                    drawable?.tintDrawable(DriveKitUI.colors.secondaryColor())
                    tabLayout.getTabAt(i)?.icon = drawable
                } else {
                    val drawable = ContextCompat.getDrawable(context,
                        R.drawable.dk_tab_indicator_default)?.mutate()
                    drawable?.tintDrawable(DriveKitUI.colors.complementaryFontColor())
                    tabLayout.getTabAt(i)?.icon = drawable
                }
            }
        }
    }
}
