package com.example.rentalproofer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rentalproofer.ui.screens.camera.CameraScreen
import com.example.rentalproofer.ui.screens.photoviewer.PhotoViewerScreen
import com.example.rentalproofer.ui.screens.sessiondetail.SessionDetailScreen
import com.example.rentalproofer.ui.screens.sessionform.SessionFormScreen
import com.example.rentalproofer.ui.screens.sessionlist.SessionListScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "session_list") {
        composable("session_list") {
            SessionListScreen(
                onCreateSession = { navController.navigate("session_form") },
                onOpenSession = { sessionId -> navController.navigate("session_detail/$sessionId") }
            )
        }
        composable(
            route = "session_form?sessionId={sessionId}",
            arguments = listOf(navArgument("sessionId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStack ->
            val sessionId = backStack.arguments?.getLong("sessionId") ?: -1L
            SessionFormScreen(
                sessionId = if (sessionId == -1L) null else sessionId,
                onSaved = { savedId ->
                    navController.navigate("session_detail/$savedId") {
                        popUpTo("session_list")
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(
            route = "session_detail/{sessionId}",
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStack ->
            val sessionId = backStack.arguments!!.getLong("sessionId")
            SessionDetailScreen(
                sessionId = sessionId,
                onAddPhoto = { type -> navController.navigate("camera/$sessionId/$type") },
                onPhotoClick = { photoId -> navController.navigate("photo_viewer/$photoId") },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "camera/{sessionId}/{photoType}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.LongType },
                navArgument("photoType") { type = NavType.StringType }
            )
        ) { backStack ->
            val sessionId = backStack.arguments!!.getLong("sessionId")
            val photoType = backStack.arguments!!.getString("photoType")!!
            CameraScreen(
                sessionId = sessionId,
                initialPhotoType = photoType,
                onDone = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(
            route = "photo_viewer/{photoId}",
            arguments = listOf(navArgument("photoId") { type = NavType.LongType })
        ) { backStack ->
            val photoId = backStack.arguments!!.getLong("photoId")
            PhotoViewerScreen(
                photoId = photoId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
