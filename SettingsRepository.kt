package com.aiimagestudio.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.aiimagestudio.data.model.AspectRatio
import com.aiimagestudio.data.model.ImageStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    
    private object PreferencesKeys {
        val API_KEY = stringPreferencesKey("api_key")
        val DEFAULT_STYLE = intPreferencesKey("default_style")
        val DEFAULT_ASPECT_RATIO = intPreferencesKey("default_aspect_ratio")
        val ENHANCE_PROMPTS = booleanPreferencesKey("enhance_prompts")
        val SAVE_TO_GALLERY = booleanPreferencesKey("save_to_gallery")
        val SHOW_ONBOARDING = booleanPreferencesKey("show_onboarding")
        val HD_QUALITY = booleanPreferencesKey("hd_quality")
    }
    
    val apiKey: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.API_KEY] ?: ""
        }
    
    val defaultStyle: Flow<ImageStyle> = context.dataStore.data
        .map { preferences ->
            val styleOrdinal = preferences[PreferencesKeys.DEFAULT_STYLE] ?: 0
            ImageStyle.fromOrdinal(styleOrdinal)
        }
    
    val defaultAspectRatio: Flow<AspectRatio> = context.dataStore.data
        .map { preferences ->
            val ratioOrdinal = preferences[PreferencesKeys.DEFAULT_ASPECT_RATIO] ?: 0
            AspectRatio.fromOrdinal(ratioOrdinal)
        }
    
    val enhancePrompts: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ENHANCE_PROMPTS] ?: true
        }
    
    val saveToGallery: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SAVE_TO_GALLERY] ?: true
        }
    
    val showOnboarding: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SHOW_ONBOARDING] ?: true
        }
    
    val hdQuality: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.HD_QUALITY] ?: true
        }
    
    suspend fun setApiKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.API_KEY] = key
        }
    }
    
    suspend fun setDefaultStyle(style: ImageStyle) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_STYLE] = style.ordinal
        }
    }
    
    suspend fun setDefaultAspectRatio(ratio: AspectRatio) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_ASPECT_RATIO] = ratio.ordinal
        }
    }
    
    suspend fun setEnhancePrompts(enhance: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENHANCE_PROMPTS] = enhance
        }
    }
    
    suspend fun setSaveToGallery(save: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SAVE_TO_GALLERY] = save
        }
    }
    
    suspend fun setShowOnboarding(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_ONBOARDING] = show
        }
    }
    
    suspend fun setHdQuality(hd: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HD_QUALITY] = hd
        }
    }
    
    suspend fun clearSettings() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
