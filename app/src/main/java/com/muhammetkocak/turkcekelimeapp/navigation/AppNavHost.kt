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
import androidx.navigation.toRoute
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.domain.model.StudyMode
import com.muhammetkocak.turkcekelimeapp.ui.addword.AddEditWordScreen
import com.muhammetkocak.turkcekelimeapp.ui.home.HomeScreen
import com.muhammetkocak.turkcekelimeapp.ui.library.LibraryScreen

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

private fun NavGraphBuilder.onboardingRoute(nav: NavHostController) {
    composable<Screen.Onboarding> {
        PlaceholderScreen(title = "Onboarding")
    }
}

private fun NavGraphBuilder.homeRoute(nav: NavHostController) {
    composable<Screen.Home> {
        HomeScreen(
            onOpenLibrary = { catId -> nav.navigate(Screen.Library(catId)) },
            onOpenAddWord = { nav.navigate(Screen.AddEditWord()) },
            onOpenSettings = { nav.navigate(Screen.Settings) },
            onOpenStats = { nav.navigate(Screen.Stats) },
            onStartStudy = { mode, direction, categoryId ->
                nav.navigate(Screen.Study(mode = mode.raw, direction = direction.raw, categoryId = categoryId))
            }
        )
    }
}

private fun NavGraphBuilder.libraryRoute(nav: NavHostController) {
    composable<Screen.Library> { entry ->
        val route = entry.toRoute<Screen.Library>()
        LibraryScreen(
            initialCategoryId = route.categoryId,
            onBack = { nav.popBackStack() },
            onAddWord = { nav.navigate(Screen.AddEditWord()) },
            onEditWord = { id -> nav.navigate(Screen.AddEditWord(wordId = id)) },
            onStudyWord = {
                nav.navigate(
                    Screen.Study(
                        mode = StudyMode.Flashcard.raw,
                        direction = LearningDirection.ForeignToTurkish.raw,
                        categoryId = null
                    )
                )
            }
        )
    }
}

private fun NavGraphBuilder.addEditWordRoute(nav: NavHostController) {
    composable<Screen.AddEditWord> {
        AddEditWordScreen(
            onBack = { nav.popBackStack() },
            onSaved = { nav.popBackStack() }
        )
    }
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
