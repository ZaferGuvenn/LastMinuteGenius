package com.fastthinkerstudios.lastminutegenius.presentation.main

import android.media.MediaMetadataRetriever
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val enhanced by viewModel.enhancedSummary.collectAsState()
    val selectedVideoUri by viewModel.selectedVideoUri.collectAsState()

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { viewModel.setSelectedVideoUri(it) } } // artık sadece URI'yi kaydeder
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Checkbox(
                checked = enhanced,
                onCheckedChange = { if (!uiState.isLoading) viewModel.setEnhancedSummary(it) else null },
                enabled = !uiState.isLoading
            )
            Text("Görsellerle daha kaliteli özet istiyorum")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { videoPicker.launch("video/*") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text("📁 Videoyu Seç")
        }

        // Snapshot (thumbnail)
        selectedVideoUri?.let { videoUri ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("📷 Seçilen Video", style = MaterialTheme.typography.bodyMedium)

            Box(modifier = Modifier.padding(8.dp)) {
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            val retriever = MediaMetadataRetriever()
                            retriever.setDataSource(context, videoUri)
                            val bitmap = retriever.frameAtTime
                            retriever.release()
                            setImageBitmap(bitmap)
                            layoutParams = ViewGroup.LayoutParams(400, 300)
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                    },
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                )

                IconButton(
                    onClick = { viewModel.clearSelectedVideoUri() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Videoyu kaldır")
                }
            }

            if (enhanced && selectedVideoUri != null && !uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                FrameSelector(
                    videoProcessor = viewModel.getVideoProcessor(),
                    videoUri = selectedVideoUri!!,
                    selectedFrames = viewModel.selectedFrames,
                    onAddFrame = { viewModel.addFrame(it) },
                    onRemoveFrame = { viewModel.removeFrameAt(it) }
                )
            }


        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gönder Butonu
        Button(
            onClick = { viewModel.processSelectedVideo() },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedVideoUri != null && !uiState.isLoading
        ) {
            Text("🚀 Gönder ve Özetle")
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
                            .heightIn(min = 200.dp, max = 400.dp)
                            .verticalScroll(rememberScrollState())
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
                    "Lütfen bir video seçin ve ardından Gönder’e tıklayın.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }


}


