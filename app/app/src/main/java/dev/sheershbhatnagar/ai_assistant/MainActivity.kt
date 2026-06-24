package dev.sheershbhatnagar.ai_assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.sheershbhatnagar.ai_assistant.core.network.SessionManager
import dev.sheershbhatnagar.ai_assistant.features.main.MainScreen
import dev.sheershbhatnagar.ai_assistant.ui.theme.AIAssistantTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(applicationContext)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by sessionManager.isDarkTheme.collectAsState(initial = true)
            AIAssistantTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}
