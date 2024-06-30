package com.example.codriving

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("codriving_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val DARK_MODE_KEY = "dark_mode"
    }

    fun setDarkMode(enabled: Boolean) {
        preferences.edit().putBoolean(DARK_MODE_KEY, enabled).apply()
    }

    fun isDarkMode(): Boolean {
        return preferences.getBoolean(DARK_MODE_KEY, false)
    }
}
