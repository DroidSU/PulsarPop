# Pulsar Pop ⚡

**Pulsar Pop** is an Android puzzle game built from the ground up using Jetpack Compose.

---

## 🌟 Features & Gameplay

1. **Procedural Endless Levels**:
   - Automatically generates scalable $N \times M$ tile grids with increasing color variety and target clear counts as levels progress.
2. **Cosmic Grid Mechanics**:
   - Tap connected clusters of matching energy tiles (orthogonally connected) to trigger explosive pops.
   - Coroutine-driven gravity cascades where upper stellar tiles drop smoothly into cleared slots.
   - Intelligent board check preventing deadlocks with automatic grid reshuffling.
3. **Immersive UI/UX**:
   - Material Design 3 styling with dynamic color support and bespoke cosmic color palettes.
   - Animated pulsing title logos, rounded card containers, and subtle haptic feedback on tile interactions.
   - Comprehensive "How to Play" guidance and pause/restart flows.

---

## 🏛️ Architecture & Technical Stack

- **Architecture**: Multi-Activity setup (`MenuActivity` for the home screen & high score hub, and `GameActivity` for the puzzle gameplay loop) adhering to strict Model-View-ViewModel (MVVM) separation of concerns.
- **Dependency Injection**: [Dagger Hilt](https://dagger.dev/hilt/) with [KSP (Kotlin Symbol Processing)](https://kotlinlang.org/docs/ksp.html) across all Activities and ViewModels.
- **Concurrency & State**: Immutable `StateFlow<GameUiState>` exposed by ViewModels powered by `viewModelScope` for non-blocking background calculations (flood-fill, gravity simulation, and persistence).
- **Persistence**: Jetpack DataStore Preferences for storing high scores and user preferences.
- **UI Toolkit**: 100% Jetpack Compose with Material 3 components and declarative UI state hoisting.

---

## 📁 Project Structure

```
com.morningstar.pulsarpop/
├── PulsarPopApplication.kt       # @HiltAndroidApp application class
├── data/
│   ├── GameRepository.kt         # Game logic helper & state definitions
│   └── PreferencesRepository.kt  # DataStore preferences (high scores)
├── di/
│   └── AppModule.kt              # Hilt dependency injection module
└── ui/
    ├── theme/                    # Material 3 Theme, Color, and Typography
    ├── menu/                     # Home screen Activity, ViewModel, and Screen
    └── game/                     # Gameplay Activity, ViewModel, State, and Screen
```

---

## 🚀 Getting Started

1. Clone the repository in [Android Studio](https://developer.android.com/studio).
2. Ensure you have JDK 17+ and the Android SDK configured.
3. Sync the project with Gradle.
4. Run the `app` configuration on an emulator or physical device (minimum SDK 24, target/compile SDK 37).
