package com.pemmob.animeindo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pemmob.animeindo.ui.detail.DetailScreen
import com.pemmob.animeindo.ui.home.HomeScreen
import com.pemmob.animeindo.ui.theme.AnimeIndoTheme

/**
 * MainActivity adalah entry point utama aplikasi AnimeIndo.
 * Mengelola alur navigasi antar halaman menggunakan Navigation Compose (NavHost).
 *
 * Route yang tersedia:
 * - "home" : Menampilkan daftar anime utama (HomeScreen)
 * - "detail/{malId}" : Menampilkan detail lengkap anime yang dipilih (DetailScreen)
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimeIndoTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    // Route 1: Home Screen
                    composable("home") {
                        HomeScreen(
                            onAnimeClick = { malId ->
                                navController.navigate("detail/$malId")
                            }
                        )
                    }

                    // Route 2: Detail Screen
                    composable(
                        route = "detail/{malId}",
                        arguments = listOf(
                            navArgument("malId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val malId = backStackEntry.arguments?.getInt("malId") ?: return@composable
                        DetailScreen(
                            malId = malId,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}