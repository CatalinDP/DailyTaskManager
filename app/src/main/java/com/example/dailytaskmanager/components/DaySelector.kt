package com.example.dailytaskmanager.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowLeft
import androidx.compose.material.icons.automirrored.outlined.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.dailytaskmanager.model.Day

@Composable
fun DaySelector(
    day: Day,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .height(48.dp)
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.AutoMirrored.Outlined.ArrowLeft, contentDescription = null,
            modifier = Modifier
                .size(48.dp)
            )
        }
        Text(
            text = "$day",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Outlined.ArrowRight, contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
            )
        }
    }
}