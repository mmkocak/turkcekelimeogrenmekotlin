package com.muhammetkocak.turkcekelimeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.muhammetkocak.turkcekelimeapp.data.prefs.UserPreferences
import com.muhammetkocak.turkcekelimeapp.navigation.AppNavHost
import com.muhammetkocak.turkcekelimeapp.navigation.Screen
import com.muhammetkocak.turkcekelimeapp.ui.root.AppRootViewModel
import com.muhammetkocak.turkcekelimeapp.ui.theme.TurkceKelimeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppRoot() }
    }
}

@Composable
private fun AppRoot(viewModel: AppRootViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val prefs = state.preferences ?: UserPreferences.Default
    TurkceKelimeTheme(
        themeMode = prefs.themeMode,
        dynamicColorEnabled = prefs.dynamicColorEnabled
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (state.preferences == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val navController = rememberNavController()
                val start: Screen = if (prefs.firstRunCompleted) Screen.Home else Screen.Onboarding
                AppNavHost(navController = navController, startDestination = start)
            }
        }
    }
}
