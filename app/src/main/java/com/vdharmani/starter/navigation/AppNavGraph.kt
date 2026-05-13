package com.vdharmani.starter.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vdharmani.starter.feature.auth.presentation.changepassword.ChangePasswordScreen
import com.vdharmani.starter.feature.auth.presentation.deleteaccount.DeleteAccountScreen
import com.vdharmani.starter.feature.auth.presentation.forgot.ForgotPasswordScreen
import com.vdharmani.starter.feature.auth.presentation.login.LoginScreen
import com.vdharmani.starter.feature.auth.presentation.signup.SignupScreen
import com.vdharmani.starter.feature.legal.PrivacyScreen
import com.vdharmani.starter.feature.legal.TermsScreen
import com.vdharmani.starter.feature.premium.presentation.PremiumScreen
import com.vdharmani.starter.feature.profile.presentation.profile.ProfileScreen

/**
 * App-level navigation. Every screen sits behind a `composable("route")`.
 * Cross-screen navigation goes through the `on…` lambdas you pass into the
 * Screen — the Screen never imports `NavController` directly.
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
                onAuthed = { navController.toHomeClearing() },
                onGoSignup = { navController.navigate(Routes.Signup) },
                onGoForgot = { navController.navigate(Routes.Forgot) },
            )
        }

        composable(Routes.Signup) {
            SignupScreen(
                onAuthed = { navController.toHomeClearing() },
                onBackToLogin = { navController.popBackStack() },
            )
        }

        composable(Routes.Forgot) {
            ForgotPasswordScreen(onBackToLogin = { navController.popBackStack() })
        }

        composable(Routes.Home) {
            HomeScreen(
                onProfile = { navController.navigate(Routes.Profile) },
                onPremium = { navController.navigate(Routes.Premium) },
                onTerms = { navController.navigate(Routes.Terms) },
                onPrivacy = { navController.navigate(Routes.Privacy) },
            )
        }

        composable(Routes.Profile) {
            ProfileScreen(
                onLoggedOut = { navController.toLoginClearing() },
                onChangePassword = { navController.navigate(Routes.ChangePassword) },
                onDeleteAccount = { navController.navigate(Routes.DeleteAccount) },
            )
        }

        composable(Routes.ChangePassword) {
            ChangePasswordScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.DeleteAccount) {
            DeleteAccountScreen(
                onBack = { navController.popBackStack() },
                onAccountDeleted = { navController.toLoginClearing() },
            )
        }

        composable(Routes.Premium) {
            PremiumScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.Terms) {
            TermsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.Privacy) {
            PrivacyScreen(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
private fun HomeScreen(
    onProfile: () -> Unit,
    onPremium: () -> Unit,
    onTerms: () -> Unit,
    onPrivacy: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("You're signed in 🎉", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onProfile, modifier = Modifier.fillMaxWidth()) { Text("Profile") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onPremium, modifier = Modifier.fillMaxWidth()) { Text("Premium") }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onTerms, modifier = Modifier.fillMaxWidth()) { Text("Terms") }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onPrivacy, modifier = Modifier.fillMaxWidth()) { Text("Privacy") }
    }
}

// -- nav helpers ------------------------------------------------------------

private fun NavController.toHomeClearing() {
    navigate(Routes.Home) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavController.toLoginClearing() {
    navigate(Routes.Login) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

private object Routes {
    const val Login = "login"
    const val Signup = "signup"
    const val Forgot = "forgot"
    const val Home = "home"
    const val Profile = "profile"
    const val ChangePassword = "change-password"
    const val DeleteAccount = "delete-account"
    const val Premium = "premium"
    const val Terms = "terms"
    const val Privacy = "privacy"
}
