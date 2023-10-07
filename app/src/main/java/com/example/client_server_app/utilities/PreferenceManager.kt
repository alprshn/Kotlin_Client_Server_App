package com.example.client_server_app.utilities

import android.content.Context
import android.content.SharedPreferences
/**
 * A utility class for managing and accessing SharedPreferences in the application.
 * @property PreferenceManager the name of this class
 * @param context The application context used for accessing SharedPreferences.
 */
class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences =
            context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Store a boolean value in SharedPreferences.
     *
     * @param key The key under which the boolean value will be stored.
     * @param value The boolean value to be stored.
     */
    fun PutBoolean(key: String, value: Boolean) {
        var editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * Retrieve a boolean value from SharedPreferences.
     *
     * @param key The key associated with the boolean value.
     * @return The boolean value associated with the key (default is `false` if not found).
     */
    fun GetBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    /**
     * Store a string value in SharedPreferences.
     *
     * @param key The key under which the string value will be stored.
     * @param value The string value to be stored.
     */
    fun PutString(key: String, value: String?) {
        var editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * Retrieve a string value from SharedPreferences.
     *
     * @param key The key associated with the string value.
     * @return The string value associated with the key (default is `null` if not found).
     */
    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    /**
     * Clear all data stored in SharedPreferences.
     */
    fun Clear() {
        var editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}