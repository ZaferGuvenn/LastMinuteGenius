package com.fastthinkerstudios.lastminutegenius.presentation.videolist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SummarySection(onShowSummaryClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        Button(onClick = onShowSummaryClick, enabled = false) {
            Text("Kısa Sınav Yap")
        }
        Button(onClick = onShowSummaryClick) {
            Text("Özeti Göster")
        }
    }
}
