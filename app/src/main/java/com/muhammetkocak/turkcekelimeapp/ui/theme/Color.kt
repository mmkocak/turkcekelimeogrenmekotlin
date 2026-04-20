package com.muhammetkocak.turkcekelimeapp.ui.theme

import androidx.compose.ui.graphics.Color

// Brand palette — modern indigo + warm amber + soft plum, inspired by Duolingo/Quizlet.
// Light scheme uses a warm off-white background with deep indigo primary.
// Dark scheme flips to near-black with brighter indigo accents.

// Light scheme
val md_theme_light_primary = Color(0xFF4F46E5)          // Indigo 600
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFE0E7FF) // Indigo 100
val md_theme_light_onPrimaryContainer = Color(0xFF1E1B4B)
val md_theme_light_secondary = Color(0xFFD97706)        // Amber 600
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFEF3C7)
val md_theme_light_onSecondaryContainer = Color(0xFF451A03)
val md_theme_light_tertiary = Color(0xFFDB2777)         // Pink 600
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFCE7F3)
val md_theme_light_onTertiaryContainer = Color(0xFF500724)
val md_theme_light_error = Color(0xFFDC2626)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFEE2E2)
val md_theme_light_onErrorContainer = Color(0xFF450A0A)
val md_theme_light_background = Color(0xFFFAFAF9)       // Stone 50
val md_theme_light_onBackground = Color(0xFF1C1917)
val md_theme_light_surface = Color(0xFFFFFFFF)
val md_theme_light_onSurface = Color(0xFF1C1917)
val md_theme_light_surfaceVariant = Color(0xFFF5F5F4)   // Stone 100
val md_theme_light_onSurfaceVariant = Color(0xFF57534E)
val md_theme_light_outline = Color(0xFFA8A29E)
val md_theme_light_outlineVariant = Color(0xFFE7E5E4)
val md_theme_light_inverseSurface = Color(0xFF292524)
val md_theme_light_inverseOnSurface = Color(0xFFF5F5F4)
val md_theme_light_inversePrimary = Color(0xFFA5B4FC)
val md_theme_light_scrim = Color(0xFF000000)
val md_theme_light_surfaceContainer = Color(0xFFF5F5F4)
val md_theme_light_surfaceContainerHigh = Color(0xFFEFEEED)
val md_theme_light_surfaceContainerHighest = Color(0xFFE7E5E4)

// Dark scheme
val md_theme_dark_primary = Color(0xFFA5B4FC)           // Indigo 300
val md_theme_dark_onPrimary = Color(0xFF1E1B4B)
val md_theme_dark_primaryContainer = Color(0xFF3730A3)
val md_theme_dark_onPrimaryContainer = Color(0xFFE0E7FF)
val md_theme_dark_secondary = Color(0xFFFCD34D)         // Amber 300
val md_theme_dark_onSecondary = Color(0xFF451A03)
val md_theme_dark_secondaryContainer = Color(0xFF92400E)
val md_theme_dark_onSecondaryContainer = Color(0xFFFEF3C7)
val md_theme_dark_tertiary = Color(0xFFF9A8D4)          // Pink 300
val md_theme_dark_onTertiary = Color(0xFF500724)
val md_theme_dark_tertiaryContainer = Color(0xFF9D174D)
val md_theme_dark_onTertiaryContainer = Color(0xFFFCE7F3)
val md_theme_dark_error = Color(0xFFF87171)
val md_theme_dark_onError = Color(0xFF450A0A)
val md_theme_dark_errorContainer = Color(0xFF7F1D1D)
val md_theme_dark_onErrorContainer = Color(0xFFFECACA)
val md_theme_dark_background = Color(0xFF0C0A09)        // Stone 950
val md_theme_dark_onBackground = Color(0xFFF5F5F4)
val md_theme_dark_surface = Color(0xFF1C1917)
val md_theme_dark_onSurface = Color(0xFFF5F5F4)
val md_theme_dark_surfaceVariant = Color(0xFF292524)
val md_theme_dark_onSurfaceVariant = Color(0xFFD6D3D1)
val md_theme_dark_outline = Color(0xFF78716C)
val md_theme_dark_outlineVariant = Color(0xFF44403C)
val md_theme_dark_inverseSurface = Color(0xFFF5F5F4)
val md_theme_dark_inverseOnSurface = Color(0xFF1C1917)
val md_theme_dark_inversePrimary = Color(0xFF4F46E5)
val md_theme_dark_scrim = Color(0xFF000000)
val md_theme_dark_surfaceContainer = Color(0xFF1C1917)
val md_theme_dark_surfaceContainerHigh = Color(0xFF292524)
val md_theme_dark_surfaceContainerHighest = Color(0xFF44403C)

// Mastery bands (consistent across themes)
val MasteryNew = Color(0xFF94A3B8)       // Slate 400
val MasteryLearning = Color(0xFFF59E0B)  // Amber 500
val MasteryReview = Color(0xFF6366F1)    // Indigo 500
val MasteryMastered = Color(0xFF10B981)  // Emerald 500

// Answer feedback
val CorrectGreen = Color(0xFF16A34A)
val WrongRed = Color(0xFFDC2626)

// Category accent palette — saturated but tasteful; cycles by index.
val CategoryPalette = listOf(
    Color(0xFF4F46E5), // indigo
    Color(0xFFF59E0B), // amber
    Color(0xFF10B981), // emerald
    Color(0xFFEC4899), // pink
    Color(0xFF06B6D4), // cyan
    Color(0xFF8B5CF6), // violet
    Color(0xFFEF4444), // red
    Color(0xFF64748B)  // slate
)

// Gradient endpoints for hero surfaces.
val HeroGradientStart = Color(0xFF4F46E5)
val HeroGradientEnd = Color(0xFF7C3AED)
val HeroGradientStartDark = Color(0xFF6366F1)
val HeroGradientEndDark = Color(0xFF8B5CF6)
