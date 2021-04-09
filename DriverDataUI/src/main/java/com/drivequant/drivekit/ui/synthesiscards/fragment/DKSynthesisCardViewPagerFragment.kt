package com.drivequant.drivekit.ui.synthesiscards.fragment

import android.graphics.Color
import android.graphics.drawable.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.synthesiscards.adapter.DKSynthesisCardFragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.dk_fragment_synthesis_card_viewpager.*

class DKSynthesisCardViewPagerFragment(
    private val cards: List<DKSynthesisCardFragment>
) : Fragment() {

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

            val background = ContextCompat.getDrawable(requireContext(), R.drawable.dk_tab_selector) as StateListDrawable

            val selectedDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.dk_tab_indicator_selected)
            val selectedColor = ColorUtils.setAlphaComponent(Color.GREEN, 122)
            DrawableCompat.setTint(
                selectedDrawable!!,
                selectedColor
            )

            val unselectedDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.dk_tab_indicator_default)
            val unselectedColor = ColorUtils.setAlphaComponent(Color.RED, 122)
            DrawableCompat.setTint(
                unselectedDrawable!!,
                unselectedColor
            )

            background.addState(
                intArrayOf(
                    android.R.attr.state_focused,
                    android.R.attr.state_selected,
                    android.R.attr.state_active
                ), selectedDrawable
            )
            //background.addState(intArrayOf(), unselectedDrawable)
            //tabLayout.background = background
            tabLayout.setupWithViewPager(view_pager, true)
        }
    }
}