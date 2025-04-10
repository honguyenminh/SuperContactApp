package com.someone.super_contact_app

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.someone.super_contact_app.ui.screens.contact_detail.ContactDetailScreen
import com.someone.super_contact_app.ui.screens.contact_edit.ContactEditScreen
import com.someone.super_contact_app.ui.screens.contact_list.ContactListScreen
import com.someone.super_contact_app.ui.screens.create_contact.CreateContactScreen

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