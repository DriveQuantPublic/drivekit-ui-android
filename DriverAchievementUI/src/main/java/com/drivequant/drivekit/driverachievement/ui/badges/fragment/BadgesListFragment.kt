package com.drivequant.drivekit.driverachievement.ui.badges.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverachievement.BadgeSyncStatus
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.badges.adapter.BadgesListAdapter
import com.drivequant.drivekit.driverachievement.ui.badges.viewmodel.BadgesListViewModel


class BadgesListFragment : Fragment() {

    private lateinit var listViewModel: BadgesListViewModel
    private lateinit var listAdapter: BadgesListAdapter
    private lateinit var badgesRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressView: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_badges_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!this::listViewModel.isInitialized)
            listViewModel = ViewModelProvider(this)[BadgesListViewModel::class.java]

        this.badgesRecyclerView = view.findViewById(R.id.recycler_view_badges)
        this.swipeRefreshLayout = view.findViewById(R.id.refresh_badges)
        this.progressView = view.findViewById(R.id.progress_circular)

        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(requireContext(), "dk_tag_badges"), javaClass.simpleName)
        badgesRecyclerView.layoutManager = LinearLayoutManager(view.context)
        swipeRefreshLayout.setOnRefreshListener {
            updateBadges()
        }
    }

    override fun onResume() {
        super.onResume()
        updateBadges()
    }

    private fun updateBadges() {
        listViewModel.badgesData.observe(viewLifecycleOwner) {
            if (listViewModel.syncStatus != BadgeSyncStatus.NO_ERROR) {
                Toast.makeText(context,
                    context?.getString(R.string.dk_achievements_failed_to_sync_badges),
                    Toast.LENGTH_LONG).show()
            }
            if (this::listAdapter.isInitialized) {
                listAdapter.notifyDataSetChanged()
            } else {
                listAdapter = BadgesListAdapter(view?.context, listViewModel)
                badgesRecyclerView.adapter = listAdapter
            }
            updateProgressVisibility(false)
        }
        updateProgressVisibility(true)
        listViewModel.fetchBadges()
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        progressView.visibility = if (displayProgress) {
            View.VISIBLE
        } else {
            View.GONE
        }

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
