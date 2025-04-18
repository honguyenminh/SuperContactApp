package com.someone.super_contact_app.ui.screens.contact_list

import android.content.Context
import androidx.lifecycle.ViewModel
import com.someone.super_contact_app.data.deleteContacts
import com.someone.super_contact_app.data.getContacts
import com.someone.super_contact_app.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
// TODO: move viewmodel to app-wide activity
// TODO: reload contacts only on data change
class ContactListViewModel(initState: ContactListUiState = ContactListUiState()) : ViewModel() {
    private var _uiState = MutableStateFlow(initState)
    val uiState: StateFlow<ContactListUiState> =_uiState.asStateFlow()

    private lateinit var _contacts: List<Contact>

    fun changeSortOrder(isAscending: Boolean) {
        _uiState.update {
            it.copy(
                isSortedAscending = isAscending,
                contacts = _contacts.sortedWith { c1, c2 ->
                    if (isAscending) {
                        c1.name.compareTo(c2.name, ignoreCase = true)
                    } else {
                        c2.name.compareTo(c1.name, ignoreCase = true)
                    }
                }
            )
        }
    }

    fun refreshContacts(context: Context) {
        _contacts = getContacts(context).sortedWith { c1, c2 ->
            if (_uiState.value.isSortedAscending) {
                c1.name.compareTo(c2.name, ignoreCase = true)
            } else {
                c2.name.compareTo(c1.name, ignoreCase = true)
            }
        }
        _uiState.update {
            it.copy(contacts = _contacts)
        }
        clearSelected()
    }

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

    // WARN: DESTRUCTIVE ACTION
    fun deleteSelected(context: Context) {
        deleteContacts(_uiState.value.selectedItems.toList(), context)
        refreshContacts(context)
    }
}