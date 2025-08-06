package com.fastthinkerstudios.lastminutegenius.presentation.summary

import android.widget.Toast
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.play.integrity.internal.v


@Composable
fun SummaryScreen(
    videoId: Int,
    modifier: Modifier = Modifier,
    viewModel: SummaryViewModel= hiltViewModel()
) {

    val videoSummary by viewModel.summary.collectAsState()

    LaunchedEffect(videoId) {
        viewModel.loadVideoSummary(videoId)
    }

    println("Videoid ve summary")
    println(videoId)
    println(videoSummary)



    var isEditing by remember { mutableStateOf(false) }
    var textSize by remember { mutableFloatStateOf(18f) }
    var editedText by remember { mutableStateOf(videoSummary) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var hasInitialized by remember { mutableStateOf(false) }

    // burada ilk yükleme esnasında sadece video summary değerini alıp edittexte ekledik
    // çünkü kullanıcının bu edittexti düzenlemesine izin veriyoruz ve
    // kullanıcı düzenlerken summary değerinin değişmesi durumunda
    // kullanıcının düzenlemeleri sıfırlanabilirdi diye bu önlemi aldık.
    LaunchedEffect(videoSummary) {
        if (!hasInitialized && videoSummary.isNotEmpty()) {
            editedText = videoSummary
            hasInitialized = true
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isEditing) "Özeti Düzenle" else "Özet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(onClick = {
                    clipboardManager.setText(AnnotatedString(editedText))
                    Toast.makeText(context, "Metin kopyalandı", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Kopyala")
                }
                IconButton(onClick = {
                    isEditing = !isEditing
                    viewModel.updateVideoSummary(videoId, editedText)
                }) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = if (isEditing) "Kaydet" else "Düzenle"
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (isEditing) {
            OutlinedTextField(
                value = editedText,
                onValueChange = { editedText = it },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                textStyle = TextStyle(fontSize = textSize.sp),
                placeholder = { Text("Metni düzenleyin...") }
            )
        } else {
            Box(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
                    .fillMaxWidth()
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

        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Yazı Boyutu: ${textSize.toInt()}sp")
            IconButton(onClick = { if (textSize > 12f) textSize -= 2f }) {
                Icon(Icons.Default.Remove, contentDescription = "Küçült")
            }
            IconButton(onClick = { if (textSize < 40f) textSize += 2f }) {
                Icon(Icons.Default.Add, contentDescription = "Büyüt")
            }
        }
    }
}
