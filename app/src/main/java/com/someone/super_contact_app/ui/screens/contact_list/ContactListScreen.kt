package com.someone.super_contact_app.ui.screens.contact_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.someone.super_contact_app.model.Contact
import com.someone.super_contact_app.ui.components.AsyncAvatarFallbackMonogram
import com.someone.super_contact_app.ui.components.dialogs.ConfirmDeleteDialog
import com.someone.super_contact_app.ui.components.getMonogram
import com.someone.super_contact_app.ui.theme.Lab3Theme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen(
    onCreateContactClicked: () -> Unit,
    onContactClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactListViewModel = viewModel(),
    refreshOnCompose: Boolean = true
) {
    val uiState by viewModel.uiState.collectAsState()
    var moreMenuExpanded by remember { mutableStateOf(false) }
    var showSelectMultipleGuideDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (refreshOnCompose) {
        LaunchedEffect(Unit) {
            viewModel.refreshContacts(context)
        }
    }

    val topBarText =
        if (uiState.selectedItems.isEmpty())
            "Contacts"
        else
            "${uiState.selectedItems.size} selected"
    val appBarContainerColor =
        if (uiState.selectedItems.isEmpty())
            colorScheme.primaryContainer
        else
            colorScheme.tertiaryContainer
    val appBarContentColor =
        if (uiState.selectedItems.isEmpty())
            colorScheme.onPrimaryContainer
        else
            colorScheme.onTertiaryContainer

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarText) },
                colors = topAppBarColors(
                    containerColor = appBarContainerColor,
                    titleContentColor = appBarContentColor
                ),
                navigationIcon = {
                    if (uiState.selectedItems.isEmpty()) return@TopAppBar
                    IconButton(
                        onClick = { viewModel.clearSelected() },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = appBarContentColor)
                    ) {
                        Icon(Icons.Default.Close, "Clear selection")
                    }
                },
                actions = {
                    if (uiState.selectedItems.isNotEmpty()) {
                        IconButton(
                            onClick = { showConfirmDeleteDialog = true },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = appBarContentColor)
                        ) {
                            Icon(Icons.Default.Delete, "Delete selected contacts")
                        }
                    }
                    IconButton(
                        onClick = { moreMenuExpanded = true },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = appBarContentColor)
                    ) {
                        Icon(Icons.Default.MoreVert, "More options")
                    }
                    DropdownMenu(expanded = moreMenuExpanded, onDismissRequest = { moreMenuExpanded = false }) {
                        if (uiState.isSortedAscending) {
                            DropdownMenuItem(
                                text = { Text("Sort descending") },
                                onClick = {
                                    viewModel.changeSortOrder(false)
                                    moreMenuExpanded = false
                                }
                            )
                        }
                        else {
                            DropdownMenuItem(
                                text = { Text("Sort ascending") },
                                onClick = {
                                    viewModel.changeSortOrder(true)
                                    moreMenuExpanded = false
                                }
                            )
                        }

                        DropdownMenuItem(text = { Text("Select all") }, onClick = {
                            viewModel.selectAll(uiState.contacts)
                            moreMenuExpanded = false
                        })
                        DropdownMenuItem(text = { Text("Select many") }, onClick = {
                            showSelectMultipleGuideDialog = true
                            moreMenuExpanded = false
                        })
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateContactClicked,
                text = { Text("Create contact") },
                icon = { Icon(Icons.Default.Add, null) }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (showSelectMultipleGuideDialog) {
            AlertDialog(
                onDismissRequest = { showSelectMultipleGuideDialog = false },
                confirmButton = {
                    TextButton(onClick = { showSelectMultipleGuideDialog = false }) {
                        Text("OK")
                    }
                },
                title = { Text("Select multiple guide") },
                text = { Text(
                    "To select multiple contacts, press and hold the contact.\n" +
                        "The way it should be. Not through a menu like this."
                ) }
            )
        }

        ConfirmDeleteDialog(
            contactsCount = uiState.selectedItems.size,
            isVisible = showConfirmDeleteDialog,
            onDismiss = { showConfirmDeleteDialog = false },
            onDelete = {
                showConfirmDeleteDialog = false
                viewModel.deleteSelected(context)
            }
        )

        LazyColumn(modifier = Modifier
            .padding(innerPadding)
            .padding(top = 12.dp)
        ) {
            items(uiState.contacts, key = { it.id }) {
                ContactItem(
                    contact = it,
                    isSelected = uiState.selectedItems.contains(it.id),
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            if (uiState.selectedItems.isEmpty())
                                onContactClicked(it.id)
                            else
                                viewModel.selectContact(it.id)
                        },
                        onLongClick = { viewModel.selectContact(it.id) }
                    )
                )
            }
        }
    }
}

@Composable
fun ContactItem(contact: Contact, isSelected: Boolean, modifier: Modifier = Modifier) {
    val itemColors = if (!isSelected) ListItemDefaults.colors()
    else ListItemDefaults.colors(containerColor = colorScheme.surfaceContainerLow)

    ListItem(
        headlineContent = { Text(contact.name, style = typography.bodyLarge) },
        leadingContent = {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(colorScheme.tertiaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check, contentDescription = "Selected",
                        tint = colorScheme.onTertiaryContainer,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }
                return@ListItem
            }
            AsyncAvatarFallbackMonogram(
                model = contact.photoUri,
                contentDescription = "contact avatar",
                monogram = getMonogram(contact.name),
            )
        },
        colors = itemColors,
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
private fun ContactItemPreview() {
    Lab3Theme {
        ContactItem(Contact("1", "John Doe", listOf("1234567890")), false)
    }
}
@PreviewLightDark
@Composable
private fun ContactItemSelectedPreview() {
    Lab3Theme {
        ContactItem(Contact("1", "John Doe", listOf("1234567890")), true)
    }
}
@PreviewLightDark
@Composable
private fun ContactList() {
    Lab3Theme {
        ContactListScreen(
            onCreateContactClicked = {},
            onContactClicked = {},
            viewModel = ContactListViewModel(ContactListUiState(
                listOf(
                    Contact("1", "John Doe", listOf()),
                    Contact("2", "John Doe Niga", listOf()),
                    Contact("3", "John", listOf()),
                    Contact("4", "John Doe", listOf()),
                    Contact("5", "John Doe", listOf()),
                    Contact("6", "John Doe", listOf()),
                )
            )),
            refreshOnCompose = false
        )
    }
}
@PreviewLightDark
@Composable
private fun ContactListSelected() {
    Lab3Theme {
        ContactListScreen(
            onCreateContactClicked = {},
            onContactClicked = {},
            viewModel = ContactListViewModel(ContactListUiState(
                listOf(
                    Contact("1", "John Doe", listOf()),
                    Contact("2", "John Doe Niga", listOf()),
                    Contact("3", "John", listOf()),
                    Contact("4", "John Doe", listOf()),
                    Contact("5", "John Doe", listOf()),
                    Contact("6", "John Doe", listOf()),
                ),
                selectedItems = setOf("1", "3", "5")
            )),
            refreshOnCompose = false
        )
    }
}