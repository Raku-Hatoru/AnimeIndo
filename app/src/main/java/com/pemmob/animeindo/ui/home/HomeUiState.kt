package com.pemmob.animeindo.ui.home

import com.pemmob.animeindo.util.Anime

/**
 * HomeUiState merepresentasikan semua kemungkinan kondisi tampilan HomeScreen.
 *
 * Alasan menggunakan sealed interface (bukan data class biasa):
 * - Setiap kondisi UI (Loading, Success, Error) punya struktur data berbeda.
 * - Compiler Kotlin bisa memastikan semua kasus ditangani di `when` expression.
 * - Lebih idiomatik untuk pattern MVVM + Compose.
 */
sealed interface HomeUiState {

    /**
     * State saat data sedang dimuat dari API.
     * HomeScreen akan menampilkan skeleton / shimmer loading.
     */
    data object Loading : HomeUiState

    /**
     * State saat data berhasil dimuat.
     *
     * @param topAnimeList  Daftar anime terpopuler (dari GET /v1/top/anime).
     * @param seasonNowList Daftar anime yang sedang tayang musim ini (dari GET /v1/seasons/now).
     * @param searchResults Daftar hasil pencarian; null jika user belum mencari.
     * @param searchQuery   Kata kunci pencarian aktif saat ini.
     * @param isSearching   True jika user sedang dalam mode pencarian (query tidak kosong).
     */
    data class Success(
        val topAnimeList: List<Anime> = emptyList(),
        val seasonNowList: List<Anime> = emptyList(),
        val searchResults: List<Anime>? = null,
        val searchQuery: String = "",
        val isSearching: Boolean = false
    ) : HomeUiState

    /**
     * State saat terjadi error (network error, server error, dsb.).
     *
     * @param message Pesan error yang ditampilkan ke user.
     */
    data class Error(val message: String) : HomeUiState
}
