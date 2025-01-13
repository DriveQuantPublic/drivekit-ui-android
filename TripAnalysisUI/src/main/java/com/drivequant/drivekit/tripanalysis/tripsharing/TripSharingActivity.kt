package com.drivequant.drivekit.tripanalysis.tripsharing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
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

internal class TripSharingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DKEdgeToEdgeManager.setSystemStatusBarForegroundColor(window)
        setContent {
            val viewModel: TripSharingViewModel = viewModel()
            TripSharing(
                viewModel.uiState,
                setupTripSharing = { viewModel.setupTripSharing() },
                cancelSetupTripSharing = { viewModel.cancelSetupTripSharing() },
                activateTripSharing = { duration -> viewModel.activateTripSharing(duration) },
                shareLink = { }, //TODO
                stopSharing = { viewModel.revokeLink() },
            )
        }
    }

    @Composable
    private fun TripSharing(
        uiState: TripSharingUiState,
        setupTripSharing: () -> Unit = { },
        cancelSetupTripSharing: () -> Unit = { },
        activateTripSharing: (durationInSeconds: Int) -> Unit = { },
        shareLink: () -> Unit = { },
        stopSharing: () -> Unit = { },
    ) {
        var showRevokeConfirmationDialog by remember { mutableStateOf(false) }

        if (showRevokeConfirmationDialog) {
            RevokeLinkConfirmationDialog(stopSharing) { showRevokeConfirmationDialog = false }
        }

        Box(
            modifier = Modifier
                .background(colorResource(com.drivequant.drivekit.common.ui.R.color.colorPrimaryDark))
                .systemBarsPadding(),
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = { AppBar() },
                backgroundColor = colorResource(com.drivequant.drivekit.common.ui.R.color.backgroundViewColor),
            ) { innerPadding ->
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
    }

    @Composable
    private fun TripSharingImage(uiState: TripSharingUiState) {
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
                .fillMaxWidth(0.6f)
                .aspectRatio(1.0f)
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

    @Preview(showBackground = true)
    @Composable
    private fun TripSharingPreview(@PreviewParameter(TripSharingPreviewParameterProvider::class) state: TripSharingUiState) {
        TripSharing(state)
    }
}
