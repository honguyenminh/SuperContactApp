package com.ilikeincest.lab3.model

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: List<String>,
    val photoUri: String? = null
)
