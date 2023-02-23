package com.drivequant.drivekit.driverachievement.ui.badges.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.recyclerview.widget.LinearLayoutManager
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverachievement.BadgeSyncStatus
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.badges.adapter.BadgesListAdapter
import com.drivequant.drivekit.driverachievement.ui.badges.viewmodel.BadgesListViewModel
import kotlinx.android.synthetic.main.dk_fragment_badges_list.*
import kotlinx.android.synthetic.main.dk_fragment_streaks_list.progress_circular


class BadgesListFragment : Fragment() {

    private lateinit var listViewModel: BadgesListViewModel
    private lateinit var listAdapter: BadgesListAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!this::listViewModel.isInitialized)
            listViewModel = ViewModelProvider(this)[BadgesListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_badges_list, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(requireContext(), "dk_tag_badges"), javaClass.simpleName)
        recycler_view_badges.layoutManager =
            LinearLayoutManager(view.context)
        refresh_badges.setOnRefreshListener {
            updateBadges()
        }
    }

    override fun onResume() {
        super.onResume()
        updateBadges()
    }

    private fun updateBadges() {
        listViewModel.badgesData.observe(this, {
            if (listViewModel.syncStatus != BadgeSyncStatus.NO_ERROR) {
                Toast.makeText(context,
                    context?.getString(R.string.dk_achievements_failed_to_sync_badges),
                    Toast.LENGTH_LONG).show()
            }
            if (this::listAdapter.isInitialized) {
                listAdapter.notifyDataSetChanged()
            } else {
                listAdapter = BadgesListAdapter(view?.context, listViewModel)
                recycler_view_badges.adapter = listAdapter
            }
            updateProgressVisibility(false)
        })
        updateProgressVisibility(true)
        listViewModel.fetchBadges()
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        progress_circular?.apply {
            visibility = if (displayProgress) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        if (displayProgress) {
            progress_circular?.visibility = View.VISIBLE
            refresh_badges.isRefreshing = true
        } else {
            progress_circular?.visibility = View.GONE
            refresh_badges.visibility = View.VISIBLE
            refresh_badges.isRefreshing = false
        }
    }
}
