package com.pemmob.animeindo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pemmob.animeindo.network.RetrofitClient
import com.pemmob.animeindo.ui.theme.AnimeIndoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimeIndoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LaunchedEffect(Unit) {
                        try {
                            val response = RetrofitClient.instance.getTopAnime()
                            Log.d("AnimeIndo API", "Berhasil mengambil data: ${response.data?.size} anime. Contoh pertama: ${response.data?.firstOrNull()?.title}")

                            val repository = com.pemmob.animeindo.util.AnimeRepository(RetrofitClient.instance)
                            val searchResults = repository.searchAnime("Naruto")
                            Log.d("AnimeIndo Repo", "Berhasil pencarian anime via AnimeRepository: ${searchResults.size} hasil. Anime pertama: ${searchResults.firstOrNull()?.title} (Score: ${searchResults.firstOrNull()?.score})")
                        } catch (e: Exception) {
                            Log.e("AnimeIndo API", "Gagal mengambil data: ${e.message}", e)
                        }
                    }
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AnimeIndoTheme {
        Greeting("Android")
    }
}