package com.drivequant.drivekit.driverachievement.ui.leaderboard.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.RankingSelectorType
import com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter.RankingsFragmentPagerAdapter
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingListViewModel
import kotlinx.android.synthetic.main.dk_fragment_leaderboard.*

class LeaderBoardFragment : Fragment() {
    lateinit var viewModel: RankingListViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RankingListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewPager()
        setTabLayout()
        setLeaderBoardHeader()
    }

    private fun buildRankingSelector(selectorText: String): Button {
        val button = Button(requireContext())
        button.text = selectorText
        button.tag = selectorText
        button.setBackgroundResource(R.drawable.button_selector)
        return button
    }

    private fun setLeaderBoardHeader() {
        when (val rankingSelector = DriverAchievementUI.rankingSelector) {
            is RankingSelectorType.NONE -> {
                //TODO nothing
            }
            is RankingSelectorType.PERIOD -> {
                val periods = rankingSelector.rankingPeriods
                for (selector in periods) {
                    val button = buildRankingSelector(selector.getValue())
                    onSelectorClick(button)
                    selector_container.addView(button)
                }
            }
        }
    }

    private fun onSelectorClick(view: View) {
        view.setOnClickListener {
            when (view.tag) {
                "LEGACY" -> {
                    Toast.makeText(requireContext(), "legacy", Toast.LENGTH_SHORT).show()
                }
                "MONTH" -> {
                    Toast.makeText(requireContext(), "month", Toast.LENGTH_SHORT).show()
                }
                "WEEK" -> {
                    Toast.makeText(requireContext(), "week", Toast.LENGTH_SHORT).show()
                }
                "ALL_TIME" -> {
                    Toast.makeText(requireContext(), "all_time", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
        view_pager_leader_board.offscreenPageLimit = 3
        view_pager_leader_board.adapter =
            RankingsFragmentPagerAdapter(
                requireContext(),
                childFragmentManager)
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
        if(DriverAchievementUI.rankingTypes.size == 1) {
            tab_layout_leader_board.removeAllTabs()
        }
    }
}