package com.aiimagestudio.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ImageUtils {
    
    suspend fun saveImageToGallery(context: Context, bitmap: Bitmap): Result<Uri> = 
        withContext(Dispatchers.IO) {
            try {
                val filename = "AI_Studio_${System.currentTimeMillis()}.png"
                var fos: OutputStream? = null
                var imageUri: Uri? = null
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ - use MediaStore
                    context.contentResolver?.also { resolver ->
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AIImageStudio")
                        }
                        
                        imageUri = resolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )
                        
                        fos = imageUri?.let { resolver.openOutputStream(it) }
                    }
                } else {
                    // Pre-Android 10 - use direct file access
                    val directory = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "AIImageStudio"
                    )
                    if (!directory.exists()) {
                        directory.mkdirs()
                    }
                    
                    val image = File(directory, filename)
                    fos = FileOutputStream(image)
                    
                    imageUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        image
                    )
                    
                    // Add to MediaStore
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DATA, image.absolutePath)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    }
                    context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                }
                
                fos?.use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                
                imageUri?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Failed to create image URI"))
                
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    fun shareImage(context: Context, uri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(shareIntent, "Share Image")
        context.startActivity(chooser)
    }
    
    fun createShareUri(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val cachePath = File(context.cacheDir, "images").apply {
                if (!exists()) mkdirs()
            }
            
            val file = File(cachePath, "share_image_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun getAspectRatioString(ratio: com.aiimagestudio.data.model.AspectRatio): String {
        return when (ratio) {
            com.aiimagestudio.data.model.AspectRatio.SQUARE -> "1:1"
            com.aiimagestudio.data.model.AspectRatio.PORTRAIT -> "2:3"
            com.aiimagestudio.data.model.AspectRatio.INSTAGRAM -> "4:5"
            com.aiimagestudio.data.model.AspectRatio.WIDE -> "16:9"
            com.aiimagestudio.data.model.AspectRatio.ULTRAWIDE -> "21:9"
        }
    }
}
