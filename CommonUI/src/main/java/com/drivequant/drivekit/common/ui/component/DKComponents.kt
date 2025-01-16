package com.drivequant.drivekit.common.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.drivequant.drivekit.common.ui.graphical.DKStyle

@Composable
fun DKText(text: String, style: DKStyle, color: Color = colorResource(com.drivequant.drivekit.common.ui.R.color.mainFontColor), modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = style.dkTextStyle(color = color),
    )
}

@Composable
fun DKText(text: AnnotatedString, style: DKStyle, color: Color = colorResource(com.drivequant.drivekit.common.ui.R.color.mainFontColor), modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = style.dkTextStyle(color = color),
    )
}

@Composable
fun DKPrimaryButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.defaultMinSize(minHeight = 48.dp).fillMaxWidth(),
        colors = buttonColors(
            backgroundColor = colorResource(com.drivequant.drivekit.common.ui.R.color.secondaryColor),
            contentColor = colorResource(com.drivequant.drivekit.common.ui.R.color.fontColorOnSecondaryColor)
        ),
        elevation = null,
        shape = CircleShape,
        onClick = onClick
    ) {
        DKText(text.uppercase(), DKStyle.HEADLINE2, color = Color.Unspecified)
    }
}

@Composable
fun DKSecondaryButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier.defaultMinSize(minHeight = 48.dp).fillMaxWidth(),
        colors = buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = colorResource(com.drivequant.drivekit.common.ui.R.color.secondaryColor)
        ),
        elevation = null,
        shape = CircleShape,
        border = BorderStroke(1.75.dp, colorResource(com.drivequant.drivekit.common.ui.R.color.secondaryColor)),
        onClick = onClick
    ) {
        DKText(text.uppercase(), DKStyle.HEADLINE2, color = Color.Unspecified)
    }
}

@Composable
fun DKTextButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    TextButton(
        modifier = modifier,
        colors = buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = colorResource(com.drivequant.drivekit.common.ui.R.color.secondaryColor)
        ),
        onClick = onClick
    ) {
        DKText(text.uppercase(), DKStyle.NORMAL_TEXT, color = Color.Unspecified)
    }
}

@Composable
fun DKCriticalActionButton(text: String, onClick: () -> Unit) {
    TextButton(
        modifier = Modifier.defaultMinSize(minHeight = 48.dp).fillMaxWidth(),
        colors = buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = colorResource(com.drivequant.drivekit.common.ui.R.color.criticalColor)
        ),
        onClick = onClick
    ) {
        DKText(text, DKStyle.HEADLINE2, color = Color.Unspecified)
    }
}
