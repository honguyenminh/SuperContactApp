package com.someone.super_contact_app.data.util

import android.provider.ContactsContract.CommonDataKinds.Phone
import com.someone.super_contact_app.model.PhoneNumberType
import com.someone.super_contact_app.model.PhoneNumberType.Home
import com.someone.super_contact_app.model.PhoneNumberType.Mobile
import com.someone.super_contact_app.model.PhoneNumberType.Other
import com.someone.super_contact_app.model.PhoneNumberType.Unknown
import com.someone.super_contact_app.model.PhoneNumberType.Work

fun PhoneNumberType.toNativeEnum(): Int = when (this) {
    Mobile -> Phone.TYPE_MOBILE
    Home -> Phone.TYPE_HOME
    Work -> Phone.TYPE_WORK
    Other -> Phone.TYPE_OTHER
    Unknown -> Phone.TYPE_OTHER
}

fun nativeEnumToPhoneType(x: Int): PhoneNumberType = when (x) {
    Phone.TYPE_MOBILE -> Mobile
    Phone.TYPE_HOME -> Home
    Phone.TYPE_WORK -> Work
    Phone.TYPE_OTHER -> Other
    else -> Unknown
}