package dev.sheershbhatnagar.ai_assistant.core.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {
    companion object {
        val HOST_ADDRESS = stringPreferencesKey("host_address")
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_FIRST_NAME = stringPreferencesKey("user_first_name")
        val USER_LAST_NAME = stringPreferencesKey("user_last_name")
        val DEFAULT_MODEL_ID = stringPreferencesKey("default_model_id")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    }

    val hostAddress: Flow<String?> = context.dataStore.data.map { it[HOST_ADDRESS] }
    val jwtToken: Flow<String?> = context.dataStore.data.map { it[JWT_TOKEN] }
    val userId: Flow<String?> = context.dataStore.data.map { it[USER_ID] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL] }
    val userFirstName: Flow<String?> = context.dataStore.data.map { it[USER_FIRST_NAME] }
    val userLastName: Flow<String?> = context.dataStore.data.map { it[USER_LAST_NAME] }
    val defaultModelId: Flow<String?> = context.dataStore.data.map { it[DEFAULT_MODEL_ID] }
    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { it[IS_DARK_THEME] ?: true }

    suspend fun saveSession(host: String, token: String, userIdStr: String, email: String, firstName: String) {
        context.dataStore.edit { prefs ->
            prefs[HOST_ADDRESS] = host
            prefs[JWT_TOKEN] = token
            prefs[USER_ID] = userIdStr
            prefs[USER_EMAIL] = email
            prefs[USER_FIRST_NAME] = firstName
        }
    }

    suspend fun saveUserProfile(firstName: String, lastName: String?, email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_FIRST_NAME] = firstName
            if (lastName != null) {
                prefs[USER_LAST_NAME] = lastName
            } else {
                prefs.remove(USER_LAST_NAME)
            }
            prefs[USER_EMAIL] = email
        }
    }

    suspend fun saveThemeSetting(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_DARK_THEME] = isDark
        }
    }

    suspend fun saveHostAddress(host: String) {
        context.dataStore.edit { prefs ->
            prefs[HOST_ADDRESS] = host
        }
    }

    suspend fun saveDefaultModel(modelId: String) {
        context.dataStore.edit { prefs ->
            prefs[DEFAULT_MODEL_ID] = modelId
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(JWT_TOKEN)
            prefs.remove(USER_ID)
            prefs.remove(USER_EMAIL)
            prefs.remove(USER_FIRST_NAME)
            prefs.remove(USER_LAST_NAME)
            prefs.remove(DEFAULT_MODEL_ID)
        }
    }
}
