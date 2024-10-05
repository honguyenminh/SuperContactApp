package com.someone.super_contact_app.ui.screens.contact_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.someone.super_contact_app.R
import com.someone.super_contact_app.data.deleteContacts
import com.someone.super_contact_app.data.getContact
import com.someone.super_contact_app.data.updateContact
import com.someone.super_contact_app.model.Contact
import com.someone.super_contact_app.ui.components.AsyncAvatarFallbackMonogram
import com.someone.super_contact_app.ui.components.dialogs.ConfirmBackDialog
import com.someone.super_contact_app.ui.components.dialogs.ConfirmDeleteDialog
import com.someone.super_contact_app.ui.components.getMonogram

@Composable
fun ContactEditScreen(
    contactId: String,
    onNavigateUp: () -> Unit,
    onNavigateHome: () -> Unit,
) {
    val context = LocalContext.current
    val contact = remember { getContact(contactId, context) }
    ContactEditScreenContent(contact, onNavigateUp, onNavigateHome)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactEditScreenContent(
    contact: Contact,
    onNavigateUp: () -> Unit,
    onNavigateHome: () -> Unit,
) {
    var showMoreActions by remember { mutableStateOf(false) }
    var showConfirmBackDialog by rememberSaveable { mutableStateOf(false) }
    var showConfirmDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var name by remember { mutableStateOf(contact.name) }
    var phones by rememberSaveable { mutableStateOf(contact.phoneNumber) }
    val context = LocalContext.current

    ConfirmDeleteDialog(
        contactsCount = 1,
        isVisible = showConfirmDeleteDialog,
        onDismiss = { showConfirmDeleteDialog = false },
        onDelete = {
            showConfirmDeleteDialog = false
            deleteContacts(listOf(contact.id), context)
            onNavigateHome()
        }
    )

    ConfirmBackDialog(
        isVisible = showConfirmBackDialog,
        onDismiss = { showConfirmBackDialog = false },
        onNavigateUp = onNavigateUp
    )

    Scaffold(
        topBar = { TopAppBar(title = {},
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            actions = {
                Button(
                    onClick = {
                        updateContact(
                            contactId = contact.id,
                            name = name,
                            phones = phones,
                            context
                        )
                        onNavigateUp()
                    },
                    enabled = name.isNotBlank() && phones.any { it.isNotBlank() }
                ) {
                    Text("Save")
                }
                IconButton(onClick = { showMoreActions = true }) {
                    Icon(Icons.Default.MoreVert, "More")
                }
                DropdownMenu(
                    expanded = showMoreActions,
                    onDismissRequest = { showMoreActions = false }) {
                    DropdownMenuItem(text = { Text("Delete") }, onClick = {
                        showMoreActions = false
                        showConfirmDeleteDialog = true
                    })
                }
            }
        ) },
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            AsyncAvatarFallbackMonogram(
                model = contact.photoUri,
                contentDescription = "Contact photo",
                monogram = getMonogram(name),
                size = 180.dp,
                textStyle = typography.displayLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 72.sp
                ),
                modifier = Modifier.padding(vertical = 36.dp)
            )
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Display name") },
                textStyle = typography.bodyLarge,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            phones.forEachIndexed { index, value ->
                OutlinedTextField(
                    value = value,
                    onValueChange = {
                        // only allow digits
                        val newPhone = it.filter { char -> char.isDigit() }
                        phones = phones.toMutableList().apply { set(index, newPhone) }
                    },
                    label = { Text("Phone number #${index+1}") },
                    textStyle = typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { phones = phones.toMutableList().apply { removeAt(index) } },
                        ) {
                            Icon(painterResource(R.drawable.variable_remove), "Remove phone number")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            TextButton(onClick = { phones = phones + "" },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text("Add phone")
            }
        }
    }
}

@Preview
@Composable
private fun ContactEdit() {
    ContactEditScreenContent(
        Contact("", "John Doe", listOf("0123456789"), null),
        {}, {}
    )
}