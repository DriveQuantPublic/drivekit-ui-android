package com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class JoinChallengeViewModel : ViewModel() {

    var syncJoinChallengeError: MutableLiveData<Any> = MutableLiveData()
        private set

    fun joinChallenge(challengeId: String) {

    }

    fun shouldDisplayJoinChallenge(): Boolean {
        return true
    }

    fun shouldDisplayCountDown(): Boolean {
        return true
    }

    fun shouldDisplayProgressBars(): Boolean {
        return true
    }
}