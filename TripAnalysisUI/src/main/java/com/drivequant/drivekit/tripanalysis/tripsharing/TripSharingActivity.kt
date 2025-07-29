package com.drivequant.drivekit.tripanalysis.tripsharing

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.component.DKCriticalActionButton
import com.drivequant.drivekit.common.ui.component.DKPrimaryButton
import com.drivequant.drivekit.common.ui.component.DKText
import com.drivequant.drivekit.common.ui.component.DKTextButton
import com.drivequant.drivekit.common.ui.graphical.DKStyle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.common.ui.utils.DurationUnit

internal open class TripSharingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DKEdgeToEdgeManager.setSystemStatusBarForegroundColor(window)
        setContent {
            val viewModel: TripSharingViewModel = viewModel()
            TripSharingScreen(
                viewModel.uiState,
                setupTripSharing = { viewModel.setupTripSharing() },
                cancelSetupTripSharing = { viewModel.cancelSetupTripSharing() },
                activateTripSharing = { duration -> viewModel.activateTripSharing(duration) },
                shareLink = { shareLink(viewModel.uiState.link) },
                stopSharing = { viewModel.revokeLink() },
                errorShown = { viewModel.errorShown() }
            )
        }
    }

    private fun shareLink(url: String?) {
        if (url != null) {
            val share = Intent.createChooser(Intent().apply {
                type = "text/plain"
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.dk_location_sharing_sharesheet_content, getString(R.string.app_name), url))
                // Title of the content.
                putExtra(Intent.EXTRA_TITLE, getString(R.string.dk_location_sharing_sharesheet_title))
                // Subject of the mail.
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.dk_location_sharing_sharesheet_title))
            }, null)
            startActivity(share)
        }
    }

    @Composable
    fun TripSharingScreen(
        uiState: TripSharingUiState,
        setupTripSharing: () -> Unit = { },
        cancelSetupTripSharing: () -> Unit = { },
        activateTripSharing: (durationInSeconds: Int) -> Unit = { },
        shareLink: () -> Unit = { },
        stopSharing: () -> Unit = { },
        errorShown: () -> Unit = { },
    ) {
        var showRevokeConfirmationDialog by remember { mutableStateOf(false) }

        if (showRevokeConfirmationDialog) {
            RevokeLinkConfirmationDialog(stopSharing) { showRevokeConfirmationDialog = false }
        }
        if (uiState.hasError) {
            ErrorDialog(errorShown)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(colorResource(com.drivequant.drivekit.common.ui.R.color.colorPrimaryDark))
        )
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            topBar = { AppBar() },
        ) { innerPadding ->
            AnimateContentIfNeeded(uiState = uiState) { uiState ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(com.drivequant.drivekit.common.ui.R.dimen.dk_margin_medium)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TripSharingImage(uiState = uiState)
                    Description(uiState = uiState)
                    Spacer(Modifier.weight(1f))
                    Actions(
                        uiState = uiState,
                        setupTripSharing = setupTripSharing,
                        cancelSetupTripSharing = cancelSetupTripSharing,
                        activateTripSharing = activateTripSharing,
                        shareLink = shareLink,
                        stopSharing = {
                            showRevokeConfirmationDialog = true
                        },
                    )
                }
            }
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .background(Color(0.8f, 0.8f, 0.8f, 0.5f))
                        .fillMaxSize()
                        .pointerInput(Unit) { detectTapGestures { } },
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = Color.Gray
                    )
                }
            }
        }
    }

    @Composable
    private fun AnimateContentIfNeeded(uiState: TripSharingUiState, content: @Composable (targetState: TripSharingUiState) -> Unit) {
        if (!uiState.isLoading) {
            AnimatedContent(targetState = uiState) {
                content(it)
            }
        } else {
            content(uiState)
        }
    }

    @Composable
    private fun TripSharingImage(uiState: TripSharingUiState) {

        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val maxWidth = if (isPortrait) 0.6f else 0.2f
        val imageResource = if (uiState.link != null) {
            R.drawable.dk_trip_analysis_location_sharing_active
        } else if (uiState.isCreatingLink) {
            R.drawable.dk_trip_analysis_location_sharing_set_duration
        } else {
            R.drawable.dk_trip_analysis_location_sharing_inactive
        }
        Image(
            painterResource(imageResource),
            contentDescription = "Illustration",
            Modifier
                .fillMaxWidth(maxWidth)
                .aspectRatio(1f)
        )
    }

    @Composable
    private fun Description(uiState: TripSharingUiState) {
        if (uiState.link != null) {
            val (remainingTime, unit) = uiState.linkDuration ?: Pair(0, null)
            DKText(
                text = stringResource(R.string.dk_location_sharing_active_status),
                style = DKStyle.NORMAL_TEXT,
                modifier = Modifier.fillMaxWidth(),
            )
            DKText(
                text = stringResource(R.string.dk_location_sharing_active_info),
                style = DKStyle.NORMAL_TEXT,
                modifier = Modifier.fillMaxWidth(),
            )
            DKText(
                text = when (unit) {
                    DurationUnit.MINUTE -> if (remainingTime > 1) {
                        stringResource(R.string.dk_location_sharing_active_info_minutes, remainingTime)
                    } else {
                        stringResource(R.string.dk_location_sharing_active_info_minute)
                    }

                    DurationUnit.HOUR -> if (remainingTime > 1) {
                        stringResource(R.string.dk_location_sharing_active_info_hours, remainingTime)
                    } else {
                        stringResource(R.string.dk_location_sharing_active_info_hour)
                    }

                    DurationUnit.DAY -> if (remainingTime > 1) {
                        stringResource(R.string.dk_location_sharing_active_info_days, remainingTime)
                    } else {
                        stringResource(R.string.dk_location_sharing_active_info_day)
                    }

                    else -> ""
                },
                style = DKStyle.NORMAL_TEXT,
                modifier = Modifier.fillMaxWidth(),
            )
        } else if (uiState.isCreatingLink) {
            DKText(
                text = stringResource(R.string.dk_location_sharing_select_description_1),
                style = DKStyle.NORMAL_TEXT,
                modifier = Modifier.fillMaxWidth(),
            )
            DKText(
                text = stringResource(R.string.dk_location_sharing_select_description_2),
                style = DKStyle.NORMAL_TEXT,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            DKText(
                text = stringResource(R.string.dk_location_sharing_inactive_description_1),
                style = DKStyle.NORMAL_TEXT,
                modifier = Modifier.fillMaxWidth(),
            )
            DKText(
                text = stringResource(R.string.dk_location_sharing_inactive_description_2),
                style = DKStyle.NORMAL_TEXT,
                modifier = Modifier.fillMaxWidth(),
            )
            DKText(
                text = stringResource(R.string.dk_location_sharing_inactive_description_3),
                style = DKStyle.NORMAL_TEXT,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    @Composable
    private fun Actions(
        uiState: TripSharingUiState,
        setupTripSharing: () -> Unit,
        cancelSetupTripSharing: () -> Unit,
        activateTripSharing: (durationInSeconds: Int) -> Unit,
        shareLink: () -> Unit,
        stopSharing: () -> Unit
    ) {
        if (uiState.link != null) {
            DKPrimaryButton(stringResource(R.string.dk_location_sharing_active_share_link),  onClick = shareLink)
            DKCriticalActionButton(stringResource(R.string.dk_location_sharing_active_disable),  onClick = stopSharing)
        } else if (uiState.isCreatingLink) {
            val dayDuration = 24 * 3600
            val weekDuration = dayDuration * 7
            val monthDuration = dayDuration * 30
            DKPrimaryButton(stringResource(R.string.dk_location_sharing_select_day)) { activateTripSharing(dayDuration) }
            DKPrimaryButton(stringResource(R.string.dk_location_sharing_select_week)) { activateTripSharing(weekDuration) }
            DKPrimaryButton(stringResource(R.string.dk_location_sharing_select_month)) { activateTripSharing(monthDuration) }
            DKCriticalActionButton(stringResource(R.string.dk_location_sharing_select_cancel), onClick = cancelSetupTripSharing)
        } else {
            DKPrimaryButton(stringResource(R.string.dk_location_sharing_inactive_enable), onClick = setupTripSharing)
        }
    }

    @Composable
    private fun RevokeLinkConfirmationDialog(confirmAction: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            text = {
                Text(
                    text = stringResource(R.string.dk_location_sharing_active_disable_confirmation)
                )
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                DKTextButton(stringResource(R.string.dk_location_sharing_active_disable)) {
                    onDismiss()
                    confirmAction()
                }
            },
            dismissButton = {
                DKTextButton(
                    stringResource(com.drivequant.drivekit.common.ui.R.string.dk_common_cancel),
                    onClick = onDismiss
                )
            },
        )
    }

    @Composable
    private fun ErrorDialog(onDismiss: () -> Unit) {
        AlertDialog(
            text = {
                Text(stringResource(com.drivequant.drivekit.common.ui.R.string.dk_common_error_message))
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                DKTextButton(stringResource(com.drivequant.drivekit.common.ui.R.string.dk_common_ok)) {
                    onDismiss()
                }
            },
        )
    }

    @Composable
    private fun AppBar() {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.dk_location_sharing_title),
                    style = LocalTextStyle.current.copy(fontFamily = DKStyle.secondaryFont),
                )
            },
            navigationIcon = {
                IconButton(onClick = { finish() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(com.drivequant.drivekit.common.ui.R.string.dk_common_back),
                    )
                }
            },
            backgroundColor = colorResource(com.drivequant.drivekit.common.ui.R.color.primaryColor),
            contentColor = colorResource(com.drivequant.drivekit.common.ui.R.color.fontColorOnPrimaryColor),
        )
    }
}
