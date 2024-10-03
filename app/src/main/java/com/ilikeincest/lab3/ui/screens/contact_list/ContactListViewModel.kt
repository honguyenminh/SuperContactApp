package com.ilikeincest.lab3.ui.screens.contact_list

import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import com.ilikeincest.lab3.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ContactListViewModel : ViewModel() {
    private var _uiState = MutableStateFlow(ContactListUiState())
    val uiState: StateFlow<ContactListUiState> =_uiState.asStateFlow()

    fun clearSelected() {
        _uiState.update {
            it.copy(selectedItems = setOf())
        }
    }

    fun selectContact(id: String) {
        val newSelectedItems = if (_uiState.value.selectedItems.contains(id)) {
            _uiState.value.selectedItems - id
        } else {
            _uiState.value.selectedItems + id
        }
        _uiState.update {
            it.copy(selectedItems = newSelectedItems)
        }
    }

    fun selectAll(contacts: List<Contact>) {
        _uiState.update {
            it.copy(selectedItems = contacts.map { it.id }.toSet())
        }
    }
}