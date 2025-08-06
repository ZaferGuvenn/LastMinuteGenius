package com.fastthinkerstudios.lastminutegenius.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fastthinkerstudios.lastminutegenius.presentation.category.CategoryScreen
import com.fastthinkerstudios.lastminutegenius.presentation.summary.SummaryScreen
import com.fastthinkerstudios.lastminutegenius.presentation.videolist.VideoListScreen

sealed class Screen(val route: String) {
    object Category : Screen("category")
    object VideoList : Screen("video_list/{categoryId}") {
        fun createRoute(categoryId: Int) = "video_list/$categoryId"
    }
    object Summary : Screen("summary/{videoId}") {
        fun createRoute(videoId: Int) = "summary/$videoId"
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onVideoAdd : (Int)-> Unit
){
    NavHost(navController, startDestination = Screen.Category.route){

        composable(Screen.Category.route){

            CategoryScreen(
                onVideoAddClicked = { categoryId->
                    onVideoAdd(categoryId) },
                onCategoryClicked = { categoryId ->
                    navController.navigate(Screen.VideoList.createRoute(categoryId))
                }
            )
        }

        composable(Screen.VideoList.route){ backStackEntry ->

            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull() ?: return@composable
            VideoListScreen(
                categoryId = categoryId,
                onShowSummaryClicked = { videoId->

                    navController.navigate(Screen.Summary.createRoute(videoId))

                }

            )
        }

        composable(Screen.Summary.route) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId")?.toIntOrNull() ?: return@composable

            SummaryScreen(videoId = videoId)
        }


    }
}