package com.aiimagestudio.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aiimagestudio.R
import com.aiimagestudio.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: Int,
    val gradient: List<Color>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    onSkip: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_1),
            description = stringResource(R.string.onboarding_desc_1),
            icon = R.drawable.ic_onboarding_create,
            gradient = listOf(GradientStart, GradientEnd)
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_2),
            description = stringResource(R.string.onboarding_desc_2),
            icon = R.drawable.ic_onboarding_styles,
            gradient = listOf(Secondary, Accent)
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_3),
            description = stringResource(R.string.onboarding_desc_3),
            icon = R.drawable.ic_onboarding_quality,
            gradient = listOf(Accent, GradientStart)
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_4),
            description = stringResource(R.string.onboarding_desc_4),
            icon = R.drawable.ic_onboarding_share,
            gradient = listOf(StyleAnime, Secondary)
        )
    )
    
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Skip button
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = onSkip,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = stringResource(R.string.skip),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextTertiary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Page indicators
        PageIndicators(
            pageCount = pages.size,
            currentPage = pagerState.currentPage
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Navigation buttons
        if (pagerState.currentPage == pages.size - 1) {
            // Get Started button on last page
            GetStartedButton(onClick = onGetStarted)
        } else {
            // Next button
            NextButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated illustration
        val infiniteTransition = rememberInfiniteTransition(label = "FloatAnimation")
        val floatOffset by infiniteTransition.animateFloat(
            initialValue = -10f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Float"
        )
        
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(y = floatOffset.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = page.gradient.map { it.copy(alpha = 0.2f) }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(colors = page.gradient)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = page.icon),
                    contentDescription = null,
                    tint = TextOnPrimary,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PageIndicators(
    pageCount: Int,
    currentPage: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            
            val width by animateDpAsState(
                targetValue = if (isSelected) 24.dp else 8.dp,
                animationSpec = tween(300),
                label = "IndicatorWidth"
            )
            
            val color by animateColorAsState(
                targetValue = if (isSelected) Primary else Border,
                animationSpec = tween(300),
                label = "IndicatorColor"
            )
            
            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
private fun NextButton(onClick: () -> Unit) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(GradientStart, GradientEnd)
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(8.dp, ButtonShape, spotColor = Primary)
            .clip(ButtonShape)
            .background(gradientBrush)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.next),
                style = MaterialTheme.typography.labelLarge,
                color = TextOnPrimary
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = TextOnPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun GetStartedButton(onClick: () -> Unit) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(GradientStart, GradientEnd)
    )
    
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ButtonScale"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .shadow(12.dp, ButtonShape, spotColor = Primary)
            .clip(ButtonShape)
            .background(gradientBrush)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.get_started),
            style = MaterialTheme.typography.labelLarge,
            color = TextOnPrimary
        )
    }
}
