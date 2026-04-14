package com.aiimagestudio.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiimagestudio.api.ImageGenerationService
import com.aiimagestudio.data.model.*
import com.aiimagestudio.data.repository.ImageRepository
import com.aiimagestudio.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val imageRepository: ImageRepository,
    private val settingsRepository: SettingsRepository,
    private val imageGenerationService: ImageGenerationService
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Generation State
    private val _generationState = MutableStateFlow<GenerationResult?>(null)
    val generationState: StateFlow<GenerationResult?> = _generationState.asStateFlow()

    // Settings
    val apiKey: StateFlow<String> = settingsRepository.apiKey
        .stateIn(viewModelScope, SharingStarted.Eager, "")

    val enhancePrompts: StateFlow<Boolean> = settingsRepository.enhancePrompts
        .stateIn(viewModelScope, SharingStarted.Eager, true)

    val showOnboarding: StateFlow<Boolean> = settingsRepository.showOnboarding
        .stateIn(viewModelScope, SharingStarted.Eager, true)

    // Recent images
    val recentImages: StateFlow<List<GeneratedImage>> = imageRepository.getRecentImages(10)
        .stateIn(viewModelScope, SharingStarted.Eager, emptyList())

    init {
        viewModelScope.launch {
            // Load default settings
            settingsRepository.defaultStyle.collect { style ->
                _uiState.update { it.copy(selectedStyle = style) }
            }
        }

        viewModelScope.launch {
            settingsRepository.defaultAspectRatio.collect { ratio ->
                _uiState.update { it.copy(selectedAspectRatio = ratio) }
            }
        }
    }

    fun updatePrompt(prompt: String) {
        _uiState.update { it.copy(prompt = prompt) }
    }

    fun selectStyle(style: ImageStyle) {
        _uiState.update { it.copy(selectedStyle = style) }
    }

    fun selectAspectRatio(ratio: AspectRatio) {
        _uiState.update { it.copy(selectedAspectRatio = ratio) }
    }

    fun generateImage() {
        val currentState = _uiState.value
        val key = apiKey.value

        if (currentState.prompt.isBlank()) {
            _generationState.value = GenerationResult.Error(
                "Please enter a prompt",
                GenerationResult.ErrorType.INVALID_PROMPT
            )
            return
        }

        if (key.isBlank()) {
            _generationState.value = GenerationResult.Error(
                "Please set your API key in settings",
                GenerationResult.ErrorType.API_ERROR
            )
            return
        }

        viewModelScope.launch {
            imageGenerationService.generateImage(
                prompt = currentState.prompt,
                style = currentState.selectedStyle,
                aspectRatio = currentState.selectedAspectRatio,
                apiKey = key,
                enhancePrompt = enhancePrompts.value
            ).collect { result ->
                _generationState.value = result

                if (result is GenerationResult.Success) {
                    // Save to database
                    saveGeneratedImage(result)
                }
            }
        }
    }

    private suspend fun saveGeneratedImage(result: GenerationResult.Success) {
        // Image is already saved locally by the service
        // We just need to record it in the database
        // This will be implemented when we have the file path
    }

    fun resetGenerationState() {
        _generationState.value = null
    }

    fun clearPrompt() {
        _uiState.update { it.copy(prompt = "") }
    }

    fun setApiKey(key: String) {
        viewModelScope.launch {
            settingsRepository.setApiKey(key)
        }
    }

    fun setShowOnboarding(show: Boolean) {
        viewModelScope.launch {
            settingsRepository.setShowOnboarding(show)
        }
    }

    fun clearError() {
        _generationState.value = null
    }

    data class UiState(
        val prompt: String = "",
        val selectedStyle: ImageStyle = ImageStyle.default(),
        val selectedAspectRatio: AspectRatio = AspectRatio.default(),
        val isGenerating: Boolean = false
    )

    class Factory(
        private val imageRepository: ImageRepository,
        private val settingsRepository: SettingsRepository,
        private val imageGenerationService: ImageGenerationService
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(
                    imageRepository,
                    settingsRepository,
                    imageGenerationService
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
