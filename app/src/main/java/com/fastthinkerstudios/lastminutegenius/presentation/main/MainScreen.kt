package com.fastthinkerstudios.lastminutegenius.presentation.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { viewModel.onVideoSelected(it) } }
    )

    var expanded by remember { mutableStateOf(false) }
    val languageOptions = listOf("Türkçe" to "tr-TR", "İngilizce" to "en-US")
    var selectedLanguage by remember { mutableStateOf(languageOptions[0]) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "🎬 Video'dan Özet Oluştur",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Dil Seçimi
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedLanguage.first,
                onValueChange = {},
                readOnly = true,
                label = { Text("Dil Seçimi") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languageOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.first) },
                        onClick = {
                            selectedLanguage = option
                            expanded = false
                            viewModel.setLanguageCode(option.second)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { videoPicker.launch("video/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📁 Videoyu Seç")
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            uiState.isLoading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Video işleniyor, lütfen bekleyin...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            uiState.summary.isNotBlank() -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .heightIn(min = 200.dp, max = 400.dp) // kart yüksekliğini sınırla
                            .verticalScroll(rememberScrollState()) // sadece burası scrollable
                            .padding(16.dp)
                    ) {
                        Text(
                            text = uiState.summary,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 22.sp
                        )
                    }

                }
            }

            else -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Lütfen bir video seçin ve dili ayarlayın.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

