package com.pemmob.animeindo.network

import com.pemmob.animeindo.DTO.AnimeAPI
import com.pemmob.animeindo.DTO.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @GET("top/anime")
    suspend fun getTopAnime(
        @Query("page") page: Int = 1
    ): ApiResponse<List<AnimeAPI>>

    // Mengambil detail satu anime berdasarkan ID
    @GET("anime/{id}")
    suspend fun getAnimeDetail(
        @Path("id") animeId: Int
    ): ApiResponse<AnimeAPI>
}