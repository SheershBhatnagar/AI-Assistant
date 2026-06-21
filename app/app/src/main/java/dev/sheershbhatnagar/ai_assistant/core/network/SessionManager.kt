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
        val DEFAULT_MODEL_ID = stringPreferencesKey("default_model_id")
    }

    val hostAddress: Flow<String?> = context.dataStore.data.map { it[HOST_ADDRESS] }
    val jwtToken: Flow<String?> = context.dataStore.data.map { it[JWT_TOKEN] }
    val userId: Flow<String?> = context.dataStore.data.map { it[USER_ID] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL] }
    val userFirstName: Flow<String?> = context.dataStore.data.map { it[USER_FIRST_NAME] }
    val defaultModelId: Flow<String?> = context.dataStore.data.map { it[DEFAULT_MODEL_ID] }

    suspend fun saveSession(host: String, token: String, userIdStr: String, email: String, firstName: String) {
        context.dataStore.edit { prefs ->
            prefs[HOST_ADDRESS] = host
            prefs[JWT_TOKEN] = token
            prefs[USER_ID] = userIdStr
            prefs[USER_EMAIL] = email
            prefs[USER_FIRST_NAME] = firstName
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
            prefs.remove(DEFAULT_MODEL_ID)
        }
    }
}
