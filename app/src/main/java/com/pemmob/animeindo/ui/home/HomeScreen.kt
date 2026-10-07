package com.pemmob.animeindo.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pemmob.animeindo.util.Anime

// ─────────────────────────────────────────────
// Warna desain (sesuai design system Stitch)
// ─────────────────────────────────────────────
private val ColorBackground = Color(0xFF111222)
private val ColorSurface = Color(0xFF1D1E2F)
private val ColorSurfaceHigh = Color(0xFF261F54)
private val ColorPrimary = Color(0xFF3B5BFE)
private val ColorCyan = Color(0xFF30C4FF)
private val ColorMagenta = Color(0xFFB844FF)
private val ColorStarRating = Color(0xFFFFB800)
private val ColorTextPrimary = Color(0xFFF2F3FF)
private val ColorTextSecondary = Color(0xFFA7ABCF)
private val ColorTextMuted = Color(0xFF6E729E)
private val ColorOutline = Color(0xFF444656)

// ─────────────────────────────────────────────
// HomeScreen — Entry point composable
// ─────────────────────────────────────────────

/**
 * HomeScreen adalah composable utama yang menampilkan:
 * - Search bar di bagian atas
 * - Section "Musim Ini" (horizontal scroll)
 * - Section "Top Anime" (vertical list)
 * - Hasil pencarian (jika user sedang mencari)
 *
 * @param onAnimeClick Callback saat user menekan kartu anime (untuk navigasi ke detail).
 * @param viewModel    ViewModel yang menyediakan data dan logika UI.
 */
@Composable
fun HomeScreen(
    onAnimeClick: (Int) -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    // Collect StateFlow dengan lifecycle-aware untuk menghindari memory leak
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> HomeLoadingContent()
            is HomeUiState.Error -> HomeErrorContent(
                message = state.message,
                onRetry = viewModel::retry
            )
            is HomeUiState.Success -> HomeSuccessContent(
                state = state,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onAnimeClick = onAnimeClick
            )
        }
    }
}

// ─────────────────────────────────────────────
// Loading State
// ─────────────────────────────────────────────

@Composable
private fun HomeLoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = ColorPrimary,
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Memuat data anime...",
                color = ColorTextSecondary,
                fontSize = 14.sp
            )
        }
    }
}

// ─────────────────────────────────────────────
// Error State
// ─────────────────────────────────────────────

@Composable
private fun HomeErrorContent(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "⚠️ Gagal memuat data",
                color = ColorTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = ColorTextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
            ) {
                Text("Coba Lagi", color = Color.White)
            }
        }
    }
}

// ─────────────────────────────────────────────
// Success State — Konten Utama
// ─────────────────────────────────────────────

@Composable
private fun HomeSuccessContent(
    state: HomeUiState.Success,
    onSearchQueryChange: (String) -> Unit,
    onAnimeClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Header ──────────────────────────────────
        item {
            HomeHeader()
        }

        // ── Search Bar ──────────────────────────────
        item {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // ── Tampilan Hasil Pencarian ─────────────────
        if (state.isSearching) {
            item {
                SectionTitle(
                    title = if (state.searchResults == null) {
                        "Mencari..."
                    } else {
                        "Hasil untuk \"${state.searchQuery}\" (${state.searchResults.size})"
                    }
                )
            }

            if (state.searchResults != null) {
                if (state.searchResults.isEmpty()) {
                    item {
                        EmptySearchResult(query = state.searchQuery)
                    }
                } else {
                    items(state.searchResults, key = { it.malId }) { anime ->
                        AnimeListItem(anime = anime, onClick = { onAnimeClick(anime.malId) })
                    }
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ColorPrimary, strokeWidth = 2.dp)
                    }
                }
            }

            return@LazyColumn
        }

        // ── Section: Musim Ini ───────────────────────
        if (state.seasonNowList.isNotEmpty()) {
            item {
                SectionTitle(title = "🌸 Musim Ini")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    items(state.seasonNowList, key = { it.malId }) { anime ->
                        AnimeCard(anime = anime, onClick = { onAnimeClick(anime.malId) })
                    }
                }
            }
        }

        // ── Section: Top Anime ───────────────────────
        if (state.topAnimeList.isNotEmpty()) {
            item {
                SectionTitle(title = "🏆 Top Anime")
            }
            items(state.topAnimeList, key = { it.malId }) { anime ->
                AnimeListItem(anime = anime, onClick = { onAnimeClick(anime.malId) })
            }
        }
    }
}

