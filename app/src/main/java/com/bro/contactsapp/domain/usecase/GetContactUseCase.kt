package com.bro.contactsapp.domain.usecase

import com.bro.contactsapp.domain.model.Contact
import com.bro.contactsapp.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    operator fun invoke(): Flow<List<Contact>> = repository.getContacts()
}