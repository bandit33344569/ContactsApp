package com.bro.contactsapp.data.local

import android.content.ContentResolver
import android.provider.ContactsContract
import com.bro.contactsapp.data.model.ContactEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ContactLocalDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {
    fun getContacts(): Flow<List<ContactEntity>> = flow {
        val contacts = mutableListOf<ContactEntity>()

        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )

        cursor?.use { c ->
            while (c.moveToNext()) {
                val id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name =
                    c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val surname =
                    c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE))

                if (c.getInt(c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(id),
                        null
                    )

                    phoneCursor?.use { pc ->
                        while (pc.moveToNext()) {
                            val number =
                                pc.getString(pc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            contacts.add(ContactEntity(name, surname, number))
                            break
                        }
                    }
                }
            }
        }

        emit(contacts)
    }
}