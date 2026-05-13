package com.vdharmani.starter.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the auth tokens.
 *
 * Backed by DataStore Preferences — async I/O, no SharedPreferences pitfalls.
 * Junior tip: never read this on the main thread (no blocking accessors are
 * exposed); always observe [authTokenFlow] or call [read] from a coroutine.
 */
@Singleton
class TokenStore @Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore by preferencesDataStore(name = STORE_NAME)

    /** Hot snapshot of the current auth state. Emits `null` when signed out. */
    val authTokenFlow: Flow<StoredAuthToken?> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            val access = prefs[KEY_ACCESS]
            val refresh = prefs[KEY_REFRESH]
            if (access.isNullOrEmpty()) null
            else StoredAuthToken(access, refresh.orEmpty())
        }

    /** Snapshot read for one-shot callers (e.g. an OkHttp interceptor). */
    suspend fun read(): StoredAuthToken? = authTokenFlow.first()

    suspend fun save(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS] = accessToken
            prefs[KEY_REFRESH] = refreshToken
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    private companion object {
        const val STORE_NAME = "starter_auth"
        val KEY_ACCESS = stringPreferencesKey("access_token")
        val KEY_REFRESH = stringPreferencesKey("refresh_token")
    }
}

data class StoredAuthToken(val accessToken: String, val refreshToken: String)
