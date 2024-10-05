package com.someone.super_contact_app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.someone.super_contact_app.ui.screens.contact_detail.ContactDetailScreen
import com.someone.super_contact_app.ui.screens.contact_edit.ContactEditScreen
import com.someone.super_contact_app.ui.screens.contact_list.ContactListScreen
import com.someone.super_contact_app.ui.screens.create_contact.CreateContactScreen
import com.someone.super_contact_app.ui.theme.Lab3Theme

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
    val contactPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
        ),
        onPermissionsResult = {} // Add if needed
    )

    if (!contactPermissionState.allPermissionsGranted) {
        val textToShow = if (contactPermissionState.shouldShowRationale) {
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
                TextButton(onClick = { contactPermissionState.launchMultiplePermissionRequest() }) {
                    Text("Confirm")
                }
            },
        )
        return
    }
    AppRouter()
}

enum class AppRoutes {
    ContactList,
    ContactDetail,
    ContactEdit,
    CreateContact
}

@Composable
fun AppRouter(
    navController: NavHostController = rememberNavController()
) {
    var currentContactId by remember { mutableStateOf("") }
    NavHost(
        navController = navController,
        startDestination = AppRoutes.ContactList.name,
        enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut() }
    ) {
        composable(AppRoutes.ContactList.name) {
            ContactListScreen(
                onCreateContactClicked = {
                    navController.navigate(AppRoutes.CreateContact.name)
                },
                onContactClicked = { contactId ->
                    currentContactId = contactId
                    navController.navigate(AppRoutes.ContactDetail.name)
                }
            )
        }
        composable(AppRoutes.ContactDetail.name) {
            ContactDetailScreen(
                contactId = currentContactId,
                onNavigateUp = { navController.navigateUp() },
                onEditContactClicked = {
                    navController.navigate(AppRoutes.ContactEdit.name)
                }
            )
        }
        composable(AppRoutes.ContactEdit.name) {
            ContactEditScreen(
                contactId = currentContactId,
                onNavigateUp = { navController.navigateUp() },
                onNavigateHome = { navController.popBackStack(AppRoutes.ContactList.name, inclusive = false) }
            )
        }
        composable(AppRoutes.CreateContact.name) {
            CreateContactScreen(onNavigateUp = { navController.navigateUp() })
        }
    }
}