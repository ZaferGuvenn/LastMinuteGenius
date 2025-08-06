package com.fastthinkerstudios.lastminutegenius

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.fastthinkerstudios.lastminutegenius.presentation.category.CategoryViewModel
import com.fastthinkerstudios.lastminutegenius.presentation.navigation.NavigationGraph
import com.fastthinkerstudios.lastminutegenius.ui.theme.LastMinuteGeniusTheme
import com.fastthinkerstudios.lastminutegenius.ui.theme.Typography
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        FirebaseApp.initializeApp(this)

        setContent {
            MaterialTheme {
                // Set status bar colors
                val isDarkTheme = isSystemInDarkTheme()
                LaunchedEffect(isDarkTheme) {
                    window.statusBarColor = Color.Transparent.toArgb()
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = !isDarkTheme
                    }
                }

                val navController = rememberNavController()
                val context = LocalContext.current
                val viewModel: CategoryViewModel = hiltViewModel()

                var currentCategoryId by remember { mutableStateOf<Int?>(null) }

                // Android 13+ picker
                val videoPicker13Launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickMultipleVisualMedia()
                ) { uris ->
                    if (!uris.isNullOrEmpty()) {
                        currentCategoryId?.let { catId ->
                            viewModel.addVideosToCategory(uris, catId, context)
                        }
                    }
                }

                // Android 24–32 picker
                val videoPickerLegacyLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenMultipleDocuments()
                ) { uris ->
                    if (!uris.isNullOrEmpty()) {
                        currentCategoryId?.let { catId ->
                            viewModel.addVideosToCategory(uris, catId, context)
                        }
                    }
                }

                NavigationGraph(
                    navController,
                    onVideoAdd = { categoryId ->
                        currentCategoryId = categoryId

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            videoPicker13Launcher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        } else {
                            videoPickerLegacyLauncher.launch(arrayOf("video/*"))
                        }
                    }
                )
            }
        }
    }
}


