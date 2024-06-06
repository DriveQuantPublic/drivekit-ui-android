package com.drivequant.drivekit.common.ui.component.lasttripscards.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.lasttripscards.adapter.DKLastTripsFragmentPagerAdapter
import com.drivequant.drivekit.common.ui.databinding.DkFragmentLastTripsCardViewpagerBinding
import com.drivequant.drivekit.common.ui.extension.tint
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


internal class DKLastTripsViewPagerFragment : Fragment() {

    private var lastTripsCards = listOf<Fragment>()
    private lateinit var tabLayout: TabLayout
    private var _binding: DkFragmentLastTripsCardViewpagerBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    companion object {
        fun newInstance(lastTripsCards: List<Fragment>): DKLastTripsViewPagerFragment {
            val fragment = DKLastTripsViewPagerFragment()
            fragment.lastTripsCards = lastTripsCards
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DkFragmentLastTripsCardViewpagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = DKLastTripsFragmentPagerAdapter(
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

            tabLayout.setupWithViewPager(binding.viewPager)
            updateTabLayout(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateTabLayout(position: Int) {
        context?.let { context ->
            for (i in 0 until tabLayout.tabCount) {
                tabLayout.getTabAt(i)?.let { tab ->
                    if (i == position) {
                        val drawable = ContextCompat.getDrawable(
                            context,
                            R.drawable.dk_tab_indicator_selected
                        )
                        drawable?.tint(context, R.color.secondaryColor)
                        tab.icon = drawable
                    } else {
                        val drawable = ContextCompat.getDrawable(
                            context,
                            R.drawable.dk_tab_indicator_default
                        )
                        drawable?.tint(context, R.color.complementaryFontColor)
                        tab.icon = drawable
                    }
                }
            }
        }
    }
}
