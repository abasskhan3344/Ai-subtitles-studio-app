package com.aiimagestudio.data.repository

import com.aiimagestudio.data.local.GeneratedImageDao
import com.aiimagestudio.data.model.GeneratedImage
import kotlinx.coroutines.flow.Flow

class ImageRepository(private val imageDao: GeneratedImageDao) {
    
    fun getAllImages(): Flow<List<GeneratedImage>> = imageDao.getAllImages()
    
    fun getRecentImages(limit: Int = 10): Flow<List<GeneratedImage>> = 
        imageDao.getRecentImages(limit)
    
    suspend fun getImageById(id: String): GeneratedImage? = imageDao.getImageById(id)
    
    fun getFavoriteImages(): Flow<List<GeneratedImage>> = imageDao.getFavoriteImages()
    
    suspend fun saveImage(image: GeneratedImage) = imageDao.insertImage(image)
    
    suspend fun updateImage(image: GeneratedImage) = imageDao.updateImage(image)
    
    suspend fun deleteImage(image: GeneratedImage) = imageDao.deleteImage(image)
    
    suspend fun deleteImageById(id: String) = imageDao.deleteImageById(id)
    
    suspend fun deleteAllImages() = imageDao.deleteAllImages()
    
    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        imageDao.updateFavoriteStatus(id, isFavorite)
    }
    
    suspend fun getImageCount(): Int = imageDao.getImageCount()
}
