package com.aicc.coldcall.feature.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aicc.coldcall.core.network.BaseUrlProvider
import com.aicc.coldcall.core.network.TokenProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerConfigStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : TokenProvider, BaseUrlProvider {

    private companion object {
        val BACKEND_URL_KEY = stringPreferencesKey("backend_url")
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        const val DEFAULT_URL = "http://10.0.2.2:8000/"
    }

    override fun getBaseUrl(): String = runBlocking {
        dataStore.data.map { prefs -> prefs[BACKEND_URL_KEY] ?: DEFAULT_URL }.first()
    }

    override suspend fun getToken(): String? =
        dataStore.data.map { prefs -> prefs[AUTH_TOKEN_KEY] }.first()

    suspend fun saveBackendUrl(url: String) {
        dataStore.edit { prefs -> prefs[BACKEND_URL_KEY] = url }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { prefs -> prefs[AUTH_TOKEN_KEY] = token }
    }

    suspend fun clearToken() {
        dataStore.edit { prefs -> prefs.remove(AUTH_TOKEN_KEY) }
    }
}
