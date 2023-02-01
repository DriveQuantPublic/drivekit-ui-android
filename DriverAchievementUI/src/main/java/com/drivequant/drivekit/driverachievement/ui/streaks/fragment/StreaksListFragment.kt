package com.drivequant.drivekit.driverachievement.ui.streaks.fragment

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.drivekit.driverachievement.StreakSyncStatus

import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksListViewModel
import kotlinx.android.synthetic.main.dk_fragment_streaks_list.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.streaks.adapter.StreaksListAdapter


class StreaksListFragment : Fragment() {

    private lateinit var listViewModel: StreaksListViewModel
    private lateinit var listAdapter: StreaksListAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(!this::listViewModel.isInitialized) {
            listViewModel = ViewModelProvider(this)[StreaksListViewModel::class.java]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dk_fragment_streaks_list, container, false)
        view.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(requireContext(), "dk_tag_streaks"), javaClass.simpleName)

        val layoutManager =
            LinearLayoutManager(view.context)
        recycler_view_streaks.layoutManager = layoutManager

        refresh_streaks.setOnRefreshListener {
            updateStreaks()
        }
    }

    override fun onResume() {
        super.onResume()
        updateStreaks()
    }

    private fun updateStreaks() {
        listViewModel.streaksData.observe(this, Observer {
            if (listViewModel.syncStatus != StreakSyncStatus.NO_ERROR) {
                Toast.makeText(context, context?.getString(R.string.dk_achievements_failed_to_sync_streaks), Toast.LENGTH_LONG)
                    .show()
            }
            if (this::listAdapter.isInitialized) {
                listAdapter.notifyDataSetChanged()
            } else {
                listAdapter = StreaksListAdapter(view?.context, listViewModel)
                recycler_view_streaks.adapter = listAdapter
            }
            updateProgressVisibility(false)
        })
        updateProgressVisibility(true)
        listViewModel.fetchStreaks()
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular?.visibility = View.VISIBLE
            refresh_streaks?.isRefreshing = true
        } else {
            progress_circular?.visibility = View.GONE
            refresh_streaks?.visibility = View.VISIBLE
            refresh_streaks?.isRefreshing = false
        }
    }
}
