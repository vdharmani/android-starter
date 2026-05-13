package com.vdharmani.starter.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vdharmani.starter.feature.auth.presentation.forgot.ForgotPasswordScreen
import com.vdharmani.starter.feature.auth.presentation.login.LoginScreen

/**
 * App-level navigation. Phase 1 only wires the login destination; subsequent
 * phases add signup, forgot password, home, profile, premium, legal, etc.
 *
 * Junior pattern: every screen sits behind a `composable("route")` here.
 * Cross-screen navigation goes through the `on…` lambdas you pass into the
 * Screen — the Screen never imports NavController.
 */
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Login,
    ) {
        composable(Routes.Login) {
            LoginScreen(
                onAuthed = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onGoSignup = { /* navController.navigate(Routes.Signup) — Phase 2 */ },
                onGoForgot = { navController.navigate(Routes.Forgot) },
            )
        }

        composable(Routes.Forgot) {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack() },
            )
        }

        composable(Routes.Home) {
            // Placeholder home screen — replace with your real home.
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("You're signed in 🎉")
            }
        }
    }
}

private object Routes {
    const val Login = "login"
    const val Forgot = "forgot"
    const val Home = "home"
}
