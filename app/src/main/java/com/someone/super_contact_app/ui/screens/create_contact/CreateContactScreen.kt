package com.someone.super_contact_app.ui.screens.create_contact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.someone.super_contact_app.R
import com.someone.super_contact_app.data.createContact
import com.someone.super_contact_app.ui.components.dialogs.ConfirmBackDialog
import com.someone.super_contact_app.ui.theme.Lab3Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContactScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf("") }
    var showConfirmBackDialog by rememberSaveable { mutableStateOf(false) }
    var phones by rememberSaveable { mutableStateOf(listOf("")) }
    val context = LocalContext.current

    ConfirmBackDialog(
        isVisible = showConfirmBackDialog,
        onDismiss = { showConfirmBackDialog = false },
        onNavigateUp = onNavigateUp
    )

    Scaffold(
        topBar = { TopAppBar(
            title = { Text("Create contact") },
            navigationIcon = {
                IconButton(
                    onClick = {
                        if (name.isBlank() && phones.all { it.isBlank() })
                            onNavigateUp()
                        else showConfirmBackDialog = true
                    }
                ) {
                    Icon(Icons.Default.Close, "Close")
                }
            },
            actions = {
                Button(
                    onClick = {
                        createContact(name, phones, context)
                        onNavigateUp()
                    },
                    enabled = name.isNotBlank() && phones.any { it.isNotBlank() }
                ) {
                    Text("Save")
                }
                Spacer(Modifier.width(12.dp))
            }
        ) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
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

@PreviewLightDark
@Composable
private fun CreateContactScreenPreview() {
    Lab3Theme {
        CreateContactScreen(onNavigateUp = {})
    }
}