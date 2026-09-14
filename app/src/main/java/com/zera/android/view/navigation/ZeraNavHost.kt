package com.zera.android.view.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zera.android.view.screens.auth.SignInScreen
import com.zera.android.view.screens.auth.SignUpScreen
import com.zera.android.view.screens.SplashScreen
import com.zera.android.view.screens.auth.WelcomeScreen
import com.zera.android.view.screens.employee.EmployeeHome
import com.zera.android.view.screens.manager.ManagerHome
import com.zera.android.view.transition.LocalAnimatedVisibilityScope
import com.zera.android.view.transition.LocalSharedTransitionScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ZeraNavHost(){
    val navController : NavHostController = rememberNavController()

    LaunchedEffect(Unit) {
        ZeraNavigator.commands.collect { command ->
            when (command) {
                is NavCommand.Navigate -> navController.navigate(command.route)

                is NavCommand.PushAndPop -> {
                    // Captura a tela atual antes de navegar, pois após o navigate()
                    // o topo da pilha já passa a ser a nova rota.
                    val previousDestinationId = navController.currentDestination?.id
                    navController.navigate(command.route) {
                        if (previousDestinationId != null) {
                            popUpTo(previousDestinationId) { inclusive = true }
                        }
                    }
                }

                NavCommand.GoBack -> navController.popBackStack()
            }
        }
    }

    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavHost(
                navController = navController,
                startDestination = Route.Splash
            ) {
                composable<Route.Splash> {
                    SplashScreen()
                }
                composable<Route.Welcome> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        WelcomeScreen()
                    }
                }
                composable<Route.SignIn> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        SignInScreen()
                    }
                }
                composable<Route.SignUp> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        SignUpScreen()
                    }
                }
                composable<Route.ManagerHome> {
                    ManagerHome()
                }
                composable<Route.EmployeeHome> {
                    EmployeeHome()
                }
            }
        }
    }
}
