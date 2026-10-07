package com.pemmob.animeindo.DTO

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Bungkus utama untuk respon API (biasanya objek json memiliki key "data")
@Serializable
data class ApiResponse<T>(
    val data: T? = null
)

@Serializable
data class GenreDto(
    @SerialName("mal_id")
    val malId: Int,

    val name: String? = null,
    val type: String? = null,
    val url: String? = null
)

@Serializable
data class ImagesDto(
    val jpg: ImageUrlDto? = null,
    val webp: ImageUrlDto? = null
)

@Serializable
data class ImageUrlDto(
    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("small_image_url")
    val smallImageUrl: String? = null,

    @SerialName("large_image_url")
    val largeImageUrl: String? = null
)


// PERBAIKAN: Menggunakan kurung biasa ( ) untuk properti data class
@Serializable
data class AnimeAPI(
    @SerialName("mal_id")
    val malId: Int,

    val title: String? = null,
    val type: String? = null,
    val score: Double? = null,
    val episodes: Int? = null,
    val status: String? = null,
    val synopsis: String? = null,
    val genres: List<GenreDto>? = null,
    val images: ImagesDto? = null
)
