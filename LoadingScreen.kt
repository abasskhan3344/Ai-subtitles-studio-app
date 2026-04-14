package com.aiimagestudio.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aiimagestudio.R
import com.aiimagestudio.data.model.GenerationResult
import com.aiimagestudio.ui.theme.*
import com.aiimagestudio.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    viewModel: MainViewModel
) {
    val generationState by viewModel.generationState.collectAsState()
    
    val loadingStep = when (generationState) {
        is GenerationResult.Loading -> (generationState as GenerationResult.Loading).step
        else -> 0
    }
    
    val loadingMessage = when (generationState) {
        is GenerationResult.Loading -> (generationState as GenerationResult.Loading).message
        else -> stringResource(R.string.generating_title)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Animated Loader
            AIPulseLoader(
                step = loadingStep,
                modifier = Modifier.size(180.dp)
            )
            
            // Loading Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.generating_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = stringResource(R.string.generating_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                
                // Step indicator
                AnimatedContent(
                    targetState = loadingMessage,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
                    },
                    label = "StepMessage"
                ) { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = Primary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Progress dots
            LoadingDots()
        }
    }
}

@Composable
private fun AIPulseLoader(
    step: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "PulseAnimation")
    
    // Multiple pulsing circles
    val circles = listOf(
        0.3f to Primary.copy(alpha = 0.3f),
        0.5f to Primary.copy(alpha = 0.5f),
        0.7f to Primary.copy(alpha = 0.7f),
        1.0f to Primary
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing circles
        circles.forEachIndexed { index, (targetScale, color) ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.6f,
                targetValue = targetScale,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2000,
                        delayMillis = index * 200,
                        easing = EaseInOutSine
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "Scale$index"
            )
            
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 0.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2000,
                        delayMillis = index * 200,
                        easing = EaseInOutSine
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "Alpha$index"
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .alpha(alpha)
                    .background(color, CircleShape)
            )
        }
        
        // Center gradient circle
        val centerScale by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1000,
                    easing = EaseInOutSine
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "CenterScale"
        )
        
        Box(
            modifier = Modifier
                .size(80.dp)
                .scale(centerScale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // AI text
            Text(
                text = "AI",
                style = MaterialTheme.typography.headlineMedium,
                color = TextOnPrimary
            )
        }
        
        // Orbiting dots
        OrbitDots(step = step)
    }
}

@Composable
private fun OrbitDots(step: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbitAnimation")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 8000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )
    
    val dotColors = listOf(Secondary, Accent, SecondaryLight, AccentLight)
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        dotColors.forEachIndexed { index, color ->
            val angle = rotation + (index * 90f)
            val radius = 70.dp
            
            val x = kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat() * radius.value
            val y = kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat() * radius.value
            
            Box(
                modifier = Modifier
                    .offset(x.dp, y.dp)
                    .size(12.dp)
                    .background(color, CircleShape)
            )
        }
    }
}

@Composable
private fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "DotsAnimation")
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = index * 150,
                        easing = EaseInOutSine
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "DotScale$index"
            )
            
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .scale(scale)
                    .background(Primary, CircleShape)
            )
        }
    }
}

// Shimmer effect for loading placeholder
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        ShimmerBase,
        ShimmerHighlight,
        ShimmerBase
    )
    
    val transition = rememberInfiniteTransition(label = "Shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslate"
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(x = translateAnim, y = translateAnim)
    )
    
    Box(
        modifier = modifier.background(brush)
    )
}
