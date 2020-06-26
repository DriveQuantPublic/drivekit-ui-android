package com.drivequant.drivekit.driverachievement.ui.leaderboard.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter.RankingsFragmentPagerAdapter
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingListViewModel
import kotlinx.android.synthetic.main.dk_fragment_leaderboard.*

class LeaderBoardFragment : Fragment() {

    lateinit var rankingViewModel: RankingListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rankingViewModel = ViewModelProviders.of(this).get(RankingListViewModel::class.java)

        setViewPager()
        setTabLayout()
        setupSelectors()

        view_pager_leader_board.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                RankingListFragment.newInstance(
                    rankingViewModel,
                    DriverAchievementUI.rankingTypes[position]
                )
            }

            override fun onPageScrollStateChanged(position: Int) {

            }
        })
    }

    private fun createRankingSelectors(selectorText: String): Button {
        val button = Button(requireContext())
        button.text = selectorText
        button.setBackgroundResource(R.drawable.button_selector)
        return button
    }

    private fun setupSelectors() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dk_fragment_leaderboard, container, false)
    }

    private fun setViewPager() {
        view_pager_leader_board.adapter =
            RankingsFragmentPagerAdapter(
                rankingViewModel,
                requireContext(),
                childFragmentManager
            )
        tab_layout_leader_board.setupWithViewPager(view_pager_leader_board)
//        for ((index, rankingType) in DriverAchievementUI.rankingTypes.withIndex()){
//            tab_layout_leader_board.getTabAt(index)?.let {
//               val picto = when (rankingType) {
//                    RankingType.SAFETY -> R.drawable.dk_common_safety
//                    RankingType.DISTRACTION -> R.drawable.dk_common_distraction
//                    RankingType.ECO_DRIVING -> R.drawable.dk_common_ecodriving
//                }
//                val icon = ImageView(requireContext())
//                ContextCompat.getDrawable(requireContext(), picto)?.let { drawable ->
//                    DrawableCompat.setTint(drawable, DriveKitUI.colors.primaryColor())
//                    icon.setImageDrawable(drawable)
//                }
//                it.customView = icon
//                val sizePx = (it.parent.height * 0.66).toInt()
//                it.customView?.layoutParams = LinearLayout.LayoutParams(sizePx,sizePx)
//            }
//        }
    }

    private fun setTabLayout() {
        if (DriverAchievementUI.rankingTypes.size == 1) {
            tab_layout_leader_board.removeAllTabs()
        }
    }
}