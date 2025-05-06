package com.bro.contactsapp.data.model

import com.bro.contactsapp.domain.model.Contact

data class ContactEntity(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)

fun ContactEntity.toDomain() = Contact(firstName, lastName, phoneNumber)
