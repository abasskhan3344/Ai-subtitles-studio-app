package com.aiimagestudio.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "generated_images")
data class GeneratedImage(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val prompt: String,
    val enhancedPrompt: String,
    val style: String,
    val aspectRatio: String,
    val imagePath: String,
    val thumbnailPath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    val createdDate: Date
        get() = Date(createdAt)
    
    fun getStyleEnum(): ImageStyle {
        return try {
            ImageStyle.valueOf(style)
        } catch (e: IllegalArgumentException) {
            ImageStyle.default()
        }
    }
    
    fun getAspectRatioEnum(): AspectRatio {
        return try {
            AspectRatio.valueOf(aspectRatio)
        } catch (e: IllegalArgumentException) {
            AspectRatio.default()
        }
    }
}
