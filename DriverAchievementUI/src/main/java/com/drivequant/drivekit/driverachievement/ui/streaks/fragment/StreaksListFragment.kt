package com.drivequant.drivekit.driverachievement.ui.streaks.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.drivekit.driverachievement.streak.AchievementSyncStatus

import com.drivequant.drivekit.driverachievement.ui.StreaksViewConfig
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksListViewModel
import kotlinx.android.synthetic.main.fragment_streaks_list.*
import android.support.v7.widget.LinearLayoutManager
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.streaks.adapter.StreaksListAdapter


class StreaksListFragment : Fragment() {

    private lateinit var listViewModel: StreaksListViewModel
    private lateinit var streaksViewConfig: StreaksViewConfig
    private var listAdapter: StreaksListAdapter? = null

    companion object {
        fun newInstance(streaksViewConfig: StreaksViewConfig): StreaksListFragment {
            val fragment = StreaksListFragment()
            fragment.streaksViewConfig = streaksViewConfig
            return fragment
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listViewModel = ViewModelProviders.of(this).get(StreaksListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_streaks_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (savedInstanceState?.getSerializable("config") as StreaksViewConfig?)?.let {
            streaksViewConfig = it
        }

        val layoutManager = LinearLayoutManager(view.context)
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
            if (listViewModel.syncStatus != AchievementSyncStatus.NO_ERROR) {
                Toast.makeText(context, streaksViewConfig.failedToSyncStreaks, Toast.LENGTH_LONG)
                    .show()
            }
            if (listAdapter != null) {
                listAdapter?.notifyDataSetChanged()
            } else {
                listAdapter = StreaksListAdapter(view?.context, listViewModel, streaksViewConfig)
                recycler_view_streaks.adapter = listAdapter
            }
            updateProgressVisibility(false)
        })
        updateProgressVisibility(true)
        listViewModel.fetchStreaks(streaksViewConfig.streaksTheme)
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
            refresh_streaks.isRefreshing = true
        } else {
            progress_circular.visibility = View.GONE
            refresh_streaks.visibility = View.VISIBLE
            refresh_streaks.isRefreshing = false
        }
    }
}
