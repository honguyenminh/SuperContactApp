package com.ilikeincest.lab3.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.ilikeincest.lab3.model.Contact

fun getContacts(context: Context): List<Contact> {
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
        null, null, null
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

fun deleteContacts(contactIds: List<String>, context: Context) {
    val contentResolver = context.contentResolver
    contactIds.forEach {
        val uri = ContactsContract.RawContacts.CONTENT_URI
        val selection = "${ContactsContract.Contacts._ID} = ?"
        val selectionArgs = arrayOf(it)
        val res = contentResolver.delete(uri, selection, selectionArgs)
        Log.d("FUCK YOU" , res.toString())
    }
}

fun createContact(name: String, phones: List<String>, context: Context) {
    val contentResolver = context.contentResolver
    // Create a new raw contact
    val rawContactUri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, ContentValues())
    if (rawContactUri != null) {
        val rawContactId = ContentUris.parseId(rawContactUri)

        // Add contact name
        val nameValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
        }
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, nameValues)

        // Add phone numbers
        for (phone in phones) {
            val phoneValues = ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
            }
            contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)
        }
    }
}