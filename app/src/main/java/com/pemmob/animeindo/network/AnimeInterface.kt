package com.pemmob.animeindo.network

import com.pemmob.animeindo.DTO.AnimeAPI
import com.pemmob.animeindo.DTO.AnimeSingleResponse
import com.pemmob.animeindo.DTO.AnimeListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface Retrofit untuk Tenrai API (https://api.tenrai.org/v1/).
 *
 * Perubahan dari versi sebelumnya:
 * - getTopAnime & searchAnime kini return AnimeListResponse (ada pagination)
 * - getAnimeDetail kini return AnimeSingleResponse (wrapper untuk single object)
 * - Ditambahkan getSeasonNow untuk mengambil anime musim sekarang
 */
interface AnimeInterface {

    /**
     * Ambil daftar anime terpopuler.
     * Endpoint: GET /v1/top/anime
     */
    @GET("top/anime")
    suspend fun getTopAnime(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): AnimeListResponse

    /**
     * Ambil detail satu anime berdasarkan MAL ID.
     * Endpoint: GET /v1/anime/{id}
     */
    @GET("anime/{id}")
    suspend fun getAnimeDetail(
        @Path("id") animeId: Int
    ): AnimeSingleResponse

    /**
     * Cari anime berdasarkan keyword atau genre.
     * Endpoint: GET /v1/anime?q=...
     */
    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String? = null,
        @Query("genres") genres: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): AnimeListResponse

    /**
     * Ambil anime yang sedang tayang musim ini.
     * Endpoint: GET /v1/seasons/now
     */
    @GET("seasons/now")
    suspend fun getSeasonNow(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): AnimeListResponse
}