package com.example.lop.util

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import java.nio.charset.Charset

object NFCUtil {

    // create a MIME-record vCard NDEF message
    fun createVCardMessage(vcard: String): NdefMessage {
        val payload = vcard.toByteArray(Charset.forName("UTF-8"))
        val record = NdefRecord.createMime("text/x-vcard", payload)
        return NdefMessage(arrayOf(record))
    }

    // create a simple text record
    fun createTextMessage(text: String): NdefMessage {
        // Helper to create a well-formed text record
        val payload = text.toByteArray(Charsets.UTF_8)
        val record = NdefRecord.createMime("text/plain", payload)
        return NdefMessage(arrayOf(record))
    }

    // write an NdefMessage to a tag. Returns true if successful.
    fun writeNdefMessageToTag(tag: Tag, message: NdefMessage): Boolean {
        return try {
            val ndef = Ndef.get(tag) ?: return false
            ndef.connect()
            if (!ndef.isWritable) {
                ndef.close()
                return false
            }
            ndef.writeNdefMessage(message)
            ndef.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // read first record payload as UTF-8 string (if possible)
    fun readTextFromNdef(message: NdefMessage?): String? {
        if (message == null) return null
        val records = message.records
        if (records.isEmpty()) return null
        return try {
            String(records[0].payload, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }
}
