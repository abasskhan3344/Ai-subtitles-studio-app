package com.aiimagestudio.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.aiimagestudio.R
import com.aiimagestudio.ui.theme.*

enum class ImageStyle(
    @StringRes val displayName: Int,
    @DrawableRes val icon: Int,
    val color: Color,
    val promptModifier: String
) {
    REALISTIC(
        R.string.style_realistic,
        R.drawable.ic_style_realistic,
        StyleRealistic,
        "photorealistic, highly detailed, 8k resolution, professional photography, " +
        "sharp focus, natural lighting, ultra realistic textures, " +
        "cinematic composition, depth of field"
    ),
    
    CINEMATIC(
        R.string.style_cinematic,
        R.drawable.ic_style_cinematic,
        StyleCinematic,
        "cinematic shot, movie still, film grain, dramatic lighting, " +
        "anamorphic lens, color graded, epic composition, " +
        "theatrical atmosphere, Hollywood production quality"
    ),
    
    ANIME(
        R.string.style_anime,
        R.drawable.ic_style_anime,
        StyleAnime,
        "anime style, manga art, vibrant colors, cel shaded, " +
        "studio ghibli inspired, clean linework, expressive eyes, " +
        "detailed background, Japanese animation aesthetic"
    ),
    
    THREE_D(
        R.string.style_3d,
        R.drawable.ic_style_3d,
        Style3D,
        "3D render, octane render, blender, c4d, " +
        "ray tracing, subsurface scattering, volumetric lighting, " +
        "physically based rendering, photorealistic 3D, studio lighting"
    ),
    
    DIGITAL_ART(
        R.string.style_digital,
        R.drawable.ic_style_digital,
        StyleDigital,
        "digital art, digital painting, concept art, " +
        "illustration, vibrant colors, detailed artwork, " +
        "professional digital illustration, artstation trending"
    ),
    
    OIL_PAINTING(
        R.string.style_oil,
        R.drawable.ic_style_oil,
        StyleOil,
        "oil painting, classical art, impasto technique, " +
        "rich textures, masterful brushstrokes, museum quality, " +
        "Renaissance inspired, canvas texture, traditional art"
    ),
    
    WATERCOLOR(
        R.string.style_watercolor,
        R.drawable.ic_style_watercolor,
        StyleWatercolor,
        "watercolor painting, wet on wet technique, " +
        "soft edges, flowing colors, artistic wash, " +
        "delicate transparency, paper texture, impressionistic"
    ),
    
    SKETCH(
        R.string.style_sketch,
        R.drawable.ic_style_sketch,
        StyleSketch,
        "pencil sketch, graphite drawing, hand drawn, " +
        "cross hatching, detailed linework, artistic sketch, " +
        "monochrome, paper texture, traditional drawing"
    ),
    
    FANTASY(
        R.string.style_fantasy,
        R.drawable.ic_style_fantasy,
        StyleFantasy,
        "fantasy art, magical atmosphere, ethereal lighting, " +
        "mystical elements, otherworldly, enchanted, " +
        "epic fantasy illustration, dramatic composition"
    ),
    
    MINIMALIST(
        R.string.style_minimalist,
        R.drawable.ic_style_minimalist,
        StyleMinimalist,
        "minimalist, clean design, simple composition, " +
        "negative space, geometric shapes, modern aesthetic, " +
        "subtle colors, elegant simplicity, contemporary art"
    ),
    
    ABSTRACT(
        R.string.style_abstract,
        R.drawable.ic_style_abstract,
        StyleAbstract,
        "abstract art, non-representational, bold colors, " +
        "geometric patterns, expressive forms, contemporary abstract, " +
        "artistic composition, visual harmony, modern art"
    );
    
    companion object {
        fun fromOrdinal(ordinal: Int): ImageStyle {
            return entries.getOrElse(ordinal) { REALISTIC }
        }
        
        fun default(): ImageStyle = REALISTIC
    }
}
