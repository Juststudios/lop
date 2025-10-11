package com.example.lop.ui

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.NdefMessage
import android.nfc.Tag
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lop.R
import com.example.lop.data.models.LopProfile
import com.example.lop.network.ConnectionManager
import com.example.lop.util.NFCUtil

class MainActivity : AppCompatActivity() {

    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var nameField: EditText
    private lateinit var phoneField: EditText
    private lateinit var connectionManager: ConnectionManager

    // Preloaded profile
    private val myProfile = LopProfile(
        name = "John Doe",
        phoneNumber = "+1234567890",
        email = "john@example.com",
        instagram = "@johninsta",
        snapchat = "johnsnap",
        businessName = "Doe Ventures",
        profileType = "Business"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameField = findViewById(R.id.nameField)
        phoneField = findViewById(R.id.phoneField)

        // Pre-fill fields
        nameField.setText(myProfile.name)
        phoneField.setText(myProfile.phoneNumber)

// Initialize connection manager
        connectionManager = ConnectionManager(this)
        connectionManager.init()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC not supported", Toast.LENGTH_LONG).show()
            finish()
        }


    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = android.app.PendingIntent.getActivity(
            this, 0, intent, android.app.PendingIntent.FLAG_MUTABLE
        )
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            showShareConfirmation(tag)
        }

        val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        if (rawMessages != null) {
            val messages = rawMessages.map { it as NdefMessage }
            val receivedText = String(messages[0].records[0].payload)
            val profileParts = receivedText.split("\n").filter { it.contains(":") }
            val receivedProfile = LopProfile(
                name = profileParts.find { it.startsWith("FN:") }?.substringAfter("FN:") ?: "",
                phoneNumber = profileParts.find { it.startsWith("TEL:") }?.substringAfter("TEL:") ?: "",
                email = null,
                instagram = null,
                snapchat = null,
                businessName = null,
                profileType = "Personal"
            )

            // Start Wi-Fi Direct session to exchange full contact info
            connectionManager.discoverPeers()
            // Later, when WifiP2pInfo is available, call:
            // connectionManager.startDataTransfer(wifiP2pInfo, receivedProfile.toJson()) { json -> ... }
        }
    }

    private fun showShareConfirmation(tag: Tag) {
        AlertDialog.Builder(this)
            .setTitle("Share your profile?")
            .setMessage("Do you want to share your contact/business card with this device?")
            .setPositiveButton("Yes") { _, _ ->
                shareProfile(tag)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun shareProfile(tag: Tag) {
        // 1️⃣ Create NFC handshake message with profile ID (or phone)
        val handshakeMessage = NFCUtil.createHandshakePayload(myProfile.phoneNumber)

        // Write handshake to tag (simulated here)
        val ndef = android.nfc.tech.Ndef.get(tag)
        ndef?.connect()
        ndef?.writeNdefMessage(handshakeMessage)
        ndef?.close()

        Toast.makeText(this, "Handshake sent! Starting connection...", Toast.LENGTH_SHORT).show()

        // 2️⃣ Initiate Wi-Fi Direct transfer
        val profileData = serializeProfile(myProfile)
        connectionManager.sendProfileData(profileData)
    }

    private fun serializeProfile(profile: LopProfile): ByteArray {
        // Simple CSV / JSON encoding
        return """
            ${profile.name},${profile.phoneNumber},${profile.email ?: ""},${profile.instagram ?: ""},${profile.snapchat ?: ""},${profile.businessName ?: ""},${profile.profileType}
        """.trimIndent().toByteArray(Charsets.UTF_8)
    }

    // Optional: receive data and save to Contacts
    fun saveContact(name: String, phone: String, email: String? = null) {
        val values = ContentValues().apply {
            put(ContactsContract.RawContacts.ACCOUNT_TYPE, null as String?)
            put(ContactsContract.RawContacts.ACCOUNT_NAME, null as String?)
        }
        val rawContactUri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, values)
        val rawContactId = rawContactUri?.lastPathSegment?.toLong() ?: return

        // Name
        val nameValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
        }
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, nameValues)

        // Phone
        val phoneValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
            put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        }
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)

        // Email
        email?.let {
            val emailValues = ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                put(ContactsContract.CommonDataKinds.Email.ADDRESS, it)
                put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
            }
            contentResolver.insert(ContactsContract.Data.CONTENT_URI, emailValues)
        }

        Toast.makeText(this, "Contact saved!", Toast.LENGTH_SHORT).show()
    }
}