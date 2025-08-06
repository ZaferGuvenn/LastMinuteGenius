package com.fastthinkerstudios.lastminutegenius

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.fastthinkerstudios.lastminutegenius.presentation.category.CategoryViewModel
import com.fastthinkerstudios.lastminutegenius.presentation.navigation.NavigationGraph
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        setContent {
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
                onVideoAdd = {  categoryId->

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


