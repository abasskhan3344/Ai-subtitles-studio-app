package com.aiimagestudio.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.aiimagestudio.R
import com.aiimagestudio.ui.theme.*
import com.aiimagestudio.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val apiKey by viewModel.apiKey.collectAsState()
    val enhancePrompts by viewModel.enhancePrompts.collectAsState()
    
    var apiKeyInput by remember { mutableStateOf(apiKey) }
    var showApiKey by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // API Key Section
            SettingsSection(title = "API Configuration") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.api_key_title),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.api_key_hint),
                                color = TextTertiary
                            )
                        },
                        trailingIcon = {
                            Row {
                                IconButton(onClick = { showApiKey = !showApiKey }) {
                                    Icon(
                                        imageVector = if (showApiKey) 
                                            Icons.Default.VisibilityOff 
                                        else 
                                            Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = TextTertiary
                                    )
                                }
                                if (apiKeyInput.isNotEmpty()) {
                                    IconButton(onClick = { apiKeyInput = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = null,
                                            tint = TextTertiary
                                        )
                                    }
                                }
                            }
                        },
                        visualTransformation = if (showApiKey) 
                            VisualTransformation.None 
                        else 
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        shape = InputShape,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = BackgroundInput,
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Border,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )
                    
                    Text(
                        text = stringResource(R.string.api_key_helper),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                    
                    // Save Button
                    if (apiKeyInput != apiKey) {
                        SaveButton(
                            onClick = {
                                viewModel.setApiKey(apiKeyInput)
                                Toast.makeText(
                                    context,
                                    "API key saved",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
            
            // Generation Settings
            SettingsSection(title = "Generation Settings") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Enhance Prompts Toggle
                    SettingsToggleItem(
                        icon = Icons.Default.AutoFixHigh,
                        title = "Enhance Prompts",
                        description = "Automatically improve your prompts for better results",
                        checked = enhancePrompts,
                        onCheckedChange = {
                            // This would need to be added to the ViewModel
                        }
                    )
                    
                    Divider(color = Border)
                    
                    // HD Quality Toggle
                    SettingsToggleItem(
                        icon = Icons.Default.HighQuality,
                        title = "HD Quality",
                        description = "Generate higher quality images (slower)",
                        checked = true,
                        onCheckedChange = {}
                    )
                    
                    Divider(color = Border)
                    
                    // Auto Save Toggle
                    SettingsToggleItem(
                        icon = Icons.Default.Save,
                        title = "Auto Save to Gallery",
                        description = "Automatically save generated images",
                        checked = true,
                        onCheckedChange = {}
                    )
                }
            }
            
            // About Section
            SettingsSection(title = "About") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // App Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Version",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = stringResource(R.string.version, "1.0.0"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                    
                    // Clear Cache Button
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                R.string.cache_cleared,
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = ButtonShape,
                        border = BorderStroke(1.dp, Border),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextSecondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.clear_cache),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(Surface)
                .border(1.dp, Border, CardShape)
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Primary,
                checkedTrackColor = Primary.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun SaveButton(onClick: () -> Unit) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(GradientStart, GradientEnd)
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(4.dp, ButtonShape, spotColor = Primary)
            .clip(ButtonShape)
            .background(gradientBrush)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.save_button),
            style = MaterialTheme.typography.labelLarge,
            color = TextOnPrimary
        )
    }
}
