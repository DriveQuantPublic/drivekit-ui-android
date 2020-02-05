package com.drivequant.drivekit.driverachievement.ui.streaks.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.drivekit.databaseutils.entity.streak.StreakTheme
import com.drivequant.drivekit.driverachievement.streak.AchievementSyncStatus

import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.StreaksViewConfig
import com.drivequant.drivekit.driverachievement.ui.streaks.adapter.StreakAdpater
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksViewModel
import kotlinx.android.synthetic.main.fragment_streaks_list.*


/**
 * Created by Mohamed.
 */

class StreaksListFragment : Fragment() {

    private lateinit var viewModel: StreaksViewModel
    private lateinit var streaksViewConfig: StreaksViewConfig
    private var adapter: StreakAdpater? = null

    companion object {
        fun newInstance(streaksViewConfig: StreaksViewConfig): StreaksListFragment {
            val fragment = StreaksListFragment()
            fragment.streaksViewConfig = streaksViewConfig
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_streaks_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        seekbar.setPadding(0, 0, 0, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (savedInstanceState?.getSerializable("config") as StreaksViewConfig?)?.let {
            streaksViewConfig = it
        }
        viewModel = ViewModelProviders.of(this).get(StreaksViewModel::class.java)
        updateStreaks()
    }

    override fun onResume() {
        super.onResume()
        updateStreaks()
    }

    private fun updateStreaks() {
        viewModel.streaksData.observe(this, Observer {
            if (viewModel.syncStatus != AchievementSyncStatus.NO_ERROR) {
                Toast.makeText(context, "FAILED", Toast.LENGTH_LONG).show()
            }
        })
//        viewModel.fetchStreaks(streaksViewConfig.streaksTheme)
          viewModel.fetchStreaks(listOf(StreakTheme.ACCELERATION,StreakTheme.PHONE_DISTRACTION))
    }
}
