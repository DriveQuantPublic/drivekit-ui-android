package com.drivequant.drivekit.ui.synthesiscards

import android.content.Context
import android.text.Spannable
import android.text.SpannableString

interface DKTripCardInfo {
    val context: Context
    fun getIcon(): Int
    fun getText(): Spannable
}

sealed class TripCardInfo(override val context: Context) : DKTripCardInfo{
    data class SUM(override val context: Context) : TripCardInfo(context)

    override fun getIcon(): Int {
        return when (this){
            is SUM -> 1 // WIP
        }
    }

    override fun getText(): Spannable {
        return when (this){
            is SUM -> SpannableString("text") // TODO WIP
        }
    }
}
