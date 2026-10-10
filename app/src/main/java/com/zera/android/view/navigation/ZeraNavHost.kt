package com.zera.android.view.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.zera.android.view.screens.auth.SignInScreen
import com.zera.android.view.screens.auth.SignUpScreen
import com.zera.android.view.screens.SplashScreen
import com.zera.android.view.screens.auth.WelcomeScreen
import com.zera.android.view.screens.employee.EmployeeHomeScreen
import com.zera.android.view.screens.employee.EmployeeItemsScreen
import com.zera.android.view.screens.employee.ManualRegisterScreen
import com.zera.android.view.screens.employee.ScanScreen
import com.zera.android.view.screens.employee.WorkingCentralScreen
import com.zera.android.view.screens.manager.CategoriesScreen
import com.zera.android.view.screens.manager.CategoryCreationScreen
import com.zera.android.view.screens.manager.CategoryCreationSuccessScreen
import com.zera.android.view.screens.manager.EmployeesScreen
import com.zera.android.view.screens.manager.IndexesScreen
import com.zera.android.view.screens.manager.ItemApprovedScreen
import com.zera.android.view.screens.manager.ItemDetailsScreen
import com.zera.android.view.screens.manager.ItensScreen
import com.zera.android.view.screens.manager.ManagerHomeScreen
import com.zera.android.view.screens.manager.ModelCreationScreen
import com.zera.android.view.screens.manager.ModelCreationSuccessScreen
import com.zera.android.view.screens.manager.ModelItemsScreen
import com.zera.android.view.screens.manager.ModelsScreen
import com.zera.android.view.screens.manager.recycling.ItensResumeScreen
import com.zera.android.view.screens.manager.recycling.ItensSelectionScreen
import com.zera.android.view.screens.manager.recycling.RecyclingResumeScreen
import com.zera.android.view.screens.manager.recycling.RecyclingScreen
import com.zera.android.view.screens.manager.recycling.ScheduledDisposalsScreen
import com.zera.android.view.screens.manager.recycling.SchedulingDetailsScreen
import com.zera.android.view.screens.manager.recycling.SchedulingSuccessScreen
import com.zera.android.view.screens.shared.ProfileScreen
import com.zera.android.view.transition.LocalAnimatedVisibilityScope
import com.zera.android.view.transition.LocalSharedTransitionScope
import com.zera.android.view.transition.ScreenAnimationRegistry

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ZeraNavHost() {
    val navController: NavHostController = rememberNavController()
    val animations = remember { ScreenAnimationRegistry() }

    LaunchedEffect(Unit) {
        ZeraNavigator.commands.collect { command ->
            when (command) {
                is NavCommand.Navigate -> {
                    animations.prepare(command.animation, navController)
                    navController.navigate(command.route)
                }

                is NavCommand.PushAndPop -> {
                    animations.prepare(command.animation, navController)
                    val backStack = navController.currentBackStack.value
                        .filter { it.destination !is NavGraph }
                    val popFromIndex = (backStack.size - command.popCount).coerceAtLeast(0)
                    val popFromId = backStack.getOrNull(popFromIndex)?.destination?.id
                        ?: navController.currentDestination?.id
                    navController.navigate(command.route) {
                        if (popFromId != null) {
                            popUpTo(popFromId) { inclusive = true }
                        }
                    }
                }

                is NavCommand.PushAndPopAll -> {
                    animations.prepare(command.animation, navController)
                    navController.navigate(command.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
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
                startDestination = Route.Splash,
                enterTransition = { animations.of(targetState).enter() },
                exitTransition = { animations.of(targetState).exit() },
                popEnterTransition = { animations.of(initialState).popEnter() },
                popExitTransition = { animations.of(initialState).popExit() },
            ) {
                composable<Route.Splash> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        SplashScreen()
                    }
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
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        ManagerHomeScreen()
                    }
                }
                composable<Route.ItemDetails> { backStackEntry ->
                    val route = backStackEntry.toRoute<Route.ItemDetails>()
                    ItemDetailsScreen(itemId = route.itemId)
                }
                composable<Route.Employees> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        EmployeesScreen()
                    }
                }
                composable<Route.ItemApproved> { backStackEntry ->
                    val route = backStackEntry.toRoute<Route.ItemApproved>()
                    ItemApprovedScreen(
                        itemId = route.itemId,
                        itemName = route.itemName,
                        itemSubtitle = route.itemSubtitle,
                    )
                }
                composable<Route.Indexes> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        IndexesScreen()
                    }
                }
                composable<Route.Itens> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        ItensScreen()
                    }
                }
                composable<Route.Recycling> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        RecyclingScreen()
                    }
                }
                composable<Route.ItensSelection> { backStackEntry ->
                    val route = backStackEntry.toRoute<Route.ItensSelection>()
                    ItensSelectionScreen(
                        placeId = route.placeId,
                        placeName = route.placeName,
                        placeAddress = route.placeAddress,
                        distanceMeters = route.distanceMeters,
                        isOpen = route.isOpen,
                        description = route.description,
                        openingDays = route.openingDays,
                        openingHourLabels = route.openingHourLabels,
                        recyclingBusinessId = route.recyclingBusinessId,
                        contactEmail = route.contactEmail,
                    )
                }
                composable<Route.RecyclingResume> { backStackEntry ->
                    val route = backStackEntry.toRoute<Route.RecyclingResume>()
                    RecyclingResumeScreen(
                        placeId = route.placeId,
                        placeName = route.placeName,
                        placeAddress = route.placeAddress,
                        distanceMeters = route.distanceMeters,
                        itemIds = route.itemIds,
                        itemNames = route.itemNames,
                        isOpen = route.isOpen,
                        description = route.description,
                        openingDays = route.openingDays,
                        openingHourLabels = route.openingHourLabels,
                        recyclingBusinessId = route.recyclingBusinessId,
                        contactEmail = route.contactEmail,
                    )
                }
                composable<Route.SchedulingSuccess> { backStackEntry ->
                    val route = backStackEntry.toRoute<Route.SchedulingSuccess>()
                    SchedulingSuccessScreen(
                        recyclerName = route.recyclerName,
                        scheduledAt = route.scheduledAt,
                        materials = route.materials,
                        contactEmail = route.contactEmail,
                        contactPhone = route.contactPhone,
                        itemNames = route.itemNames,
                        disposalId = route.disposalId,
                    )
                }
                composable<Route.SchedulingDetails> {
                    SchedulingDetailsScreen()
                }
                composable<Route.ItensResume> { backStackEntry ->
                    val route = backStackEntry.toRoute<Route.ItensResume>()
                    ItensResumeScreen(
                        itemIds = route.itemIds,
                        itemNames = route.itemNames,
                    )
                }
                composable<Route.ScheduledDisposals> {
                    ScheduledDisposalsScreen()
                }
                composable<Route.Categories> {
                    CategoriesScreen()
                }
                composable<Route.CategoryCreation> {
                    CategoryCreationScreen()
                }
                composable<Route.CategoryCreationSuccess> { backStackEntry ->
                    val route = backStackEntry.toRoute<Route.CategoryCreationSuccess>()
                    CategoryCreationSuccessScreen(
                        name = route.name,
                        description = route.description,
                    )
                }
                composable<Route.Models> {
                    ModelsScreen()
                }
                composable<Route.ModelItems> { backStackEntry ->
                    val route = backStackEntry.toRoute<Route.ModelItems>()
                    ModelItemsScreen(
                        modelId = route.modelId,
                        modelName = route.modelName,
                    )
                }
                composable<Route.ModelCreation> {
                    ModelCreationScreen()
                }
                composable<Route.ModelCreationSuccess> { backStackEntry ->
                    val route = backStackEntry.toRoute<Route.ModelCreationSuccess>()
                    ModelCreationSuccessScreen(
                        modelName = route.modelName,
                        material = route.material,
                        brand = route.brand,
                        notes = route.notes,
                    )
                }
                composable<Route.EmployeeHome> {
                    EmployeeHomeScreen()
                }
                composable<Route.Scan> {
                    ScanScreen()
                }
                composable<Route.EmployeeItems> {
                    EmployeeItemsScreen()
                }
                composable<Route.WorkingCentral> {
                    WorkingCentralScreen()
                }
                composable<Route.ManualRegister> { backStackEntry ->
                    val route = backStackEntry.toRoute<Route.ManualRegister>()
                    ManualRegisterScreen(modelName = route.modelName)
                }
                composable<Route.Profile> {
                    ProfileScreen()
                }
            }
        }
    }
}
