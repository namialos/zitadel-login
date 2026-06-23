package org.example.zitadellogin.presentation.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.zitadellogin.core.navigation.Routes
import org.example.zitadellogin.core.logging.AppLogger
import org.example.zitadellogin.core.oauth.OAuthExternalDeepLinks
import org.example.zitadellogin.core.oauth.OAuthNavigationBus
import org.example.zitadellogin.core.oauth.OAuthPendingCallback
import org.example.zitadellogin.presentation.callback.AuthCallbackRoute
import org.example.zitadellogin.presentation.callback.AuthCallbackViewModel
import org.example.zitadellogin.presentation.home.HomeRoute
import org.example.zitadellogin.presentation.home.HomeViewModel
import org.example.zitadellogin.presentation.keyclocklogin.LoginWithKeycloakRoute
import org.example.zitadellogin.presentation.login.LoginRoute
import org.example.zitadellogin.presentation.login.LoginViewModel
import org.example.zitadellogin.presentation.splash.SplashRoute
import org.example.zitadellogin.presentation.splash.SplashViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ZitadelNavHost(
    navController: NavHostController = rememberNavController(),
) {
    val deepLinks: OAuthExternalDeepLinks = koinInject()
    val navigationBus: OAuthNavigationBus = koinInject()

    LaunchedEffect(deepLinks, navigationBus) {
        deepLinks.events.collect { url ->
            AppLogger.i("ZitadelNavHost received deep link event")
            AppLogger.dump("ZitadelNavHost deep link", url)
            navigationBus.deliverRedirect(url)
        }
    }

    LaunchedEffect(navigationBus) {
        navigationBus.callback.collect { payload ->
            AppLogger.dump(
                "ZitadelNavHost navigating to AuthCallback",
                "code=${payload.code}\nstate=${payload.state}",
            )
            OAuthPendingCallback.set(payload)
            navController.navigate(Routes.AuthCallback) {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Splash,
    ) {
        composable(Routes.Splash) {
            val vm: SplashViewModel = koinViewModel()
            SplashRoute(
                viewModel = vm,
                onNavigateLogin = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.Login) {
            val vm: LoginViewModel = koinViewModel()
            LoginRoute(viewModel = vm, onLoginWithKeyClock = {
                navController.navigate(Routes.KeyClockLogin)
            })

        }
        composable(Routes.AuthCallback) {
            val vm: AuthCallbackViewModel = koinViewModel()
            AuthCallbackRoute(
                viewModel = vm,
                onSuccess = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.AuthCallback) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.Home) {
            val vm: HomeViewModel = koinViewModel()
            HomeRoute(
                viewModel = vm,
                onLoggedOut = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.KeyClockLogin) {
            LoginWithKeycloakRoute(onBackPressed = {
                navController.popBackStack()
            })
        }
    }
}

@Composable
fun RtlApplication(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
    ) {
        content()
    }
}
