package com.drivequant.drivekit.challenge.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.viewholder.ChallengeViewHolder
import com.drivequant.drivekit.challenge.ui.viewmodel.ChallengeListViewModel

class ChallengeListAdapter(val context: Context,private val viewModel: ChallengeListViewModel) :
    RecyclerView.Adapter<ChallengeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dk_challenge_list_item, parent, false)
        return ChallengeViewHolder(view)
    }

    override fun getItemCount(): Int = viewModel.filteredChallenge.size

    override fun onBindViewHolder(parent: ChallengeViewHolder, position: Int) {
        parent.bind(viewModel.filteredChallenge[position])
    }
}