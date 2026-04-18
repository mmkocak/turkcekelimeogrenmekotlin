# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Turkish word learning Android app ("Türkçe Kelime Öğrenme"). Currently at initial scaffold stage using Jetpack Compose.

- **Package**: `com.muhammetkocak.turkcekelimeapp`
- **Min SDK**: 24, **Target/Compile SDK**: 35
- **Language**: Kotlin (JVM target 11)
- **UI**: Jetpack Compose with Material 3, dynamic color support (Android 12+)

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew testDebugUnitTest      # Run unit tests for debug variant only
```

## Architecture

Single-module Gradle project (`app`). Uses Gradle version catalog (`gradle/libs.versions.toml`) for dependency management with Compose BOM for coordinated Compose library versions.

Entry point: `MainActivity` → `TurkcekelimeappTheme` → Compose UI.

Source layout follows standard Android conventions under `app/src/main/java/com/muhammetkocak/turkcekelimeapp/`.
