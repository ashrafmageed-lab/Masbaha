package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.PrayerScreen
import com.example.ui.RosaryViewModel
import com.example.ui.theme.RosaryTheme

class MainActivity : ComponentActivity() {

    private val rosaryViewModel: RosaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by rosaryViewModel.uiState.collectAsState()
            val isDark = uiState.isDarkThemeOverride ?: isSystemInDarkTheme()

            RosaryTheme(darkTheme = isDark) {
                PrayerScreen(viewModel = rosaryViewModel)
            }
        }
    }
}
