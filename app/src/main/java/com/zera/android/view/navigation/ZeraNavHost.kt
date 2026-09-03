package com.zera.android.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zera.android.view.screens.LoginScreen
import com.zera.android.view.screens.SplashScreen
import com.zera.android.view.screens.WelcomeScreen
import com.zera.android.view.screens.employee.EmployeeHome
import com.zera.android.view.screens.manager.ManagerHome

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

    NavHost(
        navController = navController,
        startDestination = Route.Splash
    ) {
        composable<Route.Splash> {
            SplashScreen()
        }
        composable<Route.Welcome>{
            WelcomeScreen()
        }
        composable<Route.Login>{
            LoginScreen()
        }
        composable<Route.ManagerHome>{
            ManagerHome()
        }
        composable<Route.EmployeeHome>{
            EmployeeHome()
        }
    }
}