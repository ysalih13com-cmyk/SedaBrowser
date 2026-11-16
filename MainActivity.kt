package com.seda.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.seda.browser.ui.browser.BrowserScreen
import com.seda.browser.ui.home.HomeScreen
import com.seda.browser.ui.theme.SedaBrowserTheme
import com.seda.browser.viewmodel.BrowserViewModel

private sealed class AppScreen {
    data object Home : AppScreen()
    data class Browser(val url: String) : AppScreen()
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SedaBrowserTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SedaBrowserApp()
                }
            }
        }
    }
}

@Composable
private fun SedaBrowserApp() {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

    when (val screen = currentScreen) {
        is AppScreen.Home -> {
            HomeScreen(
                onNavigateToBrowser = { url ->
                    currentScreen = AppScreen.Browser(url)
                }
            )
        }
        is AppScreen.Browser -> {
            // Basit ve stabil mimari: aynı anda tek WebView yaşar.
            val browserViewModel = remember(screen.url) { BrowserViewModel(screen.url) }
            BrowserScreen(
                viewModel = browserViewModel,
                onNavigateHome = { currentScreen = AppScreen.Home }
            )
        }
    }
}
