package com.drivequant.drivekit.challenge.ui.joinchallenge.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import kotlinx.android.synthetic.main.dk_fragment_challenge_join.*


class ChallengeJoinFragment : Fragment() {

    private lateinit var challengeId: String

    companion object {
        fun newInstance(challengeId:String): ChallengeJoinFragment {
            val fragment = ChallengeJoinFragment()
            fragment.challengeId = challengeId
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dk_fragment_challenge_join, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle()
    }

    private fun setStyle() {
        view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        view_separator_1.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}