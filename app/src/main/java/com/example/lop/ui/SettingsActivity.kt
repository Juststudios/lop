package com.example.lop

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private val PREFS = "lop_prefs"
    private val KEY_NAME = "name"
    private val KEY_PHONE = "phone"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val saveButton = findViewById<Button>(R.id.btnSave)
        val backButton = findViewById<Button>(R.id.btnBack)

        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        nameInput.setText(prefs.getString(KEY_NAME, ""))
        phoneInput.setText(prefs.getString(KEY_PHONE, ""))

        saveButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            prefs.edit().putString(KEY_NAME, name).putString(KEY_PHONE, phone).apply()
            finish() // go back to MainActivity; MainActivity will reload prefs in onResume()
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}
