package com.ilikeincest.lab3.data

import android.content.Context
import android.provider.ContactsContract
import com.ilikeincest.lab3.model.Contact

fun getContacts(context: Context, isAscending: Boolean = true): List<Contact> {
    val contactsList = mutableListOf<Contact>()

    val contentResolver = context.contentResolver
    val uri = ContactsContract.Contacts.CONTENT_URI
    val projection = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.PHOTO_URI
    )
    val cursor = contentResolver.query(
        uri, projection,
        null, null,
        // sort by display name
        "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ${if (isAscending) "ASC" else "DESC"}"
    )
    if (cursor != null && cursor.moveToFirst()) {
        do {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
            val photoUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))

            val phoneCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                arrayOf(id),
                null
            )

            val phoneNumber = mutableListOf<String>()
            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                do {
                    phoneNumber.add(
                        phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        ))
                    )
                } while (phoneCursor.moveToNext())
                phoneCursor.close()
            }

            contactsList.add(Contact(id, name, phoneNumber, photoUri))
        } while (cursor.moveToNext())
        cursor.close()
    }
    return contactsList
}