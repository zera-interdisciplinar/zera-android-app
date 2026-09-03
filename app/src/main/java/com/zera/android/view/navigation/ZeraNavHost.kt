package com.zera.android.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zera.android.view.screens.SplashScreen
import com.zera.android.view.screens.WelcomeScreen

@Composable
fun ZeraNavHost(){
    val navController : NavHostController = rememberNavController()

    LaunchedEffect(Unit) {
        ZeraNavigator.commands.collect { command ->
            when (command) {
                is NavCommand.Navigate -> navController.navigate(command.route)
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
    }
}