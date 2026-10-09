package com.example.dailytaskmanager.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dailytaskmanager.viewmodel.UserViewModel
import com.example.dailytaskmanager.views.MainView
import com.example.dailytaskmanager.views.StartView
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation(
    userViewModel: UserViewModel = koinViewModel()
) {
    val navController = rememberNavController();
    val userName = userViewModel.userName.collectAsStateWithLifecycle()
    val userFinishedOnboarding: Boolean = !userName.value.isBlank()

    NavHost(
        navController = navController,
        startDestination = if (userFinishedOnboarding) Home else Start
    ) {
        composable<Start> {
            StartView(
                onNavigateToHome = {
                    navController.navigate(Home)
                }
            )
        }

        composable<Home> {
            MainView(
                onBackButton = { navController.popBackStack() }
            )
        }
    }
}