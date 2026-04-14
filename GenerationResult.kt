package com.aiimagestudio.data.model

import android.graphics.Bitmap

sealed class GenerationResult {
    data class Success(
        val bitmap: Bitmap,
        val prompt: String,
        val style: ImageStyle,
        val aspectRatio: AspectRatio,
        val enhancedPrompt: String
    ) : GenerationResult()
    
    data class Error(
        val message: String,
        val type: ErrorType
    ) : GenerationResult()
    
    data class Loading(
        val step: Int = 0,
        val message: String = ""
    ) : GenerationResult()
    
    enum class ErrorType {
        NETWORK,
        API_ERROR,
        RATE_LIMIT,
        INVALID_PROMPT,
        TIMEOUT,
        UNKNOWN
    }
}

sealed class DownloadResult {
    data class Success(val filePath: String) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
}
