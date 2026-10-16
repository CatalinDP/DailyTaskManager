package com.example.dailytaskmanager.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytaskmanager.components.AlertDialogComponent
import com.example.dailytaskmanager.components.AppTopBar
import com.example.dailytaskmanager.components.SwipeToDeleteTaskItem
import com.example.dailytaskmanager.components.TaskItem
import com.example.dailytaskmanager.model.Task
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
    val todayTaskList by taskViewModel.todayTaskList.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }
    val userName = userViewModel.userName.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            AppTopBar(
                title = getFormattedCurrentDate(),
                label = "Bienvenido, ${userName.value}",
                actions = {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(role = Role.Button, onClick = { showBottomSheet = true })
                            .padding(vertical = 6.dp),
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
                            text = "Add task",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding() + 24.dp,
                    start = 24.dp,
                    end = 24.dp
                ))
        ) {
            if (todayTaskList.isEmpty()) {
                item {
                    Text(
                        text = "No task creada",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            } else {
                items(todayTaskList, key = { it.id }) { task ->
                    Log.d("DAY", "${task.name} - ${task.repetitionDays.toString()}")
                    SwipeToDeleteTaskItem(
                        task = task,
                        modifier = Modifier.animateItem(),
                        onDelete = {
                            Log.d("TASK", "Tarea Eliminar: ${task.id}")
                            taskViewModel.requestDelete(task)
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                var undone = false
                                try {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Tarea eliminada",
                                        actionLabel = "Deshacer",
                                        duration = SnackbarDuration.Short
                                    )
                                    undone = result == SnackbarResult.ActionPerformed
                                } finally {
                                    if (!undone) {
                                        taskViewModel.commitDelete(task)
                                    } else {
                                        taskViewModel.undoDelete(task)
                                    }
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (showBottomSheet) {
                item {
                    CreateTaskBottomSheet(
                        onDismiss = { showBottomSheet = false }
                    )
                }
            }
        }
    }
}
