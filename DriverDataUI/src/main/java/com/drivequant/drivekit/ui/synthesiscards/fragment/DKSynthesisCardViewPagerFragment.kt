package com.drivequant.drivekit.ui.synthesiscards.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.WrapContentViewPager
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.synthesiscards.adapter.DKSynthesisCardFragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class DKSynthesisCardViewPagerFragment : Fragment() {

    private var cards = listOf<DKSynthesisCardFragment>()
    private lateinit var tabLayout: TabLayout

    companion object {
        fun newInstance(cards: List<DKSynthesisCardFragment>): DKSynthesisCardViewPagerFragment {
            val fragment = DKSynthesisCardViewPagerFragment()
            fragment.cards = cards
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_synthesis_card_viewpager, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = view.findViewById<WrapContentViewPager>(R.id.view_pager)
        viewPager.apply {
            offscreenPageLimit = cards.size
            adapter = DKSynthesisCardFragmentPagerAdapter(
                childFragmentManager,
                cards
            )
        }

        if (cards.size > 1) {
            tabLayout = view.findViewById(R.id.tabDotsScore)
            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    updateTabLayout(tab.position)
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

            tabLayout.setupWithViewPager(viewPager)
            updateTabLayout(0)
        }
    }

    private fun updateTabLayout(position: Int) {
        for (i in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(i)?.let {
                if (i == position) {
                    val drawable = ContextCompat.getDrawable(requireContext(),
                        R.drawable.dk_tab_indicator_selected)?.mutate()
                    drawable?.tintDrawable(DriveKitUI.colors.secondaryColor())
                    tabLayout.getTabAt(i)?.icon = drawable
                } else {
                    val drawable = ContextCompat.getDrawable(requireContext(),
                        R.drawable.dk_tab_indicator_default)?.mutate()
                    drawable?.tintDrawable(DriveKitUI.colors.complementaryFontColor())
                    tabLayout.getTabAt(i)?.icon = drawable
                }
            }
        }
    }
}
