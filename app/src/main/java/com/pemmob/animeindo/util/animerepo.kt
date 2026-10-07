package com.pemmob.animeindo.util

import com.pemmob.animeindo.DTO.AnimeAPI
import com.pemmob.animeindo.network.AnimeInterface

data class Anime(
    val malId: Int,
    val title: String,
    val type: String,
    val score: Double?,
    val imageUrl: String?
)

class AnimeRepository(private val api: AnimeInterface) {

    suspend fun searchAnime(query: String, genreId: Int? = null): List<Anime> =
        api.searchAnime(query, genreId?.toString()).data.orEmpty().map { it.toAnime() }
}

private fun AnimeAPI.toAnime() = Anime(
    malId = malId,
    title = title ?: "Tanpa judul",
    type = type ?: "-",
    score = score,
    imageUrl = images?.jpg?.imageUrl
)
