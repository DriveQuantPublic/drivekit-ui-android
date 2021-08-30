package com.drivequant.drivekit.common.ui.component.lasttrips.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.lasttrips.adapter.DKLastTripsFragmentPagerAdapter
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.dk_fragment_last_trips_card_viewpager.*


class DKLastTripsViewPagerFragment : Fragment() {

    private var lastTripsCards = listOf<DKLastTripsFragment>()
    private lateinit var tabLayout: TabLayout

    companion object {
        fun newInstance(lastTripsCards: List<DKLastTripsFragment>): DKLastTripsViewPagerFragment {
            val fragment = DKLastTripsViewPagerFragment()
            fragment.lastTripsCards = lastTripsCards
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_last_trips_card_viewpager, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view_pager.adapter = DKLastTripsFragmentPagerAdapter(
            childFragmentManager,
            lastTripsCards
        )

        if (lastTripsCards.size > 1) {
            tabLayout = view.findViewById(R.id.tab_dots)
            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    updateTabLayout(tab.position)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

            tabLayout.setupWithViewPager(view_pager)
            updateTabLayout(0)
        }
    }

    private fun updateTabLayout(position: Int) {
        for (i in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(i)?.let {
                if (i == position) {
                    val drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.dk_tab_indicator_selected
                    )?.mutate()
                    drawable?.tintDrawable(DriveKitUI.colors.secondaryColor())
                    tabLayout.getTabAt(i)?.icon = drawable
                } else {
                    val drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.dk_tab_indicator_default
                    )?.mutate()
                    drawable?.tintDrawable(DriveKitUI.colors.complementaryFontColor())
                    tabLayout.getTabAt(i)?.icon = drawable
                }
            }
        }
    }
}