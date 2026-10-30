package com.example.dailytaskmanager.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.dailytaskmanager.utils.outlinedTextFieldColors

@Composable
fun TextFieldDialog(
    title: String,
    label: String,
    onDismiss: () -> Unit,
    initialText: String = "",
    onConfirm: (String) -> Unit,
) {
    var text by remember { mutableStateOf(initialText) }
    val trimmed = text.trim()

    AppDialog(
        title = title,
        onDismiss = onDismiss,
        onConfirm = { onConfirm(trimmed) },
        confirmEnabled = trimmed.isNotEmpty()
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(label) },
            colors = outlinedTextFieldColors(),
            singleLine = true
        )
    }
}
