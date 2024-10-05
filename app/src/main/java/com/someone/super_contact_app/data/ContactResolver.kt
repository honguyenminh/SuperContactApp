package com.someone.super_contact_app.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.someone.super_contact_app.model.Contact

fun getContact(id: String, context: Context): Contact {
    val contentResolver = context.contentResolver
    val uri = ContactsContract.Contacts.CONTENT_URI
    val projection = arrayOf(
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.PHOTO_URI
    )
    val selection = "${ContactsContract.Contacts._ID} = ?"
    val selectionArgs = arrayOf(id)
    val cursor = contentResolver.query(
        uri, projection, selection, selectionArgs, null
    )
    if (cursor != null && cursor.moveToFirst()) {
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

        cursor.close()
        return Contact(id, name, phoneNumber, photoUri)
    }
    cursor?.close()
    return Contact("", "", emptyList(), "")
}

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
        contentResolver.delete(uri, selection, selectionArgs)
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
            if (phone.isBlank()) continue
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

fun updateContact(contactId: String, name: String, phones: List<String>, context: Context) {
    val contentResolver = context.contentResolver

    // Update contact name
    val nameValues = ContentValues().apply {
        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
    }
    val nameSelection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
    val nameSelectionArgs = arrayOf(contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
    contentResolver.update(ContactsContract.Data.CONTENT_URI, nameValues, nameSelection, nameSelectionArgs)

    // Delete old phone numbers
    val phoneSelection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
    val phoneSelectionArgs = arrayOf(contactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
    contentResolver.delete(ContactsContract.Data.CONTENT_URI, phoneSelection, phoneSelectionArgs)

    // Add new phone numbers
    // find first raw contact of contact
    val rawContacts = getRawContactId(contactId, context)
    if (rawContacts.isEmpty()) {
        Log.e("ContactResolver", "No raw contact found for contact id $contactId")
        return
    }
    val rawContactId = rawContacts.first()
    // insert phone numbers linked to that raw contact
    for (phone in phones) {
        if (phone.isBlank()) continue
        insertPhoneNumber(rawContactId, phone, context)
    }
}

private fun getRawContactId(contactId: String, context: Context): List<Long> {
    val contentResolver = context.contentResolver
    val rawContactIds = mutableListOf<Long>()

    val uri = ContactsContract.RawContacts.CONTENT_URI
    val projection = arrayOf(ContactsContract.RawContacts._ID)
    val selection = "${ContactsContract.RawContacts.CONTACT_ID} = ?"
    val selectionArgs = arrayOf(contactId)

    val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
    cursor?.use {
        while (it.moveToNext()) {
            val rawContactId = it.getLong(it.getColumnIndexOrThrow(ContactsContract.RawContacts._ID))
            rawContactIds.add(rawContactId)
        }
    }

    return rawContactIds
}

fun insertPhoneNumber(rawContactId: Long, phoneNumber: String, context: Context) {
    val contentResolver = context.contentResolver

    val phoneValues = ContentValues().apply {
        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
        // TODO: add ability to choose phone type
        put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
    }

    contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)
}