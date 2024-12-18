package com.drivequant.drivekit.tripanalysis.tripsharing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager

internal class TripSharingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DKEdgeToEdgeManager.setSystemStatusBarForegroundColor(window)
        setContent {
            Box(
                modifier = Modifier
                    .background(colorResource(com.drivequant.drivekit.common.ui.R.color.colorPrimaryDark))
                    .systemBarsPadding()
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AppBar() },
                    backgroundColor = Color(DKColors.backgroundViewColor)
                ) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    private fun AppBar() {
        TopAppBar(
            title = {
                Text(stringResource(R.string.dk_location_sharing_title))
            },
            navigationIcon = {
                IconButton(onClick = { finish() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            },
            backgroundColor = Color(DKColors.primaryColor),
            contentColor = Color(DKColors.fontColorOnPrimaryColor)
        )
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Greeting("Android")
    }
}
