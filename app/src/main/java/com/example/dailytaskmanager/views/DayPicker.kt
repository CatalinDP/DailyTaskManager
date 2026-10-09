package com.example.dailytaskmanager.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dailytaskmanager.model.Day

@Composable
fun DayPicker(
    selectedDays: Set<Day>,
    onSelectedDaysChange: (Day) -> Unit,
) {
    val days = Day.entries
    var showDayPickerOptions by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { showDayPickerOptions = !showDayPickerOptions },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Select Days")
        }
        AnimatedVisibility(
            visible = showDayPickerOptions,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (day in days) {
                    Log.e("DAYS", day.toString())
                    DayCard(
                        day,
                        onSelectChange = { day ->
                            onSelectedDaysChange(day)
                        },
                        isSelected = day in selectedDays
                    )
                }
            }
        }
        if (selectedDays.isNotEmpty()) {
            Row {
                Text(
                    text = selectedDays.joinToString(", "),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DayCard(
    day: Day,
    onSelectChange: (Day) -> Unit,
    isSelected: Boolean = false
) {
    val isFirst = day == Day.MONDAY
    val isLast = day == Day.SUNDAY

    /// TODO: MAKE IT CLIP RELATIVE TO IS FIRST OR LAST
    Surface (
        modifier = Modifier
            .width(420.dp)
            .height(64.dp),
        onClick = { onSelectChange(day) },
        color = if (!isSelected)  Color.White else Color.Green,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = day.toString()
            )
        }
    }
}