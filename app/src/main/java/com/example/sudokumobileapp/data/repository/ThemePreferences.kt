package com.example.sudokumobileapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object ThemePreferences {
    private val Context.dataStore by preferencesDataStore(name = "settings")
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

    suspend fun saveTheme(context: Context, isDark: Boolean) {
        context.dataStore.edit { it[DARK_THEME_KEY] = isDark }
    }

    suspend fun loadTheme(context: Context): Boolean {
        val prefs = context.dataStore.data.first()
        return prefs[DARK_THEME_KEY] ?: false // default: light mode
    }
}
