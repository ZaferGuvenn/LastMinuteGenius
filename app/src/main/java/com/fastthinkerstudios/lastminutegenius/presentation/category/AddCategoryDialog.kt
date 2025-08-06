package com.fastthinkerstudios.lastminutegenius.presentation.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val maxLength = 50

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Kategori", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        if (it.length <= maxLength) name = it
                    },
                    label = { Text("Kategori Adı") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
                Text(
                    text = "${name.length}/$maxLength",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) onConfirm(name.trim())
                }
            ) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        },
        shape = MaterialTheme.shapes.large
    )
}
