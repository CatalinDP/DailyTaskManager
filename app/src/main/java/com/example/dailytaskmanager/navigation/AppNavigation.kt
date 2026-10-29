package com.example.dailytaskmanager.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dailytaskmanager.components.AppBottomBar
import com.example.dailytaskmanager.views.HomeView
import com.example.dailytaskmanager.views.OnboardingView
import com.example.dailytaskmanager.views.SettingsView
import com.example.dailytaskmanager.views.TaskView

@Composable
fun AppNavigation(
    initialStart: StartDestination
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val canShowBottomBar = topLevelDestinations.any { dest ->
        currentDestination?.hasRoute(dest.route::class) == true
    }
    val startAtHome = rememberSaveable { initialStart == StartDestination.Home }
    Box {
        NavHost(
            navController = navController,
            startDestination = if (startAtHome) HomeRoute else OnboardingRoute,
            modifier = Modifier
                .fillMaxSize()
        ) {
            composable<OnboardingRoute> {
                OnboardingView(
                    onNavigateToHome = {
                        navController.navigate(HomeRoute) {
                            popUpTo<OnboardingRoute> { inclusive = true }
                        }
                    }
                )
            }

            composable<TasksRoute> {
                TaskView()
            }

            composable<HomeRoute> {
                HomeView(
                    onSeeMoreTasksTap = { navController.navigateToTopLevel(TasksRoute) }
                )
            }
            composable<SettingsRoute> {
                SettingsView()
            }
        }
        if (canShowBottomBar) {
            AppBottomBar(
                currentDestination = currentDestination,
                onNavigate = navController::navigateToTopLevel,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

/**
 * Navigates between bottom-bar destinations keeping a single [HomeRoute] at the root.
 *
 * Pops up to [HomeRoute] instead of the graph's start destination: after the first
 * onboarding the start destination ([OnboardingRoute]) is no longer on the back stack,
 * so popping to it would be ignored and the stack would grow on every tab change.
 */
fun NavController.navigateToTopLevel(route: Any) {
    navigate(route) {
        popUpTo<HomeRoute> {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
