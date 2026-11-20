package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.i18n.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "edusphere_prefs")

class SessionPreferences(private val context: Context) {
    private val keyLoggedInUserId = stringPreferencesKey("logged_in_user_id")
    private val keyLanguage = stringPreferencesKey("app_language")
    private val keyDarkMode = booleanPreferencesKey("dark_mode")
    private val keyAdminPasswordHash = stringPreferencesKey("admin_sherzod_password_hash")
    private val keyAcknowledgedUpdateId = stringPreferencesKey("acknowledged_update_id")
    private val keyInstalledVersion = stringPreferencesKey("installed_version_name")

    val loggedInUserId: Flow<String?> = context.dataStore.data.map { it[keyLoggedInUserId] }

    val appLanguage: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        AppLanguage.fromCode(prefs[keyLanguage] ?: "en")
    }

    val darkMode: Flow<Boolean?> = context.dataStore.data.map { prefs ->
        if (prefs.contains(keyDarkMode)) prefs[keyDarkMode] else null
    }

    val adminPasswordHash: Flow<String?> = context.dataStore.data.map { it[keyAdminPasswordHash] }

    val acknowledgedUpdateId: Flow<String?> = context.dataStore.data.map { it[keyAcknowledgedUpdateId] }

    val installedVersionName: Flow<String> = context.dataStore.data.map { it[keyInstalledVersion] ?: "1.0.0" }

    suspend fun setLoggedInUserId(userId: String?) {
        context.dataStore.edit { prefs ->
            if (userId == null) prefs.remove(keyLoggedInUserId)
            else prefs[keyLoggedInUserId] = userId
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[keyLanguage] = language.code }
    }

    suspend fun setDarkMode(enabled: Boolean?) {
        context.dataStore.edit { prefs ->
            if (enabled == null) prefs.remove(keyDarkMode)
            else prefs[keyDarkMode] = enabled
        }
    }

    suspend fun setAdminPasswordHash(hash: String) {
        context.dataStore.edit { it[keyAdminPasswordHash] = hash }
    }

    suspend fun markUpdateInstalled(updateId: String, versionName: String) {
        context.dataStore.edit { prefs ->
            prefs[keyAcknowledgedUpdateId] = updateId
            prefs[keyInstalledVersion] = versionName
        }
    }

    fun observeNotificationsLastSeen(userId: String): Flow<Long> {
        val key = longPreferencesKey("notif_seen_at_$userId")
        return context.dataStore.data.map { it[key] ?: 0L }
    }

    suspend fun setNotificationsLastSeen(userId: String, timestamp: Long) {
        val key = longPreferencesKey("notif_seen_at_$userId")
        context.dataStore.edit { it[key] = timestamp }
    }
}
