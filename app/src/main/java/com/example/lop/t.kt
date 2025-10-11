package com.example.lop

import java.io.File

fun main() {
    val base = "app/src/main/java/com/example/lop"

    val dirs = listOf(
        "$base/ui",
        "$base/network",
        "$base/data/models",
        "$base/data/db",
        "$base/util"
    )

    val files = mapOf(
        "$base/ui/MainActivity.kt" to mainActivityContent,
        "$base/ui/ProfileActivity.kt" to profileActivityContent,
        "$base/ui/ContactReceivedActivity.kt" to contactReceivedActivityContent,
        "$base/ui/SettingsActivity.kt" to settingsActivityContent,
        "$base/network/WifiDirectService.kt" to wifiDirectServiceContent,
        "$base/network/BluetoothService.kt" to bluetoothServiceContent,
        "$base/network/ConnectionManager.kt" to connectionManagerContent,
        "$base/data/models/LopProfile.kt" to lopProfileContent,
        "$base/data/db/LopDatabase.kt" to lopDatabaseContent,
        "$base/util/NFCUtil.kt" to nfcUtilContent,
        "$base/util/EncryptionUtil.kt" to encryptionUtilContent,
        "$base/util/QRUtil.kt" to qrUtilContent
    )

    dirs.forEach { File(it).mkdirs() }
    files.forEach { (path, content) -> File(path).writeText(content) }

    println("✅ Lop app structure created successfully!")
}

// --- Template contents ---
val mainActivityContent = """
package com.example.lop.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lop.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
""".trimIndent()

val profileActivityContent = """
package com.example.lop.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lop.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }
}
""".trimIndent()

val contactReceivedActivityContent = """
package com.example.lop.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lop.R

class ContactReceivedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_received)
    }
}
""".trimIndent()

val settingsActivityContent = """
package com.example.lop.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lop.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
}
""".trimIndent()

val wifiDirectServiceContent = """
package com.example.lop.network

import android.net.wifi.p2p.WifiP2pManager

class WifiDirectService {
    // TODO: Implement Wi-Fi Direct connection setup and data transfer
}
""".trimIndent()

val bluetoothServiceContent = """
package com.example.lop.network

import android.bluetooth.BluetoothAdapter

class BluetoothService {
    // TODO: Implement Bluetooth connection as fallback
}
""".trimIndent()

val connectionManagerContent = """
package com.example.lop.network

class ConnectionManager {
    // TODO: Manage connection between NFC handshake and Wi-Fi Direct session
}
""".trimIndent()

val lopProfileContent = """
package com.example.lop.data.models

data class LopProfile(
    val name: String,
    val phoneNumber: String,
    val email: String?,
    val instagram: String?,
    val snapchat: String?,
    val businessName: String?,
    val profileType: String // e.g. "Personal", "Business"
)
""".trimIndent()

val lopDatabaseContent = """
package com.example.lop.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lop.data.models.LopProfile

@Database(entities = [LopProfile::class], version = 1)
abstract class LopDatabase : RoomDatabase() {
    // TODO: Define DAOs
}
""".trimIndent()

val nfcUtilContent = """
package com.example.lop.util

object NFCUtil {
    // TODO: Implement NFC reading and writing helpers
}
""".trimIndent()

val encryptionUtilContent = """
package com.example.lop.util

object EncryptionUtil {
    // TODO: Add AES/TLS encryption methods for secure transfer
}
""".trimIndent()

val qrUtilContent = """
package com.example.lop.util

object QRUtil {
    // TODO: Add QR code generator and scanner for backup sharing
}
""".trimIndent()