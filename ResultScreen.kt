package com.aiimagestudio.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aiimagestudio.R
import com.aiimagestudio.data.model.GenerationResult
import com.aiimagestudio.ui.theme.*
import com.aiimagestudio.utils.ImageUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    result: GenerationResult.Success,
    onBack: () -> Unit,
    onRegenerate: () -> Unit,
    onNewImage: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var showFullImage by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var isSharing by remember { mutableStateOf(false) }
    
    // Image reveal animation
    val imageAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 800,
            easing = EaseOutCubic
        ),
        label = "ImageAlpha"
    )
    
    val imageScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ImageScale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.result_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back_button),
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Image Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(CardShape)
                    .background(BackgroundSecondary)
                    .clickable { showFullImage = true }
            ) {
                Image(
                    bitmap = result.bitmap.asImageBitmap(),
                    contentDescription = stringResource(R.string.cd_generated_image),
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(imageAlpha)
                        .scale(imageScale),
                    contentScale = ContentScale.Fit
                )
                
                // Tap to expand hint
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .clip(CircleShape)
                        .background(OverlayDark)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Tap to expand",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextOnPrimary
                    )
                }
            }
            
            // Prompt info
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Original Prompt",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary
                )
                Text(
                    text = result.prompt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            
            // Style & Ratio info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChip(
                    icon = result.style.icon,
                    text = stringResource(result.style.displayName),
                    color = result.style.color,
                    modifier = Modifier.weight(1f)
                )
                InfoChip(
                    text = stringResource(result.aspectRatio.displayName),
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Download & Share Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Download Button
                    ActionButton(
                        icon = Icons.Default.Download,
                        text = stringResource(R.string.download_button),
                        onClick = {
                            scope.launch {
                                isDownloading = true
                                val saveResult = ImageUtils.saveImageToGallery(context, result.bitmap)
                                isDownloading = false
                                
                                saveResult.fold(
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            R.string.download_success,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onFailure = {
                                        Toast.makeText(
                                            context,
                                            R.string.download_error,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        },
                        isLoading = isDownloading,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Share Button
                    ActionButton(
                        icon = Icons.Default.Share,
                        text = stringResource(R.string.share_button),
                        onClick = {
                            scope.launch {
                                isSharing = true
                                val uri = ImageUtils.createShareUri(context, result.bitmap)
                                isSharing = false
                                
                                uri?.let {
                                    ImageUtils.shareImage(context, it)
                                }
                            }
                        },
                        isLoading = isSharing,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Regenerate Button
                OutlinedButton(
                    onClick = onRegenerate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = ButtonShape,
                    border = BorderStroke(1.dp, Primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.regenerate_button),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                
                // New Image Button
                TextButton(
                    onClick = onNewImage,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.new_image_button),
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    
    // Full Image Dialog
    if (showFullImage) {
        FullImageDialog(
            bitmap = result.bitmap,
            onDismiss = { showFullImage = false }
        )
    }
}

@Composable
private fun InfoChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    icon: Int? = null
) {
    Row(
        modifier = modifier
            .clip(ChipShape)
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(GradientStart, GradientEnd)
    )
    
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(ButtonShape)
            .background(gradientBrush)
            .clickable(enabled = !isLoading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = TextOnPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextOnPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextOnPrimary
                )
            }
        }
    }
}

@Composable
private fun FullImageDialog(
    bitmap: android.graphics.Bitmap,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OverlayDark)
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(OverlayLight)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cd_close_button),
                    tint = TextPrimary
                )
            }
            
            // Full image
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        }
    }
}
