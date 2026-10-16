package com.example.dailytaskmanager.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Task
import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelDestination<T : Any>(
    val route: T,
    val label: String,
    val icon: ImageVector
)

val topLevelDestinations = listOf(
    TopLevelDestination(
        route = TaskView,
        label = "Home",
        icon = Icons.Rounded.Home
    ),
    TopLevelDestination(
        route = TaskView,
        label = "Tasks",
        icon = Icons.Rounded.Task
    ),
    TopLevelDestination(
        route = TaskView,
        label = "Ajustes",
        icon = Icons.Rounded.Settings
    ),

)