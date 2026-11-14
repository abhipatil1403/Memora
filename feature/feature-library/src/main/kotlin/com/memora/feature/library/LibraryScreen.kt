package com.memora.feature.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.memora.core.model.Category
import com.memora.core.model.Memory
import com.memora.core.ui.components.MemoraConfidenceBadge
import com.memora.core.ui.theme.MemoraTheme
import kotlinx.coroutines.delay

@Composable
fun LibraryScreen(
    collectionType: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToMemoryDetail: (String) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    LaunchedEffect(collectionType) {
        viewModel.setInitialCollection(collectionType)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MemoraTheme.colors

    val screenTitle = state.selectedCollectionType?.let { "${it.displayName} Collection" } ?: "Library"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MemoraTheme.spacing.space2, vertical = MemoraTheme.spacing.space2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = colors.textPrimary)
            }
            Text(
                text = screenTitle,
                style = MemoraTheme.typography.title,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Active collection indicator banner
        if (state.selectedCollectionType != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MemoraTheme.spacing.space4, vertical = MemoraTheme.spacing.space1),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(MemoraTheme.shapes.full)
                        .background(colors.primary.copy(alpha = 0.12f))
                        .padding(horizontal = MemoraTheme.spacing.space3, vertical = MemoraTheme.spacing.space1),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtered by: ${state.selectedCollectionType?.displayName}",
                        style = MemoraTheme.typography.caption,
                        color = colors.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "Clear Filter",
                    style = MemoraTheme.typography.caption,
                    color = colors.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { viewModel.clearCollectionFilter() }
                        .padding(MemoraTheme.spacing.space2)
                )
            }
            Spacer(modifier = Modifier.height(MemoraTheme.spacing.space2))
        }

        // Category filter chips
        val categories = listOf(null) + Category.entries.toList()
        LazyRow(
            modifier = Modifier.padding(bottom = MemoraTheme.spacing.space4),
            contentPadding = PaddingValues(horizontal = MemoraTheme.spacing.space4),
            horizontalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space2)
        ) {
            items(categories) { category ->
                val isSelected = state.selectedCategory == category
                Text(
                    text = category?.displayName ?: "All",
                    style = MemoraTheme.typography.bodySmall,
                    color = if (isSelected) colors.onPrimary else colors.textSecondary,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(MemoraTheme.shapes.full)
                        .background(if (isSelected) colors.primary else colors.surfaceVariant)
                        .clickable { viewModel.filterByCategory(category) }
                        .padding(horizontal = MemoraTheme.spacing.space4, vertical = MemoraTheme.spacing.space2)
                )
            }
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.primary, strokeWidth = 2.dp)
            }
        } else if (state.filteredMemories.isEmpty()) {
            // Empty state for filtered collection/category
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MemoraTheme.spacing.space6),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(colors.primary.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.FolderOpen,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))
                    Text(
                        text = if (state.selectedCollectionType != null) {
                            "No memories in ${state.selectedCollectionType?.displayName}"
                        } else if (state.selectedCategory != null) {
                            "No memories in ${state.selectedCategory?.displayName}"
                        } else {
                            "No memories yet"
                        },
                        style = MemoraTheme.typography.title,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(MemoraTheme.spacing.space2))
                    Text(
                        text = if (state.selectedCollectionType != null) {
                            "Captured items matching ${state.selectedCollectionType?.displayName} will appear here automatically."
                        } else {
                            "Start capturing photos, receipts, and notes to build your library."
                        },
                        style = MemoraTheme.typography.body,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                    if (state.selectedCollectionType != null || state.selectedCategory != null) {
                        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))
                        Text(
                            text = "View All Memories",
                            style = MemoraTheme.typography.bodySmall,
                            color = colors.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(MemoraTheme.shapes.full)
                                .background(colors.primary.copy(alpha = 0.1f))
                                .clickable {
                                    viewModel.clearCollectionFilter()
                                    viewModel.filterByCategory(null)
                                }
                                .padding(horizontal = MemoraTheme.spacing.space4, vertical = MemoraTheme.spacing.space2)
                        )
                    }
                }
            }
        } else {
            // Memory grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = MemoraTheme.spacing.space4),
                horizontalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space3),
                verticalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space3),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(state.filteredMemories) { index, memory ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { delay(index * 30L); visible = true }

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(200)) + slideInVertically(
                            initialOffsetY = { it / 4 },
                            animationSpec = tween(200)
                        )
                    ) {
                        LibraryCard(
                            memory = memory,
                            onClick = { onNavigateToMemoryDetail(memory.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryCard(memory: Memory, onClick: () -> Unit) {
    val colors = MemoraTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MemoraTheme.shapes.large)
            .background(colors.surface)
            .border(1.dp, colors.outlineVariant, MemoraTheme.shapes.large)
            .clickable(onClick = onClick)
            .padding(MemoraTheme.spacing.space3)
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(MemoraTheme.shapes.medium)
                .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (memory.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = memory.imageUrl,
                    contentDescription = memory.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = memory.category.displayName.first().toString(),
                    style = MemoraTheme.typography.title,
                    color = colors.primary.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space3))

        // Category
        Text(
            text = memory.category.displayName,
            style = MemoraTheme.typography.caption,
            color = colors.primary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space1))

        // Title
        Text(
            text = memory.title,
            style = MemoraTheme.typography.bodySmall,
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space2))

        MemoraConfidenceBadge(confidence = memory.overallConfidence)
    }
}
