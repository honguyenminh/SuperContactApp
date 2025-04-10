package com.someone.super_contact_app.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.util.Log
import com.someone.super_contact_app.data.util.nativeEnumToPhoneType
import com.someone.super_contact_app.data.util.toNativeEnum
import com.someone.super_contact_app.model.Contact
import com.someone.super_contact_app.model.PhoneNumber
import com.someone.super_contact_app.model.PhoneNumberType
import com.someone.super_contact_app.model.PhoneNumberType.*

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
            Phone.CONTENT_URI,
            arrayOf(Phone.NUMBER, Phone.TYPE),
            Phone.CONTACT_ID + " = ?",
            arrayOf(id),
            null
        )

        val phoneNumber = mutableListOf<PhoneNumber>()
        if (phoneCursor != null && phoneCursor.moveToFirst()) {
            do {
                val number = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(
                    Phone.NUMBER
                ))
                val type = phoneCursor.getInt(phoneCursor.getColumnIndexOrThrow(
                    Phone.TYPE
                ))
                phoneNumber.add(PhoneNumber(number, nativeEnumToPhoneType(type)))
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
                Phone.CONTENT_URI,
                arrayOf(Phone.NUMBER, Phone.TYPE),
                Phone.CONTACT_ID + " = ?",
                arrayOf(id),
                null
            )

            val phoneNumber = mutableListOf<PhoneNumber>()
            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                do {
                    val number = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(
                        Phone.NUMBER
                    ))
                    val type = phoneCursor.getInt(phoneCursor.getColumnIndexOrThrow(
                        Phone.TYPE
                    ))
                    phoneNumber.add(PhoneNumber(number, nativeEnumToPhoneType(type)))
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

fun createContact(name: String, phones: List<PhoneNumber>, context: Context) {
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
            if (phone.number.isBlank()) continue
            val phoneValues = ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                put(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                put(Phone.NUMBER, phone.number)
                put(Phone.TYPE, phone.type.toNativeEnum())
            }
            contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)
        }
    }
}

fun updateContact(contactId: String, name: String, phones: List<PhoneNumber>, context: Context) {
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
    val phoneSelectionArgs = arrayOf(contactId, Phone.CONTENT_ITEM_TYPE)
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
        if (phone.number.isBlank()) continue
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

fun insertPhoneNumber(rawContactId: Long, phoneNumber: PhoneNumber, context: Context) {
    val contentResolver = context.contentResolver

    val phoneValues = ContentValues().apply {
        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        put(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
        put(Phone.NUMBER, phoneNumber.number)
        // TODO: add ability to choose phone type
        put(Phone.TYPE, phoneNumber.type.toNativeEnum())
    }

    contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)
}

