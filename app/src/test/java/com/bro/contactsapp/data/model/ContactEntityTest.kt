package com.bro.contactsapp.data.model

import junit.framework.TestCase.assertEquals
import org.junit.Test

class ContactEntityTest {

    @Test
    fun `toDomain maps ContactEntity to Contact correctly`() {
        val entity = ContactEntity(
            firstName = "Анна",
            lastName = "Петрова",
            phoneNumber = "+123"
        )

        val domain = entity.toDomain()

        assertEquals("Анна", domain.firstName)
        assertEquals("Петрова", domain.lastName)
        assertEquals("+123", domain.phoneNumber)
        assertEquals("Анна Петрова", domain.fullName)
    }
}