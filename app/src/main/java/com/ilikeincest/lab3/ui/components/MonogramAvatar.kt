package com.ilikeincest.lab3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MonogramAvatar(
    monogram: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    textStyle: TextStyle = typography.bodySmall
) {
    //text with a background circle
    val bgColor = colorScheme.primaryContainer
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .size(size)
            .background(color = bgColor, shape = CircleShape)
    ) {
        Text(
            text = monogram, style = textStyle,
            color = colorScheme.onPrimaryContainer,
        )
    }
}

@Preview
@Composable
private fun MonogramAvatarPreview() {
    MonogramAvatar("NG", modifier = Modifier)
}