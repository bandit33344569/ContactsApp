package com.bro.contactsapp.data.repository

import com.bro.contactsapp.data.local.ContactLocalDataSource
import com.bro.contactsapp.data.model.toDomain
import com.bro.contactsapp.domain.model.Contact
import com.bro.contactsapp.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val localDataSource: ContactLocalDataSource
) : ContactRepository {
    override fun getContacts(): Flow<List<Contact>> =
        localDataSource.getContacts().map { entities -> entities.map { it.toDomain() } }
}