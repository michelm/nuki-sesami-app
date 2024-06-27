package com.example.nuki_sesami_app

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat.getString

class UserPreferences(context: Context) {
    private val handle: SharedPreferences = context.getSharedPreferences(
        getString(context, R.string.preferences_file_key), Context.MODE_PRIVATE)
    private val context: Context = context

    fun save(key: String, value: String) {
        val editor = handle.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun save(key: Int, value: String) {
        save(getString(context, key), value)
    }

    fun load(key: String, defaultValue: String): String {
        return handle.getString(key, defaultValue) ?: defaultValue
    }

    fun load(key: Int, defaultValue: String): String {
        return load(getString(context, key), defaultValue)
    }

    fun save(key: String, value: Boolean) {
        val editor = handle.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun save(key: Int, value: Boolean) {
        save(getString(context, key), value)
    }

    fun load(key: String, defaultValue: Boolean): Boolean {
        return handle.getBoolean(key, defaultValue) ?: defaultValue
    }

    fun load(key: Int, defaultValue: Boolean): Boolean {
        return load(getString(context, key), defaultValue)
    }
}
