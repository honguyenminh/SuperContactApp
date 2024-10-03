package com.ilikeincest.lab3.ui.components

import android.accounts.AuthenticatorDescription
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent

// Async rounded avatar with monogram fallback if uri is invalid or null
@Composable
fun AsyncAvatarFallbackMonogram(
    model: Any?, contentDescription: String?,
    monogram: String,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = model,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
    ) {
        val state = painter.state
        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
            MonogramAvatar(monogram, textStyle = typography.titleMedium)
        } else {
            SubcomposeAsyncImageContent()
        }
    }
}

@Preview
@Composable
private fun AsyncAvatarPreview() {
    AsyncAvatarFallbackMonogram(model = null, contentDescription = null, monogram = "NG")
}