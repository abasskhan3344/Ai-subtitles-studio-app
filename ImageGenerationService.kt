package com.aiimagestudio.api

import android.content.Context
import android.graphics.Bitmap
nimport android.graphics.BitmapFactory
import com.aiimagestudio.data.model.AspectRatio
import com.aiimagestudio.data.model.GenerationResult
import com.aiimagestudio.data.model.ImageStyle
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageGenerationService(private val context: Context) {
    
    private val apiService = RetrofitClient.apiService
    private val gson = Gson()
    
    fun generateImage(
        prompt: String,
        style: ImageStyle,
        aspectRatio: AspectRatio,
        apiKey: String,
        enhancePrompt: Boolean = true
    ): Flow<GenerationResult> = flow {
        emit(GenerationResult.Loading(0, "Analyzing your prompt…"))
        
        try {
            // Enhance the prompt
            val enhancedPrompt = if (enhancePrompt) {
                enhanceUserPrompt(prompt, style)
            } else {
                prompt
            }
            
            emit(GenerationResult.Loading(1, "Crafting the composition…"))
            delay(500)
            
            emit(GenerationResult.Loading(2, "Adding colors and details…"))
            
            // Prepare the request
            val request = GenerationRequest(
                inputs = enhancedPrompt,
                parameters = GenerationParameters(
                    width = aspectRatio.apiWidth,
                    height = aspectRatio.apiHeight,
                    num_inference_steps = 50,
                    guidance_scale = 7.5f
                )
            )
            
            val requestBody = gson.toJson(request)
                .toRequestBody("application/json".toMediaType())
            
            val authHeader = "Bearer $apiKey"
            
            emit(GenerationResult.Loading(3, "Finalizing your masterpiece…"))
            
            // Make API call
            val response = withContext(Dispatchers.IO) {
                apiService.generateImage(authHeader, requestBody)
            }
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val bytes = responseBody.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    
                    if (bitmap != null) {
                        // Save the image locally
                        val savedPath = saveImageLocally(bitmap)
                        
                        emit(GenerationResult.Success(
                            bitmap = bitmap,
                            prompt = prompt,
                            style = style,
                            aspectRatio = aspectRatio,
                            enhancedPrompt = enhancedPrompt
                        ))
                    } else {
                        emit(GenerationResult.Error(
                            "Failed to decode image",
                            GenerationResult.ErrorType.API_ERROR
                        ))
                    }
                } else {
                    emit(GenerationResult.Error(
                        "Empty response from server",
                        GenerationResult.ErrorType.API_ERROR
                    ))
                }
            } else {
                val errorType = when (response.code()) {
                    401 -> GenerationResult.ErrorType.API_ERROR
                    403 -> GenerationResult.ErrorType.API_ERROR
                    429 -> GenerationResult.ErrorType.RATE_LIMIT
                    503 -> GenerationResult.ErrorType.API_ERROR
                    else -> GenerationResult.ErrorType.UNKNOWN
                }
                
                val errorMessage = when (response.code()) {
                    401 -> "Invalid API key. Please check your settings."
                    403 -> "Access denied. Please check your API key."
                    429 -> "Too many requests. Please wait a moment."
                    503 -> "Model is loading. Please try again in a moment."
                    else -> "Server error: ${response.code()}"
                }
                
                emit(GenerationResult.Error(errorMessage, errorType))
            }
            
        } catch (e: IOException) {
            emit(GenerationResult.Error(
                "Network error. Please check your connection.",
                GenerationResult.ErrorType.NETWORK
            ))
        } catch (e: Exception) {
            emit(GenerationResult.Error(
                e.message ?: "Something went wrong",
                GenerationResult.ErrorType.UNKNOWN
            ))
        }
    }
    
    private fun enhanceUserPrompt(prompt: String, style: ImageStyle): String {
        val qualityModifiers = "masterpiece, best quality, highly detailed, " +
            "professional artwork, stunning visual, 8k uhd, " +
            "intricate details, perfect composition"
        
        val lightingModifiers = "perfect lighting, dramatic shadows, " +
            "volumetric lighting, cinematic atmosphere"
        
        return buildString {
            append(prompt)
            append(", ")
            append(style.promptModifier)
            append(", ")
            append(qualityModifiers)
            append(", ")
            append(lightingModifiers)
        }
    }
    
    private fun saveImageLocally(bitmap: Bitmap): String {
        val directory = File(context.filesDir, "generated_images").apply {
            if (!exists()) mkdirs()
        }
        
        val fileName = "image_${System.currentTimeMillis()}.png"
        val file = File(directory, fileName)
        
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        
        return file.absolutePath
    }
    
    suspend fun saveImageToGallery(bitmap: Bitmap): Result<String> = withContext(Dispatchers.IO) {
        try {
            val directory = File(context.getExternalFilesDir(null), "Pictures/AIImageStudio").apply {
                if (!exists()) mkdirs()
            }
            
            val fileName = "AI_Studio_${System.currentTimeMillis()}.png"
            val file = File(directory, fileName)
            
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    companion object {
        const val TAG = "ImageGenerationService"
    }
}
