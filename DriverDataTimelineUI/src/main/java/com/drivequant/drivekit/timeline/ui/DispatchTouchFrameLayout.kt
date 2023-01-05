package com.drivequant.drivekit.timeline.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

internal class DispatchTouchFrameLayout constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    var interceptMotionEventListener: OnInterceptMotionEventListener? = null

    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        this.interceptMotionEventListener?.onInterceptMotionEvent(motionEvent)
        return super.dispatchTouchEvent(motionEvent)
    }

}

internal interface OnInterceptMotionEventListener {
    fun onInterceptMotionEvent(motionEvent: MotionEvent?)
}
