package com.aiimagestudio.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface HuggingFaceApiService {
    
    @POST("models/stabilityai/stable-diffusion-xl-base-1.0")
    suspend fun generateImage(
        @Header("Authorization") authorization: String,
        @Body request: RequestBody
    ): Response<ResponseBody>
    
    @POST("models/runwayml/stable-diffusion-v1-5")
    suspend fun generateImageV15(
        @Header("Authorization") authorization: String,
        @Body request: RequestBody
    ): Response<ResponseBody>
    
    @POST("models/stabilityai/stable-diffusion-2-1")
    suspend fun generateImageV21(
        @Header("Authorization") authorization: String,
        @Body request: RequestBody
    ): Response<ResponseBody>
    
    companion object {
        const val BASE_URL = "https://api-inference.huggingface.co/"
        const val DEFAULT_MODEL = "stabilityai/stable-diffusion-xl-base-1.0"
    }
}

data class GenerationRequest(
    val inputs: String,
    val parameters: GenerationParameters = GenerationParameters()
)

data class GenerationParameters(
    val width: Int = 1024,
    val height: Int = 1024,
    val num_inference_steps: Int = 50,
    val guidance_scale: Float = 7.5f,
    val negative_prompt: String = DEFAULT_NEGATIVE_PROMPT
) {
    companion object {
        const val DEFAULT_NEGATIVE_PROMPT = "blurry, low quality, distorted, deformed, " +
            "ugly, duplicate, watermark, signature, text, logo, cropped, " +
            "out of frame, worst quality, low resolution, normal quality, " +
            "username, artist name, error, bad anatomy, bad hands, " +
            "missing fingers, extra digit, fewer digits"
    }
}
