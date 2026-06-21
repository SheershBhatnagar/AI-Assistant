/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Created On: 02/05/26 00:22
*/

package dev.sheershbhatnagar.ai_assistant.features.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import dev.sheershbhatnagar.ai_assistant.core.navigation.AppNavigation
import dev.sheershbhatnagar.ai_assistant.ui.components.BottomNavBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Only show bottom navigation on main tabs
    val showBottomBar = currentRoute in listOf("home_screen", "history_screen", "profile_screen")
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
