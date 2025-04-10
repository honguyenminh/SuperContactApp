package com.someone.super_contact_app.model

data class Contact(
    val id: String,
    val name: String,
    val phoneNumbers: List<PhoneNumber>,
    val photoUri: String? = null
)