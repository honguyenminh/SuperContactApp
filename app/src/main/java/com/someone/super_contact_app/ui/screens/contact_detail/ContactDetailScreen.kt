package com.someone.super_contact_app.ui.screens.contact_detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.someone.super_contact_app.data.deleteContacts
import com.someone.super_contact_app.data.getContact
import com.someone.super_contact_app.model.Contact
import com.someone.super_contact_app.model.PhoneNumber
import com.someone.super_contact_app.model.PhoneNumberType
import com.someone.super_contact_app.ui.components.AsyncAvatarFallbackMonogram
import com.someone.super_contact_app.ui.components.dialogs.ConfirmDeleteDialog
import com.someone.super_contact_app.ui.components.getMonogram
import com.someone.super_contact_app.ui.screens.contact_detail.component.ContactActionRow
import com.someone.super_contact_app.ui.screens.contact_detail.component.PhoneNumberCard

@Composable
fun ContactDetailScreen(
    contactId: String,
    onNavigateUp: () -> Unit,
    onEditContactClicked: () -> Unit
) {
    val context = LocalContext.current
    val contact = remember { getContact(contactId, context) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    ConfirmDeleteDialog(
        contactsCount = 1,
        isVisible = showDeleteDialog,
        onDismiss = { showDeleteDialog = false },
        onDelete = {
            showDeleteDialog = false
            deleteContacts(listOf(contact.id), context)
            onNavigateUp()
        }
    )

    ContactDetailScreenContent(contact, onNavigateUp, onEditContactClicked,
        onDeleteContactClicked = {
            showDeleteDialog = true
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactDetailScreenContent(
    contact: Contact,
    onNavigateUp: () -> Unit,
    onEditContactClicked: () -> Unit,
    onDeleteContactClicked: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = {},
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            actions = {
                IconButton(onClick = onEditContactClicked) {
                    Icon(Icons.Default.Edit, null)
                }
                IconButton(onClick = onDeleteContactClicked) {
                    Icon(Icons.Outlined.Delete, null, tint = colorScheme.error)
                }
            }
        ) },
        // TODO: add remove contact button
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(top = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncAvatarFallbackMonogram(
                model = contact.photoUri,
                contentDescription = "Contact photo",
                monogram = getMonogram(contact.name),
                size = 180.dp,
                textStyle = typography.displayLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 72.sp
                )
            )
            Text(
                contact.name, style = typography.headlineMedium,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            ContactActionRow(
                onCall = {},
                onText = {},
                onVideoCall = {},
                modifier = Modifier
                    .background(colorScheme.background)
                    .padding(horizontal = 32.dp)
            )
            PhoneNumberCard(
                phones = contact.phoneNumbers,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun ContactDetailPreview() {
    ContactDetailScreenContent(
        contact = Contact(
            name = "Nguyễn Văn An",
            phoneNumbers = listOf(
                PhoneNumber("0598765123", PhoneNumberType.Home),
                PhoneNumber("0918239283", PhoneNumberType.Work),
            ),
            id = "", photoUri = "",
        ), {}, {}, {}
    )
}