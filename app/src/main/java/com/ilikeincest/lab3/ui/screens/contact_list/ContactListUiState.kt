package com.ilikeincest.lab3.ui.screens.contact_list

import com.ilikeincest.lab3.model.Contact

data class ContactListUiState(
    val contacts: List<Contact> = listOf(),
    val selectedItems: Set<String> = setOf(),
    val isSortedAscending: Boolean = true
)
