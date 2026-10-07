package com.pemmob.animeindo.network

import com.pemmob.animeindo.DTO.AnimeAPI
import com.pemmob.animeindo.DTO.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeInterface {
    @GET("top/anime")
    suspend fun getTopAnime(
        @Query("page") page: Int = 1
    ): ApiResponse<List<AnimeAPI>>

    @GET("anime/{id}")
    suspend fun getAnimeDetail(
        @Path("id") animeId: Int
    ): ApiResponse<AnimeAPI>

    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String? = null,
        @Query("genres") genres: String? = null,
        @Query("page") page: Int = 1
    ): ApiResponse<List<AnimeAPI>>
}