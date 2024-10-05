package com.someone.super_contact_app.ui.screens.contact_list

import com.someone.super_contact_app.model.Contact

data class ContactListUiState(
    val contacts: List<Contact> = listOf(),
    val selectedItems: Set<String> = setOf(),
    val isSortedAscending: Boolean = true
)
