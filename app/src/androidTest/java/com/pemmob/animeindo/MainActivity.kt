package com.pemmob.animeindo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.pemmob.animeindo.network.RetrofitInstance
import com.pemmob.animeindo.ui.theme.AnimeIndoTheme // Sesuaikan dengan nama theme proyek Anda

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState: Bundle?)
        setContent {
            AnimeIndoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(text = "Memuat data tes... Cek Logcat!")

                    // === MASUKKAN KODE TES DI SINI ===
                    // LaunchedEffect akan otomatis berjalan sekali saat layar pertama kali muncul
                    LaunchedEffect(Unit) {
                        try {
                            val response = RetrofitInstance.apiService.getTopAnime()
                            Log.d("TEST_ANIME", "Data Berhasil: $response")
                        } catch (e: Exception) {
                            Log.e("TEST_ANIME", "Error saat mengambil data: ${e.message}", e)
                        }
                    }
                }
            }
        }
    }
}
