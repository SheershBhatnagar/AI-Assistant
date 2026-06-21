/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Created On: 01/05/26 19:09
*/

package dev.sheershbhatnagar.ai_assistant.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

import dev.sheershbhatnagar.ai_assistant.R

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        NavigationBarItem(
            selected = currentRoute == "home_screen",
            onClick = {
                navController.navigate("home_screen") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null
                )
            },
            label = { Text(text = "Home") }
        )

        NavigationBarItem(
            selected = currentRoute == "history_screen",
            onClick = {
                navController.navigate("history_screen") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    Icons.Default.History,
                    contentDescription = null
                )
            },
            label = { Text(text = "History") }
        )

        NavigationBarItem(
            selected = currentRoute == "profile_screen",
            onClick = {
                navController.navigate("profile_screen") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Image(
                    painter = painterResource(R.drawable.ic_avatar_17),
                    contentDescription = null
                )
            }
        )
        
    }
}
