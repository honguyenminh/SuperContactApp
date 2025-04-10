package com.someone.super_contact_app.ui.screens.contact_detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.someone.super_contact_app.R
import com.someone.super_contact_app.ui.theme.Lab3Theme

@Composable
fun ContactActionButton(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(colorScheme.primaryContainer)
                .padding(12.dp)
        ) {
            icon()
        }
        Spacer(Modifier.height(12.dp))
        Text(label)
    }
}

@Composable
fun ContactActionRow(
    onCall: () -> Unit,
    onText: () -> Unit,
    onVideoCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        ContactActionButton(
            icon = { Icon(Icons.Outlined.Call, null) },
            label = "Call",
            onClick = onCall,
            modifier = Modifier.weight(1f)
        )
        ContactActionButton(
            icon = { Icon(painterResource(R.drawable.forum_24px), null) },
            label = "Text",
            onClick = onText,
            modifier = Modifier.weight(1f)
        )
        ContactActionButton(
            icon = { Icon(painterResource(R.drawable.videocam_24px), null) },
            label = "Video",
            onClick = onVideoCall,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
private fun ActionRowPreview() {
    Lab3Theme { ContactActionRow(
        onCall = {},
        onText = {},
        onVideoCall = {},
        modifier = Modifier
            .background(colorScheme.background)
            .padding(vertical = 12.dp, horizontal = 32.dp)
    ) }
}

@Preview
@Composable
private fun ActionBtnPrev() {
    Lab3Theme { ContactActionButton(
        icon = { Icon(Icons.Outlined.Call, null) },
        label = "Call",
        onClick = {},
        modifier = Modifier
            .background(colorScheme.background)
            .width(120.dp)
    ) }
}