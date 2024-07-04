package com.example.nuki_sesami_app.base

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat.getString
import com.example.nuki_sesami_app.R

class UserPreferences(
    private val context: Context? = null
) {
    private val handle: SharedPreferences? = context?.getSharedPreferences(
        getString(context, R.string.preferences_file_key), Context.MODE_PRIVATE)

    private val observers = ArrayList<(String, Any) -> Unit>()

    fun subscribe(observer: (String, Any) -> Unit) {
        observers.add(observer)
    }

    private fun notify(key: String, value: Any) {
        observers.forEach {
            it.invoke(key, value) // notifies the observer of the changed value
        }
    }

    fun save(key: String, value: String) {
        val editor = handle?.edit()
        editor?.putString(key, value)
        editor?.apply()
        notify(key, value)
    }

    fun save(key: Int, value: String) {
        if (context != null)
            save(getString(context, key), value)
    }

    fun load(key: String, defaultValue: String): String {
        return handle?.getString(key, defaultValue) ?: defaultValue
    }

    fun load(key: Int, defaultValue: String): String {
        if (context != null)
            return load(getString(context, key), defaultValue)
        else
            return defaultValue
    }

    fun save(key: String, value: Int) {
        val editor = handle?.edit()
        editor?.putInt(key, value)
        editor?.apply()
        notify(key, value)
    }

    fun save(key: Int, value: Int) {
        if (context != null)
            save(getString(context, key), value)
    }

    fun load(key: String, defaultValue: Int): Int {
        val value = handle?.getInt(key, defaultValue)
        if (value != null)
            return value
        else
            return defaultValue
    }

    fun load(key: Int, defaultValue: Int): Int {
        if (context != null)
            return load(getString(context, key), defaultValue)
        else
            return defaultValue
    }

    fun save(key: String, value: Boolean) {
        val editor = handle?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
        notify(key, value)
    }

    fun save(key: Int, value: Boolean) {
        if (context != null)
            save(getString(context, key), value)
    }

    fun load(key: String, defaultValue: Boolean): Boolean {
        val value = handle?.getBoolean(key, defaultValue)
        if (value != null)
            return value
        else
            return defaultValue
    }

    fun load(key: Int, defaultValue: Boolean): Boolean {
        if (context != null)
            return load(getString(context, key), defaultValue)
        else
            return defaultValue
    }
}
