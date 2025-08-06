package com.fastthinkerstudios.lastminutegenius.presentation.summary

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    navController: NavHostController,
    videoId: Int,
    modifier: Modifier = Modifier,
    viewModel: SummaryViewModel = hiltViewModel()
) {
    val videoSummary by viewModel.summary.collectAsState()
    var editedText by remember { mutableStateOf(videoSummary) }
    var isEditing by remember { mutableStateOf(false) }
    var textSize by remember { mutableFloatStateOf(18f) }
    var hasInitialized by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(videoId) {
        viewModel.loadVideoSummary(videoId)
    }

    LaunchedEffect(videoSummary) {
        if (!hasInitialized && videoSummary.isNotBlank()) {
            editedText = videoSummary
            hasInitialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Özeti") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(editedText))
                        Toast.makeText(context, "Metin kopyalandı", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Kopyala")
                    }
                    IconButton(onClick = {
                        if (isEditing) viewModel.updateVideoSummary(videoId, editedText)
                        isEditing = !isEditing
                    }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Kaydet" else "Düzenle"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = if (isEditing) "Metni Düzenliyorsunuz" else "Özet",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (isEditing) {
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    placeholder = { Text("Metni düzenleyin...") },
                    textStyle = TextStyle(fontSize = textSize.sp),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = Int.MAX_VALUE
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    SelectionContainer {
                        Text(
                            text = editedText,
                            fontSize = textSize.sp,
                            lineHeight = (textSize * 1.4).sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Yazı Boyutu: ${textSize.toInt()}sp")

                Row {
                    IconButton(
                        onClick = { if (textSize > 12f) textSize -= 2f },
                        enabled = textSize > 12f
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Küçült")
                    }
                    IconButton(
                        onClick = { if (textSize < 40f) textSize += 2f },
                        enabled = textSize < 40f
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Büyüt")
                    }
                }
            }
        }
    }
}
