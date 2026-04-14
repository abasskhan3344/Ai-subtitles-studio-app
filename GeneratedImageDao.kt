package com.aiimagestudio.data.local

import androidx.room.*
import com.aiimagestudio.data.model.GeneratedImage
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneratedImageDao {
    
    @Query("SELECT * FROM generated_images ORDER BY createdAt DESC")
    fun getAllImages(): Flow<List<GeneratedImage>>
    
    @Query("SELECT * FROM generated_images ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentImages(limit: Int): Flow<List<GeneratedImage>>
    
    @Query("SELECT * FROM generated_images WHERE id = :id")
    suspend fun getImageById(id: String): GeneratedImage?
    
    @Query("SELECT * FROM generated_images WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteImages(): Flow<List<GeneratedImage>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: GeneratedImage)
    
    @Update
    suspend fun updateImage(image: GeneratedImage)
    
    @Delete
    suspend fun deleteImage(image: GeneratedImage)
    
    @Query("DELETE FROM generated_images WHERE id = :id")
    suspend fun deleteImageById(id: String)
    
    @Query("DELETE FROM generated_images")
    suspend fun deleteAllImages()
    
    @Query("SELECT COUNT(*) FROM generated_images")
    suspend fun getImageCount(): Int
    
    @Query("UPDATE generated_images SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)
}
