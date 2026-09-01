package com.memora.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.memora.core.model.DarkMode
import com.memora.core.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    private val context: Context
) {
    private val darkModeKey = stringPreferencesKey("dark_mode")
    private val notificationsEnabledKey = booleanPreferencesKey("notifications_enabled")
    private val languageKey = stringPreferencesKey("language")
    private val backupEnabledKey = booleanPreferencesKey("backup_enabled")

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            UserPreferences(
                darkMode = DarkMode.valueOf(preferences[darkModeKey] ?: DarkMode.SYSTEM.name),
                notificationsEnabled = preferences[notificationsEnabledKey] ?: true,
                language = preferences[languageKey] ?: "en",
                backupEnabled = preferences[backupEnabledKey] ?: false
            )
        }

    suspend fun updateDarkMode(darkMode: DarkMode) {
        context.dataStore.edit { preferences ->
            preferences[darkModeKey] = darkMode.name
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[notificationsEnabledKey] = enabled
        }
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[languageKey] = language
        }
    }

    suspend fun updateBackupEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[backupEnabledKey] = enabled
        }
    }
}
