# AI Image Studio

A beautiful, modern Android application for generating AI-powered images from text prompts using the Hugging Face Inference API.

## Features

- **Text-to-Image Generation**: Convert your ideas into stunning AI-generated artwork
- **11 Art Styles**: Realistic, Cinematic, Anime, 3D Render, Digital Art, Oil Painting, Watercolor, Sketch, Fantasy, Minimalist, Abstract
- **5 Aspect Ratios**: 1:1, 2:3, 4:5, 16:9, 21:9
- **High Quality Output**: HD and 4K-style image generation
- **Smart Prompt Enhancement**: Automatically improves your prompts for better results
- **Save & Share**: Download images to gallery or share directly
- **Beautiful UI**: Modern, clean interface with smooth animations
- **Offline Caching**: Recently generated images are cached for quick access

## Screenshots

The app features:
- Clean white minimal interface
- Modern iOS-like design
- Soft shadows and rounded corners
- Smooth animations and transitions
- Gradient accent colors with pastel tones

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with ViewModel and StateFlow
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Database**: Room (for caching)
- **Preferences**: DataStore
- **Animation**: Compose Animation + Lottie

## Project Structure

```
app/src/main/java/com/aiimagestudio/
├── AIImageStudioApp.kt          # Application class
├── data/
│   ├── local/                   # Room database
│   │   ├── AppDatabase.kt
│   │   └── GeneratedImageDao.kt
│   ├── model/                   # Data models
│   │   ├── AspectRatio.kt
│   │   ├── GeneratedImage.kt
│   │   ├── GenerationResult.kt
│   │   └── ImageStyle.kt
│   └── repository/              # Repositories
│       ├── ImageRepository.kt
│       └── SettingsRepository.kt
├── api/                         # API integration
│   ├── HuggingFaceApiService.kt
│   ├── ImageGenerationService.kt
│   └── RetrofitClient.kt
├── ui/
│   ├── activities/              # Activities
│   │   └── MainActivity.kt
│   ├── screens/                 # Compose screens
│   │   ├── HomeScreen.kt
│   │   ├── LoadingScreen.kt
│   │   ├── OnboardingScreen.kt
│   │   ├── ResultScreen.kt
│   │   └── SettingsScreen.kt
│   └── theme/                   # Theme configuration
│       ├── Color.kt
│       ├── Shape.kt
│       ├── Theme.kt
│       └── Type.kt
├── utils/                       # Utilities
│   ├── ImageUtils.kt
│   └── PermissionUtils.kt
└── viewmodel/                   # ViewModels
    └── MainViewModel.kt
```

## Setup Instructions

### 1. Get a Hugging Face API Key

1. Visit [huggingface.co](https://huggingface.co)
2. Create a free account
3. Go to Settings → Access Tokens
4. Generate a new token
5. Copy the token for use in the app

### 2. Build the Project

```bash
# Clone or download the project
cd AIImageStudio

# Build the project
./gradlew build

# Or open in Android Studio and sync
```

### 3. Run the App

```bash
# Connect your Android device or start an emulator
./gradlew installDebug
```

## Configuration

### API Key Setup

1. Open the app
2. Go to Settings (gear icon on Home screen)
3. Enter your Hugging Face API key
4. Tap "Save"

### Optional Settings

- **Enhance Prompts**: Automatically improves your prompts
- **HD Quality**: Generate higher quality images (slower)
- **Auto Save**: Automatically save images to gallery

## Usage

1. **Enter a Prompt**: Describe the image you want to create
2. **Select a Style**: Choose from 11 different art styles
3. **Choose Aspect Ratio**: Pick the image dimensions
4. **Tap Generate**: Wait for the AI to create your image
5. **Save or Share**: Download to gallery or share with friends

## Permissions

The app requires the following permissions:

- **Internet**: For API calls to Hugging Face
- **Storage**: For saving images to gallery (Android 12 and below)
- **Media Images**: For saving images (Android 13+)

## API Model

This app uses the **Stable Diffusion XL Base 1.0** model from Stability AI via Hugging Face Inference API.

Model: `stabilityai/stable-diffusion-xl-base-1.0`

## Troubleshooting

### "Invalid API Key" Error
- Make sure you've entered a valid Hugging Face API token
- Check that the token has not expired

### "Rate Limit" Error
- Free tier has rate limits
- Wait a moment before trying again
- Consider upgrading your Hugging Face plan

### "Network Error"
- Check your internet connection
- Try again with a stable connection

### Images Not Saving
- Grant storage permissions when prompted
- Check app permissions in device settings

## Customization

### Adding New Styles

Edit `ImageStyle.kt` to add new art styles:

```kotlin
enum class ImageStyle(
    @StringRes val displayName: Int,
    @DrawableRes val icon: Int,
    val color: Color,
    val promptModifier: String
) {
    // Add your new style here
    NEW_STYLE(
        R.string.style_new,
        R.drawable.ic_style_new,
        NewStyleColor,
        "your style modifiers here"
    )
}
```

### Changing Default Settings

Edit `SettingsRepository.kt` to modify default values.

## Building for Production

```bash
# Generate release APK
./gradlew assembleRelease

# Or generate App Bundle for Play Store
./gradlew bundleRelease
```

## License

```
Copyright 2024 AI Image Studio

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Credits

- AI Model: [Stability AI](https://stability.ai/)
- API: [Hugging Face](https://huggingface.co/)
- Icons: [Material Design Icons](https://fonts.google.com/icons)

## Support

For issues or feature requests, please create an issue in the project repository.

---

**Enjoy creating AI art with AI Image Studio!**
