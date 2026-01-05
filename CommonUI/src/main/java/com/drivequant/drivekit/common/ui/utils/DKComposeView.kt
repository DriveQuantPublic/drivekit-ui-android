package com.drivequant.drivekit.common.ui.utils

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.doOnAttach

fun ComposeView.injectContent(content: @Composable () -> Unit) {
    this.apply {
        doOnAttach {
            // Crash occurs in View interop case
            // https://issuetracker.google.com/issues/369256395
            (it as? ViewGroup)?.getChildAt(0)?.isFocusable = false
        }
        setContent(content)
    }
}