package com.drivequant.drivekit.common.ui.utils

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.graphics.Insets
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
    fun addSystemStatusBarTopPadding(view: View, insets: Insets) {
        view.updatePadding(top = insets.top)
    }

    @JvmStatic
    fun addSystemNavigationBarBottomMargin(view: View, insets: Insets) {
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = insets.bottom
        }
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


    // TODO to be removed
    @JvmStatic
    fun addInsetsPaddings(view: View, direction: DKEdgeToEdgeDirection = DKEdgeToEdgeDirection.TOP) {
       // do nothing
    }

    // TODO to be removed
    @JvmStatic
    fun addInsetsMargins(view: View, direction: DKEdgeToEdgeDirection = DKEdgeToEdgeDirection.BOTTOM) {
        // do nothing
    }
}

// TODO to be removed
enum class DKEdgeToEdgeDirection {
    TOP, BOTTOM, VERTICAL
}