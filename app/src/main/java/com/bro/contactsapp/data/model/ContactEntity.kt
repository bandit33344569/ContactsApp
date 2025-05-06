package com.bro.contactsapp.data.model

import com.bro.contactsapp.domain.model.Contact

data class ContactEntity(
    val name: String,
    val surname: String,
    val phoneNumber: String
)

fun ContactEntity.toDomain() = Contact(name, surname,phoneNumber)
