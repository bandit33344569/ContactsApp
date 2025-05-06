package com.bro.contactsapp.domain.model

data class Contact(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
){
    val fullName: String
        get() = "$firstName $lastName".trim()
}
