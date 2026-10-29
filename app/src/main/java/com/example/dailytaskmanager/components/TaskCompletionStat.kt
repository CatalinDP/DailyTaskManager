package com.example.dailytaskmanager.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.dailytaskmanager.R
import com.example.dailytaskmanager.model.Task

@Composable
fun TaskCompletionStat(
    tasks: List<Task>,
    completedIds: Set<Long>
) {
    val tasksCompletedToday = tasks.count { it.id in completedIds }
    val totalTasks = tasks.size
    val colors = MaterialTheme.colorScheme

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
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(16.dp))
            .clip(shape = RoundedCornerShape(16.dp))
            .background(colors.surfaceContainerHigh),
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
                color = colors.tertiary,
                trackColor = colors.onSurfaceVariant,
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )
            Text(
                text = stringResource(R.string.task_progress_percent, totalPercentage),
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.task_completion_today_title),
                style = MaterialTheme.typography.labelLarge,
                color = colors.onSurfaceVariant
            )
            Text(
                text = pluralStringResource(
                    R.plurals.task_progress_count,
                    tasksCompletedToday,
                    tasksCompletedToday,
                    totalTasks
                ),
                style = MaterialTheme.typography.titleLarge,
                color = colors.onSurface
            )
            Text(
                text = stringResource(R.string.task_progress_message),
                style = MaterialTheme.typography.labelMedium,
                color = colors.primary
            )
        }
    }
}
