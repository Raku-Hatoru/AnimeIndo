package com.pemmob.animeindo.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pemmob.animeindo.network.RetrofitClient
import com.pemmob.animeindo.util.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola state dan logik bisnis pada Halaman Detail Anime.
 *
 * @param malId ID Anime dari MyAnimeList / Tenrai API
 * @param repository Instance repository data anime
 */
class DetailViewModel(
    private val malId: Int,
    private val repository: AnimeRepository = AnimeRepository(RetrofitClient.instance)
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    /**
     * Memuat ulang data detail anime dari API.
     */
    fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val anime = repository.getAnimeDetail(malId)
                if (anime != null) {
                    _uiState.value = DetailUiState.Success(anime)
                } else {
                    _uiState.value = DetailUiState.Error("Detail anime tidak ditemukan.")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(
                    e.localizedMessage ?: "Terjadi kesalahan saat memuat detail anime."
                )
            }
        }
    }

    /**
     * Factory untuk membuat instance DetailViewModel dengan parameter malId.
     */
    class Factory(
        private val malId: Int,
        private val repository: AnimeRepository = AnimeRepository(RetrofitClient.instance)
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                return DetailViewModel(malId, repository) as T
            }
            throw IllegalArgumentException("ViewModel Class tidak dikenal: ${modelClass.name}")
        }
    }
}
