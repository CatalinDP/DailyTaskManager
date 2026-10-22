package com.example.dailytaskmanager.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytaskmanager.R
import com.example.dailytaskmanager.viewmodel.TaskViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TaskCompletionStat(
    taskViewModel: TaskViewModel = koinViewModel()
) {
    val tasks = taskViewModel.selectedDayTasks.collectAsStateWithLifecycle()
    val completedIds = taskViewModel.completedIds.collectAsStateWithLifecycle()
    val tasksCompletedToday = tasks.value.filter { it.id in completedIds.value }.size
    val totalTasks = tasks.value.size

    val target = if (totalTasks == 0) 0f else tasksCompletedToday.toFloat() / totalTasks
    val progress = animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )
    val totalPercentage = (target * 100).toInt()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(start = 18.dp),
        contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress.value },
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurfaceVariant,
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )
            Text(
                text = stringResource(R.string.task_progress_percent, totalPercentage),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.task_completion_today_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = pluralStringResource(
                    R.plurals.task_progress_count,
                    tasksCompletedToday,
                    tasksCompletedToday,
                    totalTasks
                ),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = stringResource(R.string.task_progress_message),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}