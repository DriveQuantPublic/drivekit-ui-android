package com.drivequant.drivekit.tripanalysis.tripsharing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.component.DKPrimaryButton
import com.drivequant.drivekit.common.ui.component.DKText
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.graphical.DKStyle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager

internal class TripSharingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DKEdgeToEdgeManager.setSystemStatusBarForegroundColor(window)
        setContent {
            Content()
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun Content() {
        Box(
            modifier = Modifier
                .background(colorResource(com.drivequant.drivekit.common.ui.R.color.colorPrimaryDark))
                .systemBarsPadding(),
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = { AppBar() },
                backgroundColor = Color(DKColors.backgroundViewColor),
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(com.drivequant.drivekit.common.ui.R.dimen.dk_margin_medium)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painterResource(R.drawable.dk_trip_analysis_location_sharing_inactive),
                        contentDescription = "Illustration",
                        Modifier
                            .fillMaxWidth(0.6f)
                            .aspectRatio(1.0f)
                    )
                    DKText(
                        text = stringResource(R.string.dk_location_sharing_inactive_description_1),
                        style = DKStyle.NORMAL_TEXT,
                    )
                    DKText(
                        text = stringResource(R.string.dk_location_sharing_inactive_description_2),
                        style = DKStyle.NORMAL_TEXT,
                    )
                    DKText(
                        text = stringResource(R.string.dk_location_sharing_inactive_description_3),
                        style = DKStyle.NORMAL_TEXT,
                    )
                    Spacer(Modifier.weight(1f))
                    DKPrimaryButton(stringResource(R.string.dk_location_sharing_inactive_enable)) { }
                }
            }
        }
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
                        contentDescription = "Back",
                    )
                }
            },
            backgroundColor = Color(DKColors.primaryColor),
            contentColor = Color(DKColors.fontColorOnPrimaryColor),
        )
    }
}
