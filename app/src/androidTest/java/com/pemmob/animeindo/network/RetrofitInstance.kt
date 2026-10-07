package com.pemmob.animeindo.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitInstance {

    // URL dasar API (Contoh menggunakan Jikan API v4 untuk MyAnimeList)
    private const val BASE_URL = "https://jikan.moe"

    // Konfigurasi JSON agar mengabaikan key baru/tidak dikenal yang ada di API
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    // Pembuatan instance Retrofit
    private val retrofit: Retrofit by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    // Expose apiService agar bisa dipanggil dari UI/Repository
    val apiService: APIService by lazy {
        retrofit.create(APIService::class.java)
    }
}
