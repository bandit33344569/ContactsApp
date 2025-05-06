package com.bro.contactsapp.domain.util

import com.bro.contactsapp.domain.model.Contact
import org.junit.Assert.assertEquals
import org.junit.Test

class ContactGroupingTest {

    @Test
    fun `groupByFirstLetter groups contacts by first letter and sorts correctly`() {
        val contacts = listOf(
            Contact("Борис", "Иванов", "+789"),
            Contact("Анна", "Петрова", "+123"),
            Contact("Алексей", "Смирнов", "+456"),
            Contact("Яна", "Кузнецова", "+999")
        )

        val groupedContacts = contacts.groupByFirstLetter()

        assertEquals(3, groupedContacts.size)

        assertEquals(2, groupedContacts[0].contacts.size)
        assertEquals(1, groupedContacts[1].contacts.size)
        assertEquals(1, groupedContacts[2].contacts.size)

        assertEquals('А', groupedContacts[0].letter)
        assertEquals('Б', groupedContacts[1].letter)
        assertEquals('Я', groupedContacts[2].letter)

        assertEquals("Алексей Смирнов", groupedContacts[0].contacts[0].fullName)
        assertEquals("Анна Петрова", groupedContacts[0].contacts[1].fullName)
        assertEquals("Борис Иванов", groupedContacts[1].contacts[0].fullName)
        assertEquals("Яна Кузнецова", groupedContacts[2].contacts[0].fullName)
    }

    @Test
    fun `groupByFirstLetter handles empty list`() {
        val contacts = emptyList<Contact>()

        val groupedContacts = contacts.groupByFirstLetter()

        assertEquals(0, groupedContacts.size)
    }

    @Test
    fun `groupByFirstLetter handles case insensitive sorting`() {
        val contacts = listOf(
            Contact("анна", "Петрова", "+123"),
            Contact("Алексей", "Смирнов", "+456")
        )

        val groupedContacts = contacts.groupByFirstLetter()

        assertEquals(1, groupedContacts.size)
        assertEquals(2, groupedContacts[0].contacts.size)

        assertEquals('А', groupedContacts[0].letter)

        assertEquals("анна Петрова", groupedContacts[0].contacts[1].fullName)
        assertEquals("Алексей Смирнов", groupedContacts[0].contacts[0].fullName)
    }
}