package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import com.drivequant.drivekit.common.ui.component.DKPrimaryButton
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.common.ui.utils.injectContent
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.core.utils.PermissionStatus
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.databinding.ActivityFullScreenIntentPermissionBinding
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class FullScreenIntentPermissionActivity : BasePermissionActivity() {
    private val buttonText by lazy { mutableStateOf(getString(R.string.dk_perm_utils_text_button_fsi_settings)) }

    private lateinit var binding: ActivityFullScreenIntentPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenIntentPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar(R.string.dk_perm_utils_fsi_activity_title)
        setStyle()
        manageSkipButton()

        binding.buttonRequestFullScreenIntentPermission.injectContent {
            DKPrimaryButton(buttonText.value) {
                DiagnosisHelper.requestFullScreenPermission(this)
            }
        }

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            update(binding.root) { view, insets ->
                addSystemStatusBarTopPadding(findViewById(R.id.toolbar), insets)
                addSystemNavigationBarBottomPadding(view, insets)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DiagnosisHelper.REQUEST_FULL_SCREEN_INTENT
            && DiagnosisHelper.getFullScreenIntentStatus(this) == PermissionStatus.VALID
        ) {
            forward()
        }
    }

    private fun manageSkipButton() {
        binding.buttonSkip.setOnClickListener {
            skip(PermissionView.FULL_SCREEN_INTENT)
        }
    }

    private fun setStyle() {
        binding.textViewFullScreenIntentPermissionTitle.highlightMedium()
        binding.textViewFullScreenIntentPermissionText.normalText()
        binding.buttonSkip.normalText()
        window.decorView.setBackgroundColor(DKColors.backgroundViewColor)
    }
}
