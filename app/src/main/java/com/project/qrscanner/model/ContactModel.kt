package com.project.qrscanner.model

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import com.project.qrscanner.builder.convertEmojisToUnicode
import com.project.qrscanner.builder.reconvertUnicodeToEmoji
import java.io.Serializable

class ContactModel(
    var name: String = "",
    var company: String = "",
    var phone: String = "",
    var email: String = "",
    var note: String = "",
    var address: String = "",
) : Serializable

fun fromVCard(vCard: String): ContactModel {
    var name = ""
    var company = ""
    var phone = ""
    var email = ""
    var note = ""
    var address = ""

    vCard.lines().forEach { line ->
        when {
            line.startsWith("FN:") -> name = line.substringAfter("FN:")
            line.startsWith("ORG:") -> company = line.substringAfter("ORG:")
            line.startsWith("TEL:") -> phone = line.substringAfter("TEL:")
            line.startsWith("EMAIL:") -> email = line.substringAfter("EMAIL:")
            line.startsWith("NOTE:") -> note = line.substringAfter("NOTE:")
            line.startsWith("ADR:") -> address = line.substringAfter("ADR:")
        }
    }

    return ContactModel(
        name = name.reconvertUnicodeToEmoji(),
        company = company.reconvertUnicodeToEmoji(),
        phone = phone,
        email = email.reconvertUnicodeToEmoji(),
        note = note.reconvertUnicodeToEmoji(),
        address = address.reconvertUnicodeToEmoji()
    )
}

fun ContactModel.toVCard(): String {
    return buildString {
        append("BEGIN:VCARD\n")
        append("VERSION:3.0\n")
        if (name.isNotEmpty()) append("FN:${name.convertEmojisToUnicode()}\n")
        if (company.isNotEmpty()) append("ORG:${company.convertEmojisToUnicode()}\n")
        if (phone.isNotEmpty()) append("TEL:${phone.convertEmojisToUnicode()}\n")
        if (email.isNotEmpty()) append("EMAIL:${email.convertEmojisToUnicode()}\n")
        if (note.isNotEmpty()) append("NOTE:${note.convertEmojisToUnicode()}\n")
        if (address.isNotEmpty()) append("ADR:${address.convertEmojisToUnicode()}\n")
        append("END:VCARD")
    }
}

fun insertContact(context: Context, contact: ContactModel) {
    val intent = Intent(Intent.ACTION_INSERT).apply {
        type = ContactsContract.RawContacts.CONTENT_TYPE
        putExtra(ContactsContract.Intents.Insert.NAME, contact.name)
        putExtra(ContactsContract.Intents.Insert.PHONE, contact.phone)
        putExtra(ContactsContract.Intents.Insert.EMAIL, contact.email)
        putExtra(ContactsContract.Intents.Insert.POSTAL, contact.address)
        putExtra(ContactsContract.Intents.Insert.COMPANY, contact.company)
        putExtra(ContactsContract.Intents.Insert.NOTES, contact.note)
    }
    context.startActivity(intent)
}