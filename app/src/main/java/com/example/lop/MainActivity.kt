package com.example.lop.ui

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Ndef
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var nameField: EditText
    private lateinit var phoneField: EditText
    private lateinit var shareButton: Button
    private lateinit var settingsButton: Button

    private var messageToWrite: NdefMessage? = null
    private val PREFS = "lop_prefs"
    private val KEY_NAME = "name"
    private val KEY_PHONE = "phone"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameField = findViewById(R.id.nameField)
        phoneField = findViewById(R.id.phoneField)
        shareButton = findViewById(R.id.shareButton)
        settingsButton = findViewById(R.id.btnSettings)

        // Load saved values
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        nameField.setText(prefs.getString(KEY_NAME, ""))
        phoneField.setText(prefs.getString(KEY_PHONE, ""))

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        shareButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val phone = phoneField.text.toString().trim()
            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill name and phone", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val vcard = """
                BEGIN:VCARD
                VERSION:3.0
                FN:$name
                TEL:$phone
                END:VCARD
            """.trimIndent()
            messageToWrite = NdefMessage(arrayOf(
                NdefRecord.createMime("text/x-vcard", vcard.toByteArray(Charsets.UTF_8))
            ))

            AlertDialog.Builder(this)
                .setTitle("Share Contact")
                .setMessage("Tap another NFC device or tag to share.")
                .setPositiveButton("OK", null)
                .show()
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC not available on this device", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh fields in case settings changed
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        nameField.setText(prefs.getString(KEY_NAME, ""))
        phoneField.setText(prefs.getString(KEY_PHONE, ""))

        nfcAdapter?.let { adapter ->
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            adapter.enableForegroundDispatch(this, pending, null, null)
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // If we prepared a message and a tag was presented -> write it
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        if (tag != null && messageToWrite != null) {
            val ok = writeNdefMessageToTag(tag, messageToWrite!!)
            Toast.makeText(this, if (ok) "Contact written to tag/device" else "Failed to write NFC", Toast.LENGTH_SHORT).show()
            messageToWrite = null
            return
        }

        // If we received NDEF messages (app opened by an NFC NDEF action)
        val raw = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        if (raw != null && raw.isNotEmpty()) {
            val msgs = raw.map { it as NdefMessage }
            val record = msgs[0].records.firstOrNull()
            val payload = record?.payload
            payload?.let {
                // Many vCards are stored as MIME; decode directly
                val text = String(it, Charsets.UTF_8)
                if (text.startsWith("BEGIN:VCARD")) {
                    AlertDialog.Builder(this)
                        .setTitle("Contact received")
                        .setMessage("Import received vCard into Contacts?")
                        .setPositiveButton("Import") { _, _ -> saveVCardImport(text) }
                        .setNegativeButton("Cancel", null)
                        .show()
                } else {
                    Toast.makeText(this, "NFC data: $text", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Writes NDEF to a tag/device using standard Ndef API
    private fun writeNdefMessageToTag(tag: Tag, message: NdefMessage): Boolean {
        try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                if (!ndef.isWritable) {
                    ndef.close()
                    return false
                }
                val size = message.toByteArray().size
                if (ndef.maxSize < size) {
                    ndef.close()
                    return false
                }
                ndef.writeNdefMessage(message)
                ndef.close()
                return true
            } else {
                // Try formatable
                val format = android.nfc.NdefFormatable.get(tag)
                if (format != null) {
                    format.connect()
                    format.format(message)
                    format.close()
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    // Start system vCard import by writing vcf file to cache and launching ACTION_VIEW
    private fun saveVCardImport(vcard: String) {
        try {
            val file = java.io.File(cacheDir, "import.vcf")
            file.writeText(vcard)
            val uri = androidx.core.content.FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/x-vcard")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to import contact: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
