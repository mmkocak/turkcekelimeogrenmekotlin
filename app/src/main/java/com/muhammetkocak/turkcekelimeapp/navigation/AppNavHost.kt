package com.muhammetkocak.turkcekelimeapp.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Screen,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(tween(260)) { it / 6 } + fadeIn(tween(220))
        },
        exitTransition = {
            fadeOut(tween(140))
        },
        popEnterTransition = {
            slideInHorizontally(tween(220)) { -it / 6 } + fadeIn(tween(200))
        },
        popExitTransition = {
            slideOutHorizontally(tween(200)) { it / 4 } + fadeOut(tween(160))
        }
    ) {
        onboardingRoute(navController)
        homeRoute(navController)
        libraryRoute(navController)
        addEditWordRoute(navController)
        wordDetailRoute(navController)
        studyRoute(navController)
        statsRoute(navController)
        settingsRoute(navController)
    }
}

// Phase 1 placeholder routes — concrete screens land in subsequent phases.

private fun NavGraphBuilder.onboardingRoute(nav: NavHostController) {
    composable<Screen.Onboarding> { PlaceholderScreen(title = "Onboarding") }
}

private fun NavGraphBuilder.homeRoute(nav: NavHostController) {
    composable<Screen.Home> { PlaceholderScreen(title = "Ana Ekran") }
}

private fun NavGraphBuilder.libraryRoute(nav: NavHostController) {
    composable<Screen.Library> { PlaceholderScreen(title = "Kütüphane") }
}

private fun NavGraphBuilder.addEditWordRoute(nav: NavHostController) {
    composable<Screen.AddEditWord> { PlaceholderScreen(title = "Kelime Ekle / Düzenle") }
}

private fun NavGraphBuilder.wordDetailRoute(nav: NavHostController) {
    composable<Screen.WordDetail> { PlaceholderScreen(title = "Kelime Detayı") }
}

private fun NavGraphBuilder.studyRoute(nav: NavHostController) {
    composable<Screen.Study> { PlaceholderScreen(title = "Çalışma") }
}

private fun NavGraphBuilder.statsRoute(nav: NavHostController) {
    composable<Screen.Stats> { PlaceholderScreen(title = "İstatistikler") }
}

private fun NavGraphBuilder.settingsRoute(nav: NavHostController) {
    composable<Screen.Settings> { PlaceholderScreen(title = "Ayarlar") }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title)
    }
}

@Composable
internal fun LoadingRoot() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
