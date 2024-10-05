package com.someone.super_contact_app.ui.components.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ConfirmBackDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = { TextButton(onClick = onNavigateUp) {
                Text("Discard") }
            },
            dismissButton = { TextButton(onClick = onDismiss) {
                Text("Cancel") }
            },
            title = { Text("Discard changes?") },
            text = { Text("Any unsaved changes will be lost.") },
            modifier = modifier
        )
    }
}

@Preview
@Composable
private fun BackDialogPreview() {
    Scaffold {
        ConfirmBackDialog(isVisible = true, onDismiss = {}, onNavigateUp = {}, modifier = Modifier.padding(it))
    }
}