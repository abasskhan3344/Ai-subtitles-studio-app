package com.aiimagestudio.data.model

import androidx.annotation.StringRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aiimagestudio.R

enum class AspectRatio(
    @StringRes val displayName: Int,
    val widthRatio: Float,
    val heightRatio: Float,
    val apiWidth: Int,
    val apiHeight: Int
) {
    SQUARE(
        R.string.ratio_1_1,
        1f, 1f,
        1024, 1024
    ),
    
    PORTRAIT(
        R.string.ratio_2_3,
        2f, 3f,
        768, 1152
    ),
    
    INSTAGRAM(
        R.string.ratio_4_5,
        4f, 5f,
        832, 1040
    ),
    
    WIDE(
        R.string.ratio_16_9,
        16f, 9f,
        1216, 704
    ),
    
    ULTRAWIDE(
        R.string.ratio_21_9,
        21f, 9f,
        1344, 576
    );
    
    val ratio: Float get() = widthRatio / heightRatio
    
    fun getWidth(height: Dp): Dp = (height.value * ratio).dp
    fun getHeight(width: Dp): Dp = (width.value / ratio).dp
    
    companion object {
        fun fromOrdinal(ordinal: Int): AspectRatio {
            return entries.getOrElse(ordinal) { SQUARE }
        }
        
        fun default(): AspectRatio = SQUARE
    }
}
