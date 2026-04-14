package com.aiimagestudio.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aiimagestudio.AIImageStudioApp
import com.aiimagestudio.api.ImageGenerationService
import com.aiimagestudio.ui.screens.*
import com.aiimagestudio.ui.theme.AIImageStudioTheme
import com.aiimagestudio.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val app = application as AIImageStudioApp
        MainViewModel.Factory(
            app.imageRepository,
            app.settingsRepository,
            ImageGenerationService(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var isReady by mutableStateOf(false)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                isReady = true
            }
        }

        splashScreen.setKeepOnScreenCondition { !isReady }

        setContent {
            AIImageStudioTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainApp(viewModel: MainViewModel) {
    val context = LocalContext.current
    val showOnboarding by viewModel.showOnboarding.collectAsState()
    val generationState by viewModel.generationState.collectAsState()
    
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var showOnboardingScreen by remember { mutableStateOf(showOnboarding) }

    // Handle generation state changes
    LaunchedEffect(generationState) {
        when (generationState) {
            is GenerationResult.Loading -> {
                currentScreen = Screen.Loading
            }
            is GenerationResult.Success -> {
                currentScreen = Screen.Result
            }
            is GenerationResult.Error -> {
                // Stay on home screen, error will be shown
            }
            null -> {
                // No action needed
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AnimatedContent(
            targetState = when {
                showOnboardingScreen -> Screen.Onboarding
                else -> currentScreen
            },
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                fadeOut(animationSpec = tween(300))
            },
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                Screen.Onboarding -> {
                    OnboardingScreen(
                        onGetStarted = {
                            viewModel.setShowOnboarding(false)
                            showOnboardingScreen = false
                        },
                        onSkip = {
                            viewModel.setShowOnboarding(false)
                            showOnboardingScreen = false
                        }
                    )
                }
                Screen.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToSettings = {
                            currentScreen = Screen.Settings
                        }
                    )
                }
                Screen.Loading -> {
                    LoadingScreen(
                        viewModel = viewModel
                    )
                }
                Screen.Result -> {
                    val result = generationState as? com.aiimagestudio.data.model.GenerationResult.Success
                    result?.let {
                        ResultScreen(
                            result = it,
                            onBack = {
                                viewModel.resetGenerationState()
                                currentScreen = Screen.Home
                            },
                            onRegenerate = {
                                viewModel.generateImage()
                            },
                            onNewImage = {
                                viewModel.resetGenerationState()
                                viewModel.clearPrompt()
                                currentScreen = Screen.Home
                            }
                        )
                    }
                }
                Screen.Settings -> {
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = {
                            currentScreen = Screen.Home
                        }
                    )
                }
            }
        }
    }
}

sealed class Screen {
    object Onboarding : Screen()
    object Home : Screen()
    object Loading : Screen()
    object Result : Screen()
    object Settings : Screen()
}
