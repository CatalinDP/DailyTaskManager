package com.example.dailytaskmanager.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dailytaskmanager.viewmodel.UserViewModel
import com.example.dailytaskmanager.views.TaskView
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.Alignment
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.dailytaskmanager.components.AppBottomBar
import com.example.dailytaskmanager.views.OnboardingView

@Composable
fun AppNavigation(
    userViewModel: UserViewModel = koinViewModel()
) {
    val navController = rememberNavController();
    val userName = userViewModel.userName.collectAsStateWithLifecycle()
    val userFinishedOnboarding: Boolean = !userName.value.isBlank()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val canShowBottomBar = topLevelDestinations.any { dest ->
        currentDestination?.hasRoute(dest.route::class) == true
    }
    Box() {
        NavHost(
            navController = navController,
            startDestination = if (userFinishedOnboarding) TaskView else Onboarding,
            modifier = Modifier
                .fillMaxSize()
        ) {
            composable<Onboarding> {
                OnboardingView(
                    onNavigateToHome = {
                        navController.navigate(TaskView)
                    }
                )
            }

            composable<TaskView> {
                TaskView(
                    onBackButton = { navController.popBackStack() }
                )
            }
        }
        if (canShowBottomBar) {
            AppBottomBar(
                currentDestination = currentDestination,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}