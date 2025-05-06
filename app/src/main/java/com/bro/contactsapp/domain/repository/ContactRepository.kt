package com.bro.contactsapp.domain.repository

import com.bro.contactsapp.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getContacts(): Flow<List<Contact>>
}