package com.pemmob.animeindo.ui.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pemmob.animeindo.util.Anime

// Palet Warna Design System Dark Neon (Konsisten dengan HomeScreen)
private val DarkBackground = Color(0xFF111222)
private val DarkSurface = Color(0xFF1D1E2F)
private val DarkStatCard = Color(0xFF261F54)
private val PrimaryBlue = Color(0xFF3B5BFE)
private val AccentCyan = Color(0xFF30C4FF)
private val AccentMagenta = Color(0xFFB844FF)
private val StarGold = Color(0xFFFFB800)
private val TextPrimary = Color(0xFFF2F3FF)
private val TextSecondary = Color(0xFFA0A3BD)
private val ChipBackground = Color(0xFF282A45)

/**
 * Composable utama Halaman Detail Anime.
 * Menggunakan ViewModel dan LazyColumn (Lazy Layout) untuk performa scroll yang optimal.
 */
@Composable
fun DetailScreen(
    malId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(factory = DetailViewModel.Factory(malId))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        when (val state = uiState) {
            is DetailUiState.Loading -> DetailLoadingView(onBackClick = onBackClick)
            is DetailUiState.Error -> DetailErrorView(
                message = state.message,
                onRetry = { viewModel.loadDetail() },
                onBackClick = onBackClick
            )
            is DetailUiState.Success -> DetailContentView(
                anime = state.anime,
                onBackClick = onBackClick
            )
        }
    }
}

/**
 * Tampilan Utama Detail Anime menggunakan LazyColumn (Lazy Layout).
 */
@Composable
private fun DetailContentView(
    anime: Anime,
    onBackClick: () -> Unit
) {
    var isBookmarked by remember { mutableStateOf(false) }
    var isSynopsisExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // ITEM 1: Hero Banner Header
            item {
                DetailHeroBanner(
                    imageUrl = anime.imageUrl,
                    title = anime.title
                )
            }

            // ITEM 2: Title, Subtitle, & Type Badge
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Type Badge (TV, Movie, OVA, dll)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryBlue.copy(alpha = 0.2f))
                                .border(1.dp, PrimaryBlue.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = anime.type.uppercase(),
                                color = AccentCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Score Badge
                        if (anime.score != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurface)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating Score",
                                    tint = StarGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%.1f", anime.score),
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = " / 10",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Judul Utama
                    Text(
                        text = anime.title,
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 30.sp
                    )

                    // Judul Bahasa Jepang / Inggris (jika ada)
                    anime.titleJapanese?.takeIf { it.isNotBlank() }?.let { titleJp ->
                        Text(
                            text = titleJp,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // ITEM 3: Quick Stats Cards Row (Episodes, Rank, Popularity, Status)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        label = "Episode",
                        value = anime.episodes?.toString() ?: "?",
                        icon = Icons.Default.PlayArrow,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Peringkat",
                        value = anime.rank?.let { "#$it" } ?: "-",
                        icon = Icons.Default.Star,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Popularitas",
                        value = anime.popularity?.let { "#$it" } ?: "-",
                        icon = Icons.Default.Favorite,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Status",
                        value = when (anime.status) {
                            "Currently Airing" -> "Airing"
                            "Finished Airing" -> "Tamat"
                            else -> anime.status ?: "-"
                        },
                        icon = Icons.Default.Info,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ITEM 4: Genre Chips (LazyRow di dalam item LazyColumn)
            if (anime.genres.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        Text(
                            text = "Genre",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                        ) {
                            items(anime.genres) { genre ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(ChipBackground)
                                        .border(1.dp, PrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = genre,
                                        color = AccentCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ITEM 5: Synopsis Section (Expandable)
            anime.synopsis?.takeIf { it.isNotBlank() }?.let { synText ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurface)
                            .padding(16.dp)
                            .animateContentSize()
                    ) {
                        Text(
                            text = "Sinopsis",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = synText,
                            color = TextSecondary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            maxLines = if (isSynopsisExpanded) Int.MAX_VALUE else 4,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isSynopsisExpanded) "Tampilkan Lebih Sedikit" else "Baca Selengkapnya",
                            color = PrimaryBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { isSynopsisExpanded = !isSynopsisExpanded }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }

            // ITEM 6: Informasi Detail Tambahan
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Informasi Tambahan",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    InfoRow(label = "Studio", value = anime.studios.joinToString().ifEmpty { "-" })
                    InfoRow(
                        label = "Musim / Tahun",
                        value = listOfNotNull(anime.season?.replaceFirstChar { it.uppercase() }, anime.year?.toString())
                            .joinToString(" ")
                            .ifEmpty { "-" }
                    )
                    InfoRow(label = "Sumber", value = anime.source ?: "-")
                    InfoRow(label = "Rating Umur", value = anime.rating ?: "-")
                    InfoRow(label = "Anggota MAL", value = anime.members?.let { "%,d".format(it) } ?: "-")
                    InfoRow(label = "Favorit", value = anime.favorites?.let { "%,d".format(it) } ?: "-")
                }
            }


            // Bottom Spacing agar pembaca nyaman
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Top Navigation Controls (Overlay Melayang di Atas Hero)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(DarkBackground.copy(alpha = 0.7f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = TextPrimary
                )
            }

            // Bookmark / Favorite Button
            IconButton(
                onClick = { isBookmarked = !isBookmarked },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(DarkBackground.copy(alpha = 0.7f))
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Simpan Anime",
                    tint = if (isBookmarked) AccentCyan else TextPrimary
                )
            }
        }
    }
}

/**
 * Hero Banner Poster dengan Efek Gradient Overlay Fade ke Bawah
 */
@Composable
private fun DetailHeroBanner(
    imageUrl: String?,
    title: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
    ) {
        // Poster Gambar Utama
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Fade ke Hitam/DarkBackground
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkBackground.copy(alpha = 0.4f),
                            Color.Transparent,
                            DarkBackground.copy(alpha = 0.8f),
                            DarkBackground
                        )
                    )
                )
        )
    }
}

/**
 * Component Chip Stats (Episode, Rank, Popularitas, Status)
 */
@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkStatCard)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = AccentCyan,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}

/**
 * Row Informasi Tambahan (Studio, Tahun, dll)
 */
@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.5f)
        )
    }
}

/**
 * View saat data sedang dimuat (Loading state).
 */
@Composable
private fun DetailLoadingView(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(DarkBackground.copy(alpha = 0.7f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = TextPrimary
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = PrimaryBlue)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Memuat Detail Anime...",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * View saat gagal memuat data (Error state).
 */
@Composable
private fun DetailErrorView(
    message: String,
    onRetry: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(DarkBackground.copy(alpha = 0.7f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = TextPrimary
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Gagal Memuat Data",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Coba Lagi")
            }
        }
    }
}
