package com.fastthinkerstudios.lastminutegenius.presentation.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fastthinkerstudios.lastminutegenius.data.local.entity.CategoryEntity
import com.fastthinkerstudios.lastminutegenius.domain.model.Category
import com.fastthinkerstudios.lastminutegenius.util.toEntity

@Composable
fun SelectCategoryDialog(
    categories: List<Category>,
    onDismiss: () -> Unit,
    onCategorySelected: (Category) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Kategori Seç",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { category ->
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCategorySelected(category) }
                            .padding(horizontal = 4.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        },
        shape = MaterialTheme.shapes.large
    )
}
