package com.drivequant.drivekit.common.ui.utils

import android.graphics.Color
import android.view.View
import android.view.Window
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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
    fun addSystemStatusBarTopPadding(view: View, insets: Insets) {
        view.updatePadding(top = insets.top)
    }

    @JvmStatic
    fun addSystemNavigationBarBottomPadding(view: View, insets: Insets) {
        view.updatePadding(bottom = insets.bottom)
    }

    @JvmStatic
    fun update(rootView: View, callback: ((View, Insets) -> Unit)) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.statusBars())
            callback(rootView, insets)
            WindowInsetsCompat.CONSUMED
        }
    }
}