// ─────────────────────────────────────────────
// Komponen: Header
// ─────────────────────────────────────────────

@Composable
private fun HomeHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(ColorSurfaceHigh, ColorBackground)
                )
            )
            .padding(start = 20.dp, end = 20.dp, top = 52.dp, bottom = 16.dp)
    ) {
        Text(
            text = "AnimeIndo",
            color = ColorPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Temukan anime favoritmu",
            color = ColorTextMuted,
            fontSize = 14.sp
        )
    }
}

// ─────────────────────────────────────────────
// Komponen: Search Bar
// ─────────────────────────────────────────────

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text("Cari anime...", color = ColorTextMuted)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Cari",
                tint = ColorCyan
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Hapus pencarian",
                        tint = ColorTextMuted
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { keyboardController?.hide() }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = ColorSurface,
            unfocusedContainerColor = ColorSurface,
            focusedBorderColor = ColorPrimary,
            unfocusedBorderColor = ColorOutline,
            focusedTextColor = ColorTextPrimary,
            unfocusedTextColor = ColorTextPrimary,
            cursorColor = ColorPrimary
        )
    )
}

// ─────────────────────────────────────────────
// Komponen: Section Title
// ─────────────────────────────────────────────

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = ColorTextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp)
    )
}

// ─────────────────────────────────────────────
// Komponen: Anime Card (Horizontal Scroll)
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeCard(anime: Anime, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(140.dp)
            .height(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Poster image
            AsyncImage(
                model = anime.imageUrl,
                contentDescription = anime.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient overlay bawah untuk keterbacaan judul
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xDD0D0E1E))
                        )
                    )
            )

            // Badge score di kanan atas
            if (anime.score != null) {
                ScoreBadge(
                    score = anime.score,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }

            // Badge tipe anime (TV, Movie, dll.)
            anime.type.let { type ->
                if (type.isNotBlank() && type != "-") {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0xCC0D0E1E)
                    ) {
                        Text(
                            text = type,
                            color = ColorCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Judul di bawah
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = anime.title,
                    color = ColorTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// Komponen: Anime List Item (Vertical List)
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeListItem(anime: Anime, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Poster kiri
            AsyncImage(
                model = anime.imageUrl,
                contentDescription = anime.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(76.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )

            // Info kanan
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = anime.title,
                    color = ColorTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Genre chips (maks 2)
                if (anime.genres.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(anime.genres.take(2)) { genre ->
                            GenreChip(genre = genre)
                        }
                    }
                }

                // Row bawah: score + episode + status
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Score
                    if (anime.score != null) {
                        ScoreBadge(score = anime.score)
                    }

                    // Episode
                    if (anime.episodes != null) {
                        Text(
                            text = "${anime.episodes} ep",
                            color = ColorTextMuted,
                            fontSize = 11.sp
                        )
                    }

                    // Status badge
                    if (!anime.status.isNullOrBlank()) {
                        StatusBadge(status = anime.status)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Komponen: Score Badge
// ─────────────────────────────────────────────

@Composable
private fun ScoreBadge(score: Double, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        color = Color(0xBB0D0E1E)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = ColorStarRating,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = String.format("%.2f", score),
                color = ColorTextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─────────────────────────────────────────────
// Komponen: Genre Chip
// ─────────────────────────────────────────────

@Composable
private fun GenreChip(genre: String) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = ColorMagenta.copy(alpha = 0.15f)
    ) {
        Text(
            text = genre,
            color = ColorMagenta,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

// ─────────────────────────────────────────────
// Komponen: Status Badge
// ─────────────────────────────────────────────

@Composable
private fun StatusBadge(status: String) {
    val (bgColor, textColor) = when {
        status.contains("Airing", ignoreCase = true) -> ColorCyan.copy(alpha = 0.15f) to ColorCyan
        status.contains("Finished", ignoreCase = true) -> ColorOutline.copy(alpha = 0.4f) to ColorTextMuted
        else -> ColorPrimary.copy(alpha = 0.15f) to ColorPrimary
    }

    Surface(
        shape = RoundedCornerShape(50.dp),
        color = bgColor
    ) {
        Text(
            text = status.take(12),
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

// ─────────────────────────────────────────────
// Komponen: Empty Search Result
// ─────────────────────────────────────────────

@Composable
private fun EmptySearchResult(query: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🔍", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tidak ada hasil untuk",
                color = ColorTextSecondary,
                fontSize = 14.sp
            )
            Text(
                text = "\"$query\"",
                color = ColorTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
