package com.ilikeincest.lab3.ui.screens.create_contact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilikeincest.lab3.R
import com.ilikeincest.lab3.data.createContact
import com.ilikeincest.lab3.model.Contact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContactScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf("") }
    var showConfirmBackDialog by rememberSaveable { mutableStateOf(false) }
    // TODO: move logic to viewmodel
    var phones by rememberSaveable { mutableStateOf(listOf("")) }
    val context = LocalContext.current

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
                Button(onClick = {
                    createContact(name, phones, context)
                    onNavigateUp()
                }) {
                    Text("Save")
                }
                Spacer(Modifier.width(12.dp))
            }
        ) },
        modifier = modifier
    ) { innerPadding ->
        if (showConfirmBackDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmBackDialog = false },
                confirmButton = { TextButton(onClick = onNavigateUp) {
                    Text("Discard") }
                },
                dismissButton = { TextButton(onClick = { showConfirmBackDialog = false }) {
                    Text("Cancel") }
                },
                title = { Text("Discard changes?") },
                text = { Text("Any unsaved changes will be lost.") }
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(innerPadding)
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

@Preview
@Composable
private fun CreateContactScreenPreview() {
    CreateContactScreen(onNavigateUp = {})
}