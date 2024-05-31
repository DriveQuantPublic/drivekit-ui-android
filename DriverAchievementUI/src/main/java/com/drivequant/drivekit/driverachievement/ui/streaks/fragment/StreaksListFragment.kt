package com.drivequant.drivekit.driverachievement.ui.streaks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.driverachievement.StreakSyncStatus
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.streaks.adapter.StreaksListAdapter
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksListViewModel

class StreaksListFragment : Fragment() {

    private lateinit var listViewModel: StreaksListViewModel
    private lateinit var listAdapter: StreaksListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressView: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dk_fragment_streaks_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!this::listViewModel.isInitialized) {
            listViewModel = ViewModelProvider(this)[StreaksListViewModel::class.java]
        }

        this.recyclerView = view.findViewById(R.id.recycler_view_streaks)
        this.swipeRefreshLayout = view.findViewById(R.id.refresh_streaks)
        this.progressView = view.findViewById(R.id.progress_circular)

        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_streaks), javaClass.simpleName)

        val layoutManager =
            LinearLayoutManager(view.context)
        recyclerView.layoutManager = layoutManager

        swipeRefreshLayout.setOnRefreshListener {
            updateStreaks()
        }
    }

    override fun onResume() {
        super.onResume()
        updateStreaks()
    }

    private fun updateStreaks() {
        listViewModel.streaksData.observe(viewLifecycleOwner, Observer {
            if (listViewModel.syncStatus != StreakSyncStatus.NO_ERROR) {
                Toast.makeText(context, context?.getString(R.string.dk_achievements_failed_to_sync_streaks), Toast.LENGTH_LONG)
                    .show()
            }
            if (this::listAdapter.isInitialized) {
                listAdapter.notifyDataSetChanged()
            } else {
                listAdapter = StreaksListAdapter(view?.context, listViewModel)
                recyclerView.adapter = listAdapter
            }
            updateProgressVisibility(false)
        })
        updateProgressVisibility(true)
        listViewModel.fetchStreaks()
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
