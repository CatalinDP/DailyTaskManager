package com.example.dailytaskmanager.views

import android.R
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytaskmanager.components.AlertDialogComponent
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.model.User
import com.example.dailytaskmanager.notifications.scheduleTestAlarm
import com.example.dailytaskmanager.notifications.sendTestNotification
import com.example.dailytaskmanager.ui.theme.DailyTaskManagerTheme
import com.example.dailytaskmanager.utils.getCurrentDayName
import com.example.dailytaskmanager.utils.getFormattedCurrentDate
import com.example.dailytaskmanager.viewmodel.TaskViewModel
import com.example.dailytaskmanager.viewmodel.UserViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    onBackButton: () -> Unit,
    taskViewModel: TaskViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val todayTaskList by taskViewModel.todayTaskList.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }
    val userName = userViewModel.userName.collectAsStateWithLifecycle()
    val userHour = userViewModel.userStartingHour.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(modifier =
        Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        text = "Daily Task Manager - ${getFormattedCurrentDate()} - Bienvenido, ${userName.value} - Tu día empieza: ${userHour.value}",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
                .padding(innerPadding)
        ) {
            if (todayTaskList.isEmpty()) {
                Text(
                    text = "No task creada",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                for (task in todayTaskList) {
                    Log.d("DAY", "${task.name} - ${task.repetitionDays.toString()}")
                    TaskItem(
                        task,
                            onDeleteTask = { taskViewModel.delTask(task) }
                        )
                }
            }

            TextButton(
                onClick = {
                    showBottomSheet = true
                }
            ) {
                Text(text = "Add Task")
            }
            TextButton(
                onClick = {
                    userViewModel.deleteUser()
                    onBackButton()
                }
            ) {
                Text(text = "Del user")
            }
            Button(onClick = { sendTestNotification(context) }) {
                Text("Probar notificación")
            }

            Button(onClick = { scheduleTestAlarm(context) }) {
                Text("Probar alarma")
            }
            if (showBottomSheet) {
                CreateTaskBottomSheet(
                    onDismiss = { showBottomSheet = false }
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onDeleteTask: () -> Unit,
    ) {
    var isChecked by remember { mutableStateOf(task.isCompleted) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.onSecondary, shape = RoundedCornerShape(16.dp))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                modifier = Modifier
                    .padding(horizontal = 12.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            Column {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.labelLarge,
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${task.startingTime.toString()} - ${task.endingTime.toString()}",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
        IconButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier
                .padding(horizontal = 12.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.RemoveCircle,
                contentDescription = "Delete button",
                tint = MaterialTheme.colorScheme.error
            )
        }
        if (showDeleteDialog) {
            AlertDialogComponent(
                title = "Delete task",
                message = "Seguro que quieres eliminar la tarea",
                onDismiss = { showDeleteDialog = false },
                onConfirm = { onDeleteTask() },
                showDialog = true
            )
        }
    }
}
