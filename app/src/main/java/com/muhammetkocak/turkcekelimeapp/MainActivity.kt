package com.muhammetkocak.turkcekelimeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.muhammetkocak.turkcekelimeapp.navigation.AppNavHost
import com.muhammetkocak.turkcekelimeapp.navigation.Screen
import com.muhammetkocak.turkcekelimeapp.ui.theme.ThemeMode
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
private fun AppRoot() {
    TurkceKelimeTheme(themeMode = ThemeMode.System, dynamicColorEnabled = true) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            AppNavHost(navController = navController, startDestination = Screen.Home)
        }
    }
}
