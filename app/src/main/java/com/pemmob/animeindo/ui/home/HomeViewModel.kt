package com.pemmob.animeindo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.animeindo.network.RetrofitClient
import com.pemmob.animeindo.util.AnimeRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * HomeViewModel bertanggung jawab untuk:
 * 1. Mengambil data Top Anime dan Season Now saat layar dibuka.
 * 2. Mengelola state pencarian dengan debounce 400ms agar tidak spamming API.
 * 3. Mengekspos satu StateFlow<HomeUiState> ke HomeScreen.
 *
 * Alasan menggunakan ViewModel:
 * - Data tetap bertahan saat konfigurasi berubah (rotasi layar, dll.).
 * - Memisahkan logika bisnis dari Composable agar mudah diuji.
 *
 * Alasan menggunakan StateFlow (bukan LiveData):
 * - Lebih idiomatik dengan Kotlin Coroutines dan Compose.
 * - Mendukung debounce dan operator Flow lainnya secara native.
 */
@OptIn(FlowPreview::class)
class HomeViewModel(
    private val repository: AnimeRepository = AnimeRepository(RetrofitClient.instance)
) : ViewModel() {

    // StateFlow internal yang bisa diubah oleh ViewModel
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    // StateFlow publik yang hanya bisa dibaca oleh HomeScreen
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Flow terpisah untuk menyimpan query pencarian user
    private val _searchQuery = MutableStateFlow("")

    init {
        // Load data utama saat ViewModel pertama kali dibuat
        loadHomeData()

        // Mulai observasi perubahan query pencarian dengan debounce
        observeSearchQuery()
    }

    /**
     * Mengambil Top Anime dan Season Now secara paralel.
     * Keduanya dijalankan bersamaan menggunakan coroutine scope.
     */
    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // Jalankan kedua request secara sekuensial untuk menjaga kesederhanaan.
                // Bisa diubah ke async/await untuk paralel jika performa dibutuhkan.
                val topAnime = repository.getTopAnime()
                val seasonNow = repository.getSeasonNow()

                _uiState.value = HomeUiState.Success(
                    topAnimeList = topAnime,
                    seasonNowList = seasonNow
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    message = e.message ?: "Terjadi kesalahan saat memuat data."
                )
            }
        }
    }

    /**
     * Observasi perubahan _searchQuery.
     * - drop(1): lewati nilai awal (string kosong) agar tidak trigger search saat buka.
     * - debounce(400ms): tunggu 400ms setelah user berhenti mengetik sebelum kirim request.
     * - distinctUntilChanged: abaikan jika query tidak berubah.
     */
    private fun observeSearchQuery() {
        _searchQuery
            .drop(1)
            .debounce(400L)
            .distinctUntilChanged()
            .onEach { query -> performSearch(query) }
            .launchIn(viewModelScope)
    }

    /**
     * Dipanggil dari HomeScreen setiap kali nilai search bar berubah.
     * Hanya update query — pencarian aktual ditangani oleh observeSearchQuery().
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query

        // Update state secara langsung untuk menampilkan query terbaru di UI
        // tanpa menunggu debounce selesai.
        _uiState.update { current ->
            if (current is HomeUiState.Success) {
                current.copy(
                    searchQuery = query,
                    isSearching = query.isNotBlank()
                )
            } else current
        }
    }

    /**
     * Eksekusi pencarian ke API.
     * Jika query kosong, tampilkan kembali data home normal.
     */
    private fun performSearch(query: String) {
        if (query.isBlank()) {
            // User menghapus semua teks — kembali ke tampilan home
            _uiState.update { current ->
                if (current is HomeUiState.Success) {
                    current.copy(
                        searchResults = null,
                        isSearching = false
                    )
                } else current
            }
            return
        }

        viewModelScope.launch {
            // Tampilkan indikator loading pencarian di state
            _uiState.update { current ->
                if (current is HomeUiState.Success) {
                    current.copy(isSearching = true)
                } else current
            }

            try {
                val results = repository.searchAnime(query)
                _uiState.update { current ->
                    if (current is HomeUiState.Success) {
                        current.copy(
                            searchResults = results,
                            isSearching = true
                        )
                    } else current
                }
            } catch (e: Exception) {
                // Error pencarian tidak menggantikan seluruh state,
                // cukup tampilkan list kosong agar UX tidak rusak.
                _uiState.update { current ->
                    if (current is HomeUiState.Success) {
                        current.copy(
                            searchResults = emptyList(),
                            isSearching = true
                        )
                    } else current
                }
            }
        }
    }

    /**
     * Dipanggil saat user menekan tombol "Coba Lagi" di state Error.
     */
    fun retry() {
        loadHomeData()
    }
}
