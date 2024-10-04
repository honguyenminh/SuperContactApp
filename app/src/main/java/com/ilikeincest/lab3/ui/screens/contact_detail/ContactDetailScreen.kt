package com.ilikeincest.lab3.ui.screens.contact_detail

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilikeincest.lab3.data.getContact
import com.ilikeincest.lab3.model.Contact
import com.ilikeincest.lab3.ui.components.AsyncAvatarFallbackMonogram
import com.ilikeincest.lab3.ui.components.getMonogram

@Composable
fun ContactDetailScreen(
    contactId: String,
    onEditContactClicked: () -> Unit
) {
    val context = LocalContext.current
    val contact = remember { getContact(contactId, context) }
    ContactDetailScreenContent(contact, onEditContactClicked)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactDetailScreenContent(
    contact: Contact,
    onEditContactClicked: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    Scaffold(
        topBar = { TopAppBar(title = {},
            navigationIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
        ) },
        floatingActionButton = { ExtendedFloatingActionButton(
            text = { Text("Edit") },
            icon = { Icon(Icons.Default.Edit, null) },
            onClick = onEditContactClicked
        ) }
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
                contact.name, style = typography.headlineLarge,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
            ) {
                Column {
                    Text(
                        text = "Phone numbers", style = typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    contact.phoneNumber.forEach {
                        Box(Modifier.clickable {
                            clipboard.setText(AnnotatedString(it))
                            Toast.makeText(context, "Copied phone number", Toast.LENGTH_SHORT).show()
                        }) {
                            Text(it,
                                style = typography.bodyLarge,
                                modifier = Modifier
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
            Text("Tap number to copy")
        }
    }
}

@Preview
@Composable
private fun ContactDetailPreview() {
    ContactDetailScreenContent(
        contact = Contact(
            name = "Nguyễn Văn An",
            phoneNumber = listOf("0598765123", "0978666543"),
            id = "", photoUri = "",
        ), onEditContactClicked = {}
    )
}