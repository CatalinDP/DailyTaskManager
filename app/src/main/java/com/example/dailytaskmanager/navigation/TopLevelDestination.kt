package com.example.dailytaskmanager.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Task
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.dailytaskmanager.R

data class TopLevelDestination<T : Any>(
    val route: T,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
)

val topLevelDestinations = listOf(
    TopLevelDestination(
        route = HomeRoute,
        labelRes = R.string.navigation_home,
        icon = Icons.Rounded.Home
    ),
    TopLevelDestination(
        route = TasksRoute,
        labelRes = R.string.navigation_tasks,
        icon = Icons.Rounded.Task
    ),
    TopLevelDestination(
        route = SettingsRoute,
        labelRes = R.string.navigation_settings,
        icon = Icons.Rounded.Settings
    ),

)