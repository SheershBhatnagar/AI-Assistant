package dev.sheershbhatnagar.ai_assistant.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.sheershbhatnagar.ai_assistant.core.network.SessionManager
import dev.sheershbhatnagar.ai_assistant.features.auth.EmailScreen
import dev.sheershbhatnagar.ai_assistant.features.auth.OtpScreen
import dev.sheershbhatnagar.ai_assistant.features.auth.AuthViewModel
import dev.sheershbhatnagar.ai_assistant.features.chat.ChatScreen
import dev.sheershbhatnagar.ai_assistant.features.chat.ChatViewModel
import dev.sheershbhatnagar.ai_assistant.features.history.HistoryScreen
import dev.sheershbhatnagar.ai_assistant.features.history.HistoryViewModel
import dev.sheershbhatnagar.ai_assistant.features.home.HomeScreen
import dev.sheershbhatnagar.ai_assistant.features.home.HomeViewModel
import dev.sheershbhatnagar.ai_assistant.features.profile.ProfileScreen
import dev.sheershbhatnagar.ai_assistant.features.profile.ProfileViewModel
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    NavHost(
        navController = navController,
        startDestination = "splash_screen",
        modifier = modifier
    ) {
        composable("splash_screen") {
            LaunchedEffect(Unit) {
                val token = sessionManager.jwtToken.firstOrNull()
                if (!token.isNullOrBlank()) {
                    navController.navigate("home_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                } else {
                    navController.navigate("email_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                }
            }
        }

        composable("email_screen") {
            val authViewModel: AuthViewModel = viewModel()
            EmailScreen(
                viewModel = authViewModel,
                onNavigateToOtp = {
                    navController.navigate("otp_screen")
                }
            )
        }

        composable("otp_screen") {
            val authViewModel: AuthViewModel = viewModel()
            OtpScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate("home_screen") {
                        popUpTo("email_screen") { inclusive = true }
                    }
                }
            )
        }

        composable("home_screen") {
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToChat = { conversationId ->
                    navController.navigate("chat_screen/$conversationId")
                }
            )
        }

        composable("history_screen") {
            val historyViewModel: HistoryViewModel = viewModel()
            HistoryScreen(
                viewModel = historyViewModel,
                onNavigateToChat = { conversationId ->
                    navController.navigate("chat_screen/$conversationId")
                }
            )
        }

        composable("profile_screen") {
            val profileViewModel: ProfileViewModel = viewModel()
            ProfileScreen(
                viewModel = profileViewModel,
                onLogout = {
                    navController.navigate("email_screen") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "chat_screen/{conversationId}",
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            val chatViewModel: ChatViewModel = viewModel()
            ChatScreen(
                conversationId = conversationId,
                viewModel = chatViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
