package com.example.dailytaskmanager.views

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytaskmanager.components.AppTopBar
import com.example.dailytaskmanager.components.TaskCompletionStat
import com.example.dailytaskmanager.components.TaskItem
import com.example.dailytaskmanager.components.WeatherComponent
import com.example.dailytaskmanager.components.WeatherSkeleton
import com.example.dailytaskmanager.R
import com.example.dailytaskmanager.components.SuggestionBox
import com.example.dailytaskmanager.components.WeeklyStatView
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.model.WeatherState
import com.example.dailytaskmanager.ui.messageRes
import com.example.dailytaskmanager.utils.getCurrentDayNumber
import com.example.dailytaskmanager.utils.getFormattedCurrentDate
import com.example.dailytaskmanager.utils.hasLocationPermission
import com.example.dailytaskmanager.utils.weekDayNarrowLabels
import com.example.dailytaskmanager.viewmodel.HomeViewModel
import com.example.dailytaskmanager.viewmodel.SettingsViewModel
import com.example.dailytaskmanager.viewmodel.TaskViewModel
import com.example.dailytaskmanager.viewmodel.UserViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeView(
    homeViewModel: HomeViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    taskViewModel: TaskViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onSeeMoreTasksTap: () -> Unit = {},
) {
    val context = LocalContext.current
    val userName = userViewModel.userName.collectAsStateWithLifecycle()
    val weather by homeViewModel.weather.collectAsStateWithLifecycle()
    val tasks = homeViewModel.nextTasks.collectAsStateWithLifecycle()
    val todayTasks = homeViewModel.todayTasks.collectAsStateWithLifecycle()
    val showWeather = settingsViewModel.showWeather.collectAsStateWithLifecycle()
    val completedIds = taskViewModel.completedIds.collectAsStateWithLifecycle()
    val tempUnit = settingsViewModel.unit.collectAsStateWithLifecycle()
    var showBottomSheet: Boolean by remember { mutableStateOf(false) }
    var taskTapped: Task? by remember { mutableStateOf(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) homeViewModel.loadWeather() else homeViewModel.onLocationDenied()
    }
    val weekProgression = taskViewModel.weekProgression.collectAsStateWithLifecycle()
    val streak = taskViewModel.dayStreak.collectAsStateWithLifecycle()

    val streakText = if (streak.value > 0) {
        pluralStringResource(R.plurals.home_streak, streak.value, streak.value)
    } else {
        stringResource(R.string.home_streak_start)
    }

    LaunchedEffect(showWeather.value) {
        if (!showWeather.value) return@LaunchedEffect
        // Only ask when needed; loadWeather() keeps already loaded data
        if (context.hasLocationPermission()) {
            homeViewModel.loadWeather()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    val scrollState = rememberScrollState()
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
            modifier = Modifier
                .padding(
                    PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = 124.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
                )
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TaskCompletionStat(tasks = todayTasks.value, completedIds = completedIds.value)
            WeeklyStatView(
                values = weekProgression.value,
                labels = weekDayNarrowLabels(),
                highlightedIndex = getCurrentDayNumber(),
            )
            SuggestionBox(
                streakText,
                icon = Icons.Outlined.LocalFireDepartment
            )
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
                    },
                    isToday = true
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
