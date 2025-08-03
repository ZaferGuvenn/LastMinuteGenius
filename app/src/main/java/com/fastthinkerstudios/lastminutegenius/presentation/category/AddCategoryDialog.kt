package com.fastthinkerstudios.lastminutegenius.presentation.category

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.remember

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("")}

    AlertDialog(
        onDismissRequest= onDismiss,
        title = { Text("Yeni Kategori")},
        text ={
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text( "Kategori Adı")}
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) onConfirm(name)
            }) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}