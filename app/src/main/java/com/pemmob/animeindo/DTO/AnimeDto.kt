package com.pemmob.animeindo.DTO

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO baru yang lebih lengkap untuk menampung data dari Tenrai API.
 * Menggantikan field-field yang kurang pada AnimeAPI di UserRequest.kt.
 * Field ini sesuai dengan response dari endpoint:
 *   - GET /v1/top/anime
 *   - GET /v1/seasons/now
 *   - GET /v1/anime?q=...
 */

@Serializable
data class PaginationDto(
    @SerialName("last_visible_page")
    val lastVisiblePage: Int = 1,

    @SerialName("has_next_page")
    val hasNextPage: Boolean = false,

    @SerialName("current_page")
    val currentPage: Int = 1,

    val items: PaginationItemsDto? = null
)

@Serializable
data class PaginationItemsDto(
    val count: Int = 0,
    val total: Int = 0,

    @SerialName("per_page")
    val perPage: Int = 25
)

/**
 * Wrapper API response yang menyertakan pagination.
 * Dipakai untuk endpoint yang mengembalikan list (top, seasons, search).
 */
@Serializable
data class AnimeListResponse(
    val pagination: PaginationDto? = null,
    val data: List<AnimeAPI>? = null
)

/**
 * Wrapper API response untuk single object (detail anime).
 * Dipakai untuk endpoint GET /v1/anime/{id}.
 */
@Serializable
data class AnimeSingleResponse(
    val data: AnimeAPI? = null
)
