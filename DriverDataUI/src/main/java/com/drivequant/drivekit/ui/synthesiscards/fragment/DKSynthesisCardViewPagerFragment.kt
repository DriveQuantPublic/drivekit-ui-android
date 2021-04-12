package com.drivequant.drivekit.ui.synthesiscards.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.synthesiscards.adapter.DKSynthesisCardFragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.dk_fragment_synthesis_card_viewpager.*


class DKSynthesisCardViewPagerFragment : Fragment() {

    private var cards = listOf<DKSynthesisCardFragment>()

    companion object {
        fun newInstance(cards: List<DKSynthesisCardFragment>) : DKSynthesisCardViewPagerFragment{
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

        view_pager.adapter = DKSynthesisCardFragmentPagerAdapter(
            childFragmentManager,
            cards
        )

        if (cards.size > 1) {
            val tabLayout: TabLayout = view.findViewById(R.id.tabDotsScore)
            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val test = tabLayout.getTabAt(tab.position)
                    if (test != null) {
                        test.text = DKSpannable().append("·", requireContext().resSpans {
                            color(DriveKitUI.colors.secondaryColor())
                            size(R.dimen.dk_text_xxxbig)
                            typeface(Typeface.BOLD)
                        }).toSpannable()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    val test = tabLayout.getTabAt(tab.position)
                    if (test != null) {
                        test.text = DKSpannable().append("·", requireContext().resSpans {
                            color(DriveKitUI.colors.mainFontColor())
                            size(R.dimen.dk_text_xxbig)
                            typeface(Typeface.NORMAL)
                        }).toSpannable()
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            for (i in 0 until cards.size){
                val test = tabLayout.getTabAt(i)
                test?.let {
                    test.text = DKSpannable().append("·", requireContext().resSpans {
                        color(DriveKitUI.colors.mainFontColor())
                        size(R.dimen.dk_text_xxxbig)
                        typeface(Typeface.NORMAL)

                    }).toSpannable()
                }
            }
            tabLayout.getTabAt(0)?.customView?.let {
                it.isSelected = true
            }
            tabLayout.setupWithViewPager(view_pager, true)
            tabLayout.getTabAt(0)?.select()

        }
    }
}