package com.example.nuki_sesami_app

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat.getString

class UserPreferences(context: Context) {
    private val _handle: SharedPreferences = context.getSharedPreferences(
        getString(context, R.string.preferences_file_key), Context.MODE_PRIVATE)

    fun save(key: String, value: String) {
        val editor = _handle.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun load(key: String, defaultValue: String): String {
        return _handle.getString(key, defaultValue) ?: defaultValue
    }
}
