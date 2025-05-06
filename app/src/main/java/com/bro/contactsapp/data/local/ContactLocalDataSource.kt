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
            null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        cursor?.use { c ->
            while (c.moveToNext()) {
                val id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val displayName =
                    c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                var firstName = displayName
                var lastName = ""
                val nameCursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE),
                    null
                )
                nameCursor?.use { nc ->
                    if (nc.moveToFirst()) {
                        firstName = nc.getString(
                            nc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)
                        ) ?: displayName
                        lastName = nc.getString(
                            nc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)
                        ) ?: ""
                    }
                }

                var mobileNumber: String? = null
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
                            val number = pc.getString(
                                pc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                            val type = pc.getInt(
                                pc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE)
                            )
                            if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                                mobileNumber = number
                                break
                            } else if (mobileNumber == null) {
                                mobileNumber = number
                            }
                        }
                    }
                }

                if (mobileNumber != null) {
                    contacts.add(ContactEntity(firstName, lastName, mobileNumber!!))
                }
            }
        }

        emit(contacts)
    }
}