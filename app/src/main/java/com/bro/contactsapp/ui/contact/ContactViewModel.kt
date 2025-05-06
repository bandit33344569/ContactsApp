package com.bro.contactsapp.ui.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bro.contactsapp.domain.usecase.GetContactsUseCase
import com.bro.contactsapp.domain.util.GroupedContact
import com.bro.contactsapp.domain.util.groupByFirstLetter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val getContactsUseCase: GetContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState

    fun loadContacts() {
        viewModelScope.launch {
            try {
                getContactsUseCase().map { contacts ->
                    contacts.groupByFirstLetter()
                }.collect { groupedContacts ->
                    _uiState.update { state ->
                        state.copy(
                            groupedContacts = groupedContacts,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Ошибка загрузки контактов", isLoading = false) }
            }
        }
    }

    fun updatePermissionDenied() {
        _uiState.update { it.copy(permissionDenied = true, isLoading = false) }
    }
}

data class ContactUiState(
    val groupedContacts: List<GroupedContact> = emptyList(),
    val isLoading: Boolean = true,
    val permissionDenied: Boolean = false,
    val error: String? = null
)