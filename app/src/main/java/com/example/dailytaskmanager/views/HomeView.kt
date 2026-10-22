package com.example.dailytaskmanager.views

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytaskmanager.components.AppTopBar
import com.example.dailytaskmanager.components.TaskCompletionStat
import com.example.dailytaskmanager.components.TaskItem
import com.example.dailytaskmanager.components.WeatherComponent
import com.example.dailytaskmanager.components.WeatherSkeleton
import com.example.dailytaskmanager.R
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.model.WeatherState
import com.example.dailytaskmanager.navigation.TaskView
import com.example.dailytaskmanager.ui.messageRes
import com.example.dailytaskmanager.utils.getFormattedCurrentDate
import com.example.dailytaskmanager.viewmodel.HomeViewModel
import com.example.dailytaskmanager.viewmodel.SettingsViewModel
import com.example.dailytaskmanager.viewmodel.TaskViewModel
import com.example.dailytaskmanager.viewmodel.UserViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeView(
    homeViewModel: HomeViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    taskViewModel: TaskViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onSeeMoreTasksTap: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    fun fetchLocation() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        Log.d("LOCATION", "LOCATION: $granted")
        if (!granted) {
            homeViewModel.onLocationDenied()
            return
        }

        scope.launch {
            val location = LocationServices.getFusedLocationProviderClient(context)
                .getCurrentLocation(Priority.PRIORITY_LOW_POWER, null)
                .await()
            Log.d("LOCATION", "LOCATION: $location")
            if (location != null) {
                homeViewModel.getWeather(location.latitude, location.longitude)
                homeViewModel.coordinates.value.copy(first = location.latitude, second = location.longitude)
            } else {
                homeViewModel.onLocationDenied()
            }
        }
    }

    val userName = userViewModel.userName.collectAsStateWithLifecycle()
    val weather by homeViewModel.weather.collectAsStateWithLifecycle()
    val tasks = homeViewModel.nextTasks.collectAsStateWithLifecycle()
    val showWeather = settingsViewModel.showWeather.collectAsStateWithLifecycle()
    val completedIds = taskViewModel.completedIds.collectAsStateWithLifecycle()
    val tempUnit = settingsViewModel.unit.collectAsStateWithLifecycle()
    var showBottomSheet: Boolean by remember { mutableStateOf(false) }
    var taskTapped: Task? by remember { mutableStateOf(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fetchLocation() else homeViewModel.onLocationDenied()
    }

    LaunchedEffect(showWeather.value) {
        if (!showWeather.value) return@LaunchedEffect
        permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            AppTopBar(
                title = getFormattedCurrentDate(),
                label = stringResource(R.string.home_welcome, userName.value),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = 124.dp,
                start = 24.dp,
                end = 24.dp
            )),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TaskCompletionStat()
            if (showWeather.value) {
                when (val state = weather) {
                    WeatherState.Loading -> WeatherSkeleton()
                    is WeatherState.Success -> WeatherComponent(weather = state.data, tempUnit.value)
                    is WeatherState.Error -> Text(
                        text = stringResource(
                            R.string.home_error,
                            stringResource(state.reason.messageRes())
                        )
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.home_upcoming_tasks),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = onSeeMoreTasksTap
                ) {
                    Text(
                        text = stringResource(R.string.home_see_more),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            for (task in tasks.value) {
                TaskItem(
                    task,
                    onTaskChecked = taskViewModel::onTaskChecked,
                    isCompleted = task.id in completedIds.value,
                    onTap = {
                        taskTapped = it
                        showBottomSheet = true
                    }
                )
            }
            if (showBottomSheet) {
                CreateTaskBottomSheet(
                    onDismiss = {
                        taskTapped = null
                        showBottomSheet = false
                                },
                    task = taskTapped
                )
            }
        }
    }
}
