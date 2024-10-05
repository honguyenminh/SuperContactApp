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
fun ConfirmDeleteDialog(
    contactsCount: Int,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title: String
    val text: String

    if (contactsCount == 1) {
        title = "Delete contact?"
        text = "This contact will be permanently deleted from your device"
    } else {
        title = "Delete $contactsCount contacts?"
        text = "These contacts will be permanently deleted from your device"
    }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
            title = { Text(title) },
            text = { Text(text) },
            modifier = modifier
        )
    }
}

@Preview
@Composable
private fun ConfirmDeleteDialogPreview() {
    Scaffold {
        ConfirmDeleteDialog(
            contactsCount = 2,
            isVisible = true,
            onDismiss = {},
            onDelete = {},
            modifier = Modifier.padding(it)
        )
    }
}