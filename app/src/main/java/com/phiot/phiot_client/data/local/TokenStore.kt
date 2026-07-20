package com.phiot.phiot_client.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.phiot.phiot_client.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = AppConfig.DATASTORE_NAME,
)

class TokenStore(private val context: Context) {

    private val tokenKey = stringPreferencesKey("token")
    private val emailKey = stringPreferencesKey("email")

    val session: Flow<UserSession?> = context.dataStore.data.map { preferences ->
        val token = preferences[tokenKey].orEmpty()
        val email = preferences[emailKey].orEmpty()
        if (token.isBlank()) null else UserSession(token = token, email = email)
    }

    suspend fun saveSession(token: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token
            preferences[emailKey] = email
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(tokenKey)
            preferences.remove(emailKey)
        }
    }
}

data class UserSession(
    val token: String,
    val email: String,
)
