package com.example.lop.util

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef

object NFCUtil {

    fun createHandshakePayload(profileId: String): NdefMessage {
        val record = NdefRecord.createTextRecord("en", profileId)
        return NdefMessage(arrayOf(record))
    }

    fun readHandshakePayload(tag: Tag): String? {
        val ndef = Ndef.get(tag)
        ndef?.connect()
        val message = ndef?.ndefMessage
        ndef?.close()
        return message?.records?.firstOrNull()?.toText()
    }

    private fun NdefRecord.toText(): String {
        val payload = this.payload
        val textEncoding = if ((payload[0].toInt() and 128) == 0) Charsets.UTF_8 else Charsets.UTF_16
        val languageCodeLength = payload[0].toInt() and 63
        return String(payload, languageCodeLength + 1, payload.size - languageCodeLength - 1, textEncoding)
    }
}