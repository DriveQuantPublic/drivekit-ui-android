package com.drivequant.drivekit.driverachievement.ui.badges.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.IntArg
import com.drivequant.drivekit.driverachievement.BadgeSyncStatus
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.badges.BadgeCounterView
import com.drivequant.drivekit.driverachievement.ui.badges.adapter.BadgesListAdapter
import com.drivequant.drivekit.driverachievement.ui.badges.viewmodel.BadgesListViewModel


class BadgesListFragment : Fragment() {

    private lateinit var listViewModel: BadgesListViewModel
    private lateinit var listAdapter: BadgesListAdapter
    private lateinit var badgesRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressView: ProgressBar
    private lateinit var badgeCounterContainer: View
    private lateinit var badgeCounterTitle: TextView
    private lateinit var bronzeBadgeCounter: BadgeCounterView
    private lateinit var silverBadgeCounter: BadgeCounterView
    private lateinit var goldBadgeCounter: BadgeCounterView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dk_fragment_badges_list, container, false).setDKStyle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!this::listViewModel.isInitialized)
            listViewModel = ViewModelProvider(this)[BadgesListViewModel::class.java]

        this.badgesRecyclerView = view.findViewById(R.id.recycler_view_badges)
        this.swipeRefreshLayout = view.findViewById(R.id.refresh_badges)
        this.progressView = view.findViewById(R.id.progress_circular)
        this.badgeCounterContainer = view.findViewById(R.id.badgeCounterContainer)
        this.badgeCounterTitle = view.findViewById(R.id.badgeCounterTitle)
        this.bronzeBadgeCounter = view.findViewById(R.id.bronzeContainer)
        this.silverBadgeCounter = view.findViewById(R.id.silverContainer)
        this.goldBadgeCounter = view.findViewById(R.id.goldContainer)

        this.badgeCounterTitle.typeface = DriveKitUI.primaryFont(view.context)

        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_badges), javaClass.simpleName)
        badgesRecyclerView.layoutManager = LinearLayoutManager(view.context)
        swipeRefreshLayout.setOnRefreshListener {
            updateBadges()
        }

        listViewModel.badgesData.observe(viewLifecycleOwner) {
            context?.let { context ->
                updateStatistics()
                if (listViewModel.syncStatus != BadgeSyncStatus.NO_ERROR) {
                    Toast.makeText(context, context.getString(R.string.dk_achievements_failed_to_sync_badges), Toast.LENGTH_LONG).show()
                }
            }
            if (this::listAdapter.isInitialized) {
                listAdapter.notifyDataSetChanged()
            } else {
                listAdapter = BadgesListAdapter(view.context, listViewModel)
                badgesRecyclerView.adapter = listAdapter
            }
            updateProgressVisibility(false)
        }
    }

    override fun onResume() {
        super.onResume()
        updateBadges()
    }

    private fun updateBadges() {
        updateProgressVisibility(true)
        listViewModel.fetchBadges()
    }

    private fun updateStatistics() {
        context?.let { context ->
            val statistics = listViewModel.badgesStatistics
            if (statistics != null && statistics.total > 0) {
                this.badgeCounterTitle.text = DKResource.buildString(
                    context = context,
                    string = resources.getQuantityString(R.plurals.dk_badge_earned_badges_number_title, statistics.acquired),
                    textColor = DriveKitUI.colors.primaryColor(),
                    textSize = com.drivequant.drivekit.common.ui.R.dimen.dk_text_medium,
                    IntArg(
                        statistics.acquired,
                        color = DriveKitUI.colors.primaryColor(),
                        size = com.drivequant.drivekit.common.ui.R.dimen.dk_text_medium
                    )
                )
                this.bronzeBadgeCounter.update(
                    R.string.dk_badge_bronze,
                    R.color.dkBadgeLevel1DarkColor,
                    statistics.acquiredBronze,
                    statistics.totalBronze
                )
                this.silverBadgeCounter.update(
                    R.string.dk_badge_silver,
                    R.color.dkBadgeLevel2DarkColor,
                    statistics.acquiredSilver,
                    statistics.totalSilver
                )
                this.goldBadgeCounter.update(
                    R.string.dk_badge_gold,
                    R.color.dkBadgeLevel3DarkColor,
                    statistics.acquiredGold,
                    statistics.totalGold
                )
                this.badgeCounterContainer.visibility = View.VISIBLE
            } else {
                this.badgeCounterContainer.visibility = View.GONE
            }
        }
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progressView.visibility = View.VISIBLE
            swipeRefreshLayout.isRefreshing = true
        } else {
            progressView.visibility = View.GONE
            swipeRefreshLayout.visibility = View.VISIBLE
            swipeRefreshLayout.isRefreshing = false
        }
    }
}
