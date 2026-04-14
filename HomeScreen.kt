package com.aiimagestudio.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aiimagestudio.R
import com.aiimagestudio.data.model.AspectRatio
import com.aiimagestudio.data.model.GenerationResult
import com.aiimagestudio.data.model.ImageStyle
import com.aiimagestudio.ui.theme.*
import com.aiimagestudio.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val generationState by viewModel.generationState.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    val focusManager = LocalFocusManager.current
    
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Show error dialog when there's an error
    LaunchedEffect(generationState) {
        if (generationState is GenerationResult.Error) {
            errorMessage = (generationState as GenerationResult.Error).message
            showErrorDialog = true
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.cd_settings_button),
                            tint = Primary
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Prompt Input Section
            PromptInputSection(
                prompt = uiState.prompt,
                onPromptChange = viewModel::updatePrompt,
                onClear = viewModel::clearPrompt,
                focusManager = focusManager
            )
            
            // Style Selector Section
            StyleSelectorSection(
                selectedStyle = uiState.selectedStyle,
                onStyleSelected = viewModel::selectStyle
            )
            
            // Aspect Ratio Section
            AspectRatioSection(
                selectedRatio = uiState.selectedAspectRatio,
                onRatioSelected = viewModel::selectAspectRatio
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Generate Button
            GenerateButton(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.generateImage()
                },
                enabled = uiState.prompt.isNotBlank() && apiKey.isNotBlank()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    
    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.titleLarge,
                    color = Error
                )
            },
            text = {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK", color = Primary)
                }
            },
            containerColor = Surface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PromptInputSection(
    prompt: String,
    onPromptChange: (String) -> Unit,
    onClear: () -> Unit,
    focusManager: FocusManager
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Your Idea",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        
        OutlinedTextField(
            value = prompt,
            onValueChange = onPromptChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            placeholder = {
                Text(
                    text = stringResource(R.string.prompt_hint),
                    color = TextTertiary
                )
            },
            trailingIcon = {
                if (prompt.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = TextTertiary
                        )
                    }
                }
            },
            shape = InputShape,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = BackgroundInput,
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            maxLines = 5
        )
        
        Text(
            text = stringResource(R.string.prompt_helper),
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary
        )
    }
}

@Composable
private fun StyleSelectorSection(
    selectedStyle: ImageStyle,
    onStyleSelected: (ImageStyle) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.select_style),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
        ) {
            items(ImageStyle.entries.toTypedArray()) { style ->
                StyleCard(
                    style = style,
                    isSelected = style == selectedStyle,
                    onClick = { onStyleSelected(style) }
                )
            }
        }
    }
}

@Composable
private fun StyleCard(
    style: ImageStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) style.color.copy(alpha = 0.15f) else BackgroundCard,
        animationSpec = tween(200),
        label = "BackgroundColor"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) style.color else Border,
        animationSpec = tween(200),
        label = "BorderColor"
    )
    
    Column(
        modifier = Modifier
            .width(80.dp)
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp,
                shape = CardShape,
                spotColor = if (isSelected) style.color else ShadowLight
            )
            .clip(CardShape)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = CardShape
            )
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Style Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(style.color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = style.icon),
                contentDescription = null,
                tint = style.color,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Style Name
        Text(
            text = stringResource(style.displayName),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) style.color else TextSecondary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun AspectRatioSection(
    selectedRatio: AspectRatio,
    onRatioSelected: (AspectRatio) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.aspect_ratio),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AspectRatio.entries.forEach { ratio ->
                AspectRatioButton(
                    ratio = ratio,
                    isSelected = ratio == selectedRatio,
                    onClick = { onRatioSelected(ratio) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AspectRatioButton(
    ratio: AspectRatio,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Primary else BackgroundCard,
        animationSpec = tween(200),
        label = "BackgroundColor"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) TextOnPrimary else TextSecondary,
        animationSpec = tween(200),
        label = "ContentColor"
    )
    
    Box(
        modifier = modifier
            .height(48.dp)
            .shadow(
                elevation = if (isSelected) 4.dp else 2.dp,
                shape = ButtonShape,
                spotColor = if (isSelected) Primary else ShadowLight
            )
            .clip(ButtonShape)
            .background(backgroundColor)
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = if (isSelected) Color.Transparent else Border,
                shape = ButtonShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(ratio.displayName),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor
        )
    }
}

@Composable
private fun GenerateButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(GradientStart, GradientEnd)
    )
    
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.98f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "Scale"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = ButtonShape,
                spotColor = Primary
            )
            .clip(ButtonShape)
            .background(gradientBrush)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoFixHigh,
                contentDescription = null,
                tint = TextOnPrimary
            )
            Text(
                text = stringResource(R.string.generate_button),
                style = MaterialTheme.typography.labelLarge,
                color = TextOnPrimary
            )
        }
    }
}
