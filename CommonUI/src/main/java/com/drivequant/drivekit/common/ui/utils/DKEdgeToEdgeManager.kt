package com.drivequant.drivekit.common.ui.utils

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.shouldInvertTextColor
import com.drivequant.drivekit.core.DriveKit

object DKEdgeToEdgeManager {
    fun setSystemStatusBarForegroundColor(window: Window) {
        val color = DriveKit.applicationContext.getColor(R.color.colorPrimaryDark)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = color.shouldInvertTextColor(Color.WHITE)
    }

    fun addSystemStatusBarTopPadding(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }
    }

    fun addSystemNavigationBarBottomMargin(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.bottom
            }
            WindowInsetsCompat.CONSUMED
        }
    }
}