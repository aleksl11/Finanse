package com.example.finanse.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object PreferencesKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
}

suspend fun saveThemeMode(context: Context, themeMode: ThemeMode) {
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.THEME_MODE] = themeMode.name
    }
}

fun getThemeMode(context: Context): Flow<ThemeMode> {
    return context.dataStore.data.map { preferences ->
        val theme = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        ThemeMode.valueOf(theme)
    }
}