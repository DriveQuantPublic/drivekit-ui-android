package com.drivequant.drivekit.timeline.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.utils.DKWeakList

internal class DispatchTouchFrameLayout constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private val interceptMotionEventListeners = DKWeakList<OnInterceptMotionEventListener>()

    fun addOnInterceptMotionEventListener(listener: OnInterceptMotionEventListener) {
        this.interceptMotionEventListeners.add(listener)
    }

    fun removeOnInterceptMotionEventListener(listener: OnInterceptMotionEventListener) {
        this.interceptMotionEventListeners.remove(listener)
    }

    fun removeAllOnInterceptMotionEventListeners() {
        this.interceptMotionEventListeners.clear()
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        this.interceptMotionEventListeners.forEach {
            it.onInterceptMotionEvent(motionEvent)
        }
        return super.dispatchTouchEvent(motionEvent)
    }

}

internal class DispatchTouchLinearLayout constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val interceptMotionEventListeners = DKWeakList<OnInterceptMotionEventListener>()

    fun addOnInterceptMotionEventListener(listener: OnInterceptMotionEventListener) {
        this.interceptMotionEventListeners.add(listener)
    }

    fun removeOnInterceptMotionEventListener(listener: OnInterceptMotionEventListener) {
        this.interceptMotionEventListeners.remove(listener)
    }

    fun removeAllOnInterceptMotionEventListeners() {
        this.interceptMotionEventListeners.clear()
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        this.interceptMotionEventListeners.forEach {
            it.onInterceptMotionEvent(motionEvent)
        }
        return super.dispatchTouchEvent(motionEvent)
    }

}

internal interface OnInterceptMotionEventListener {
    fun onInterceptMotionEvent(motionEvent: MotionEvent?)
}
