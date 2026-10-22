package com.example.dailytaskmanager.views

import android.util.Log
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytaskmanager.components.AppTopBar
import com.example.dailytaskmanager.components.SwipeToDeleteContainer
import com.example.dailytaskmanager.components.TaskItem
import com.example.dailytaskmanager.R
import com.example.dailytaskmanager.components.DaySelector
import com.example.dailytaskmanager.model.Day
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.utils.getCurrentDayName
import com.example.dailytaskmanager.utils.getFormattedCurrentDate
import com.example.dailytaskmanager.viewmodel.TaskViewModel
import com.example.dailytaskmanager.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskView(
    onBackButton: () -> Unit,
    taskViewModel: TaskViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val todayTaskList by taskViewModel.selectedDayTasks.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }
    val userName = userViewModel.userName.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val taskDeletedMessage = stringResource(R.string.tasks_deleted)
    val undoLabel = stringResource(R.string.tasks_undo)
    var undoCount by remember { mutableIntStateOf(0) }
    val selectedDay = taskViewModel.selectedDay.collectAsStateWithLifecycle()
    val completedIds = taskViewModel.completedIds.collectAsStateWithLifecycle()
    var taskTapped: Task? by remember { mutableStateOf(null) }

    LaunchedEffect(completedIds) {
        Log.d("COMPLETION", "completedIds: ${completedIds.value}")
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            AppTopBar(
                title = getFormattedCurrentDate(),
                label = stringResource(R.string.home_welcome, userName.value),
                actions = {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(role = Role.Button, onClick = { showBottomSheet = true })
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.tasks_add),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 88.dp)
            )
        }
    ) { innerPadding ->
        Log.d("TASK", "lista: ${todayTaskList.map { it.id }}")
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = 124.dp,
                start = 24.dp,
                end = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (todayTaskList.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.tasks_empty),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            } else {
                items(todayTaskList, key = { it.id }) { task ->
                    key(undoCount) {
                        Log.d("DAY", "${task.name} - ${task.repetitionDays.toString()}")
                        SwipeToDeleteContainer(
                            modifier = Modifier.animateItem(),
                            onDelete = {
                                Log.d("TASK", "Tarea Eliminar: ${task.id}")
                                taskViewModel.requestDelete(task)
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    var undone = false
                                    try {
                                        val result = snackbarHostState.showSnackbar(
                                            message = taskDeletedMessage,
                                            actionLabel = undoLabel,
                                            duration = SnackbarDuration.Short
                                        )
                                        Log.d("TASKDEL", "snackbar result: $result")
                                        undone = result == SnackbarResult.ActionPerformed
                                    } finally {
                                        if (!undone) {
                                            taskViewModel.commitDelete(task)
                                            Log.d("TASKDEL", "Tarea eliminada")
                                        } else {
                                            undoCount++
                                            taskViewModel.undoDelete(task)
                                            Log.d("TASKDEL", "Tarea salvada")
                                        }
                                    }
                                }
                            }) {
                            TaskItem(
                                task = task,
                                onTaskChecked = taskViewModel::onTaskChecked,
                                isCompleted = task.id in completedIds.value,
                                onTap = {
                                    taskTapped = it
                                    showBottomSheet = true
                                }
                                )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            if (showBottomSheet) {
                item {
                    CreateTaskBottomSheet(
                        onDismiss = {
                            showBottomSheet = false
                            taskTapped = null
                                    },
                        task = taskTapped
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 124.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            DaySelector(
                day = selectedDay.value,
                onNext = {
                    taskViewModel.getNextDay()
                },
                onPrevious = {
                    taskViewModel.getPreviousDay()
                },
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

