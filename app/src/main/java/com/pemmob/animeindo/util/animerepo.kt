package com.pemmob.animeindo.util

import com.pemmob.animeindo.DTO.AnimeAPI
import com.pemmob.animeindo.network.AnimeInterface

/**
 * Domain model yang digunakan di layer UI / ViewModel.
 * Ini adalah representasi yang sudah dibersihkan dari DTO.
 *
 * Penambahan dari versi sebelumnya:
 * - episodes: untuk menampilkan jumlah episode di kartu anime
 * - status: untuk badge status (Airing, Finished, dll.)
 * - genres: untuk filter chip genre di HomeScreen
 * - synopsis: untuk halaman detail
 */
data class Anime(
    val malId: Int,
    val title: String,
    val titleEnglish: String? = null,
    val titleJapanese: String? = null,
    val type: String,
    val score: Double?,
    val imageUrl: String?,
    val episodes: Int?,
    val status: String?,
    val genres: List<String>,
    val synopsis: String?,
    val rank: Int? = null,
    val popularity: Int? = null,
    val members: Int? = null,
    val favorites: Int? = null,
    val year: Int? = null,
    val season: String? = null,
    val studios: List<String> = emptyList(),
    val source: String? = null,
    val rating: String? = null
)

/**
 * Repository yang menjadi satu-satunya sumber data untuk layer UI.
 * Berinteraksi dengan AnimeInterface (Retrofit) dan memetakan DTO ke domain model.
 */
class AnimeRepository(private val api: AnimeInterface) {

    /**
     * Ambil top anime dari Tenrai API.
     * Dipakai oleh HomeViewModel untuk bagian "Top Anime".
     */
    suspend fun getTopAnime(page: Int = 1): List<Anime> =
        api.getTopAnime(page = page).data.orEmpty().map { it.toAnime() }

    /**
     * Ambil anime musim sekarang dari Tenrai API.
     * Dipakai oleh HomeViewModel untuk bagian "Musim Ini".
     */
    suspend fun getSeasonNow(page: Int = 1): List<Anime> =
        api.getSeasonNow(page = page).data.orEmpty().map { it.toAnime() }

    /**
     * Cari anime berdasarkan keyword.
     * Dipakai oleh HomeViewModel ketika user mengetik di search bar.
     */
    suspend fun searchAnime(query: String, genreId: Int? = null, page: Int = 1): List<Anime> =
        api.searchAnime(query, genreId?.toString(), page).data.orEmpty().map { it.toAnime() }

    /**
     * Ambil detail lengkap anime berdasarkan MAL ID.
     * Dipakai oleh DetailViewModel untuk mengisi DetailScreen.
     */
    suspend fun getAnimeDetail(malId: Int): Anime? {
        val response = api.getAnimeDetail(malId)
        return response.data?.toAnime()
    }
}

/**
 * Extension function untuk memetakan AnimeAPI (DTO) ke Anime (domain model).
 * Dipisahkan agar AnimeRepository tetap bersih dan mudah diuji.
 */
private fun AnimeAPI.toAnime() = Anime(
    malId = malId,
    title = title ?: titleEnglish ?: "Tanpa judul",
    titleEnglish = titleEnglish,
    titleJapanese = titleJapanese,
    type = type ?: "-",
    score = score,
    imageUrl = images?.jpg?.largeImageUrl ?: images?.jpg?.imageUrl,
    episodes = episodes,
    status = status,
    genres = genres?.mapNotNull { it.name } ?: emptyList(),
    synopsis = synopsis,
    rank = rank,
    popularity = popularity,
    members = members,
    favorites = favorites,
    year = year,
    season = season,
    studios = studios?.mapNotNull { it.name } ?: emptyList(),
    source = source,
    rating = rating
)

