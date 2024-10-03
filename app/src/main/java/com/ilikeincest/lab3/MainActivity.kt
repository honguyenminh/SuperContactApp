package com.ilikeincest.lab3

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.ilikeincest.lab3.data.getContacts
import com.ilikeincest.lab3.model.Contact
import com.ilikeincest.lab3.ui.screens.contact_list.ContactListScreen
import com.ilikeincest.lab3.ui.theme.Lab3Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab3Theme {
                ContactApp()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactApp() {
    val contactPermissionState = rememberPermissionState(
        permission = Manifest.permission.READ_CONTACTS,
        onPermissionResult = { isGranted ->
            if (isGranted) return@rememberPermissionState
            // TODO
        }
    )

    if (!contactPermissionState.status.isGranted) {
        val textToShow = if (contactPermissionState.status.shouldShowRationale) {
            // If the user has denied the permission but the rationale can be shown,
            // then gently explain why the app requires this permission
            "Contact permission is required for the app to work.\n" +
                    " Since you previously denied the permission, if there's no permission pop-up, please enable manually in app info"
        } else {
            // If it's the first time the user lands on this feature, or the user
            // doesn't want to be asked again for this permission, explain that the
            // permission is required
            "Contact permission is required for the app to work."
        }
        AlertDialog(
            icon = { Icon(Icons.Default.Info, null) },
            title = { Text("Permission needed") },
            text = { Text(textToShow) },
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { contactPermissionState.launchPermissionRequest() }) {
                    Text("Confirm")
                }
            },
        )
        return
    }
    var contacts by remember { mutableStateOf(listOf<Contact>()) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        contacts = getContacts(context)
    }
    ContactListScreen(contacts)
}