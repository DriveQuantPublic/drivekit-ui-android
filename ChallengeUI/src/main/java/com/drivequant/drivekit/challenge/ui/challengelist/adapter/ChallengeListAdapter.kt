package com.drivequant.drivekit.challenge.ui.challengelist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengelist.viewholder.ChallengeViewHolder
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.ChallengeData
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.ChallengeListener

internal class ChallengeListAdapter(
    val context: Context,
    private val challenges: List<ChallengeData>,
    private val listener: ChallengeListener
) :
    RecyclerView.Adapter<ChallengeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dk_challenge_list_item, parent, false)
        return ChallengeViewHolder(view)
    }

    override fun getItemCount(): Int = this.challenges.size

    override fun onBindViewHolder(parent: ChallengeViewHolder, position: Int) {
        val challenge = this.challenges[position]
        parent.bind(challenge)
        parent.itemView.setOnClickListener {
            listener.onClickChallenge(challenge)
        }
    }
}