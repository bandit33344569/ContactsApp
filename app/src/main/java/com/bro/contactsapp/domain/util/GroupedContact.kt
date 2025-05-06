package com.bro.contactsapp.domain.util

import com.bro.contactsapp.domain.model.Contact

data class GroupedContact(
    val letter: Char,
    val contacts: List<Contact>
)

fun List<Contact>.groupByFirstLetter(): List<GroupedContact> {
    return this
        .sortedBy { it.firstName.lowercase() }
        .groupBy { it.firstName.first().uppercaseChar() }
        .map { (letter, contacts) -> GroupedContact(letter, contacts) }
        .sortedBy { it.letter }
}