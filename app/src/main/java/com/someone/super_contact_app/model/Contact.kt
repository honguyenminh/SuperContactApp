package com.someone.super_contact_app.model

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: List<String>,
    val photoUri: String? = null
)
