package com.drivequant.drivekit.common.ui.utils

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.shouldInvertTextColor

object DKEdgeToEdgeManager {

    @JvmStatic
    fun setSystemStatusBarForegroundColor(window: Window) {
        val color = window.context.getColor(R.color.colorPrimaryDark)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = color.shouldInvertTextColor(Color.WHITE)
    }

    @JvmStatic
    fun addInsetsPaddings(view: View, direction: DKEdgeToEdgeDirection = DKEdgeToEdgeDirection.TOP) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            when (direction) {
                DKEdgeToEdgeDirection.TOP -> v.updatePadding(top = insets.top)
                DKEdgeToEdgeDirection.BOTTOM -> v.updatePadding(bottom = insets.bottom)
                DKEdgeToEdgeDirection.VERTICAL-> v.updatePadding(top = insets.top, bottom = insets.bottom)
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    @JvmStatic
    fun addInsetsMargins(view: View, direction: DKEdgeToEdgeDirection = DKEdgeToEdgeDirection.BOTTOM) {
        var isConsumed = false // for some reason, the listener can be triggered multiple times
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            if (!isConsumed) {
                when (direction) {
                    DKEdgeToEdgeDirection.TOP -> {
                        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            topMargin = v.marginTop + insets.top
                        }
                    }

                    DKEdgeToEdgeDirection.BOTTOM -> {
                        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            bottomMargin = v.marginBottom + insets.bottom
                        }
                    }

                    DKEdgeToEdgeDirection.VERTICAL -> {
                        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            topMargin = v.marginTop + insets.top
                            bottomMargin = v.marginBottom + insets.bottom
                        }
                    }
                }
            }
            isConsumed = true
            WindowInsetsCompat.CONSUMED
        }
    }
}

enum class DKEdgeToEdgeDirection {
    TOP, BOTTOM, VERTICAL
}