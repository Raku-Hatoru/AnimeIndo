package com.pemmob.animeindo.ui.detail

import com.pemmob.animeindo.util.Anime

/**
 * State UI untuk Halaman Detail Anime.
 * Mengikuti pola Unidirectional Data Flow (UDF).
 */
sealed interface DetailUiState {
    /**
     * State saat data detail anime sedang diunduh dari API.
     */
    data object Loading : DetailUiState

    /**
     * State saat data detail anime berhasil dimuat.
     * @param anime Objek domain Anime berisi detail lengkap.
     */
    data class Success(val anime: Anime) : DetailUiState

    /**
     * State saat terjadi kegagalan muat data.
     * @param message Pesan deskriptif mengenai error yang terjadi.
     */
    data class Error(val message: String) : DetailUiState
}
