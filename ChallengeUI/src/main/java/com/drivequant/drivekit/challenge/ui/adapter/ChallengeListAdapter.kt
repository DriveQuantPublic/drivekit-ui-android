package com.drivequant.drivekit.challenge.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.viewholder.ChallengeViewHolder
import com.drivequant.drivekit.challenge.ui.viewmodel.ChallengeListViewModel
import com.drivequant.drivekit.challenge.ui.viewmodel.containsActiveChallenge
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus

internal class ChallengeListAdapter(
    val context: Context,
    private val viewModel: ChallengeListViewModel,
    private val status: List<ChallengeStatus>
) :
    RecyclerView.Adapter<ChallengeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.dk_challenge_list_item, parent, false)
        return ChallengeViewHolder(view)
    }

    override fun getItemCount(): Int =
        if (status.containsActiveChallenge()) {
            viewModel.activeChallenges.size
        } else {
            viewModel.finishedChallenges.size
        }

    override fun onBindViewHolder(parent: ChallengeViewHolder, position: Int) {
        val challenges =
            if (status.containsActiveChallenge()) {
                viewModel.activeChallenges
            } else {
                viewModel.finishedChallenges
            }
        parent.bind(challenges[position])
    }
}