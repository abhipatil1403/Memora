package com.memora.feature.home

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.memora.core.model.Memory
import com.memora.core.model.SmartCollection
import com.memora.core.ui.components.MemoraConfidenceBadge
import com.memora.core.ui.theme.MemoraTheme
import kotlinx.coroutines.delay

/**
 * Maps icon name strings from the data layer to Material [ImageVector] icons.
 */
private fun mapCollectionIcon(iconName: String): ImageVector {
    return when (iconName) {
        "calendar_today" -> Icons.Outlined.CalendarToday
        "date_range" -> Icons.Outlined.DateRange
        "school" -> Icons.Outlined.School
        "receipt_long" -> Icons.AutoMirrored.Outlined.ReceiptLong
        "star" -> Icons.Outlined.Star
        "favorite" -> Icons.Outlined.Favorite
        else -> Icons.Outlined.Star
    }
}

@Composable
fun HomeScreen(
    onNavigateToCapture: () -> Unit,
    onNavigateToMemoryDetail: (String) -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCollections: () -> Unit = onNavigateToLibrary,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MemoraTheme.colors

    Scaffold(
        modifier = modifier,
        containerColor = colors.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCapture,
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Capture")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colors.primary, strokeWidth = 2.dp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(top = MemoraTheme.spacing.space4),
                verticalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space6)
            ) {
                // Header
                item {
                    HomeHeader(
                        greeting = state.greeting,
                        totalMemories = state.totalMemories,
                        onSearchClick = onNavigateToSearch,
                        onProfileClick = onNavigateToProfile
                    )
                }

                // Smart Collections
                item {
                    SmartCollectionsRow(
                        collections = state.collections,
                        onCollectionClick = { onNavigateToCollections() }
                    )
                }

                // Section header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MemoraTheme.spacing.space4),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent",
                            style = MemoraTheme.typography.heading,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "See All",
                            style = MemoraTheme.typography.bodySmall,
                            color = colors.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onNavigateToLibrary() }
                        )
                    }
                }

                // Empty state if no memories
                if (state.recentMemories.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MemoraTheme.spacing.space4)
                                .clip(MemoraTheme.shapes.large)
                                .background(colors.surfaceVariant.copy(alpha = 0.5f))
                                .padding(MemoraTheme.spacing.space6),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Outlined.CameraAlt,
                                    contentDescription = null,
                                    tint = colors.primary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(Modifier.height(MemoraTheme.spacing.space3))
                                Text(
                                    text = "No memories saved yet",
                                    style = MemoraTheme.typography.subheading,
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(MemoraTheme.spacing.space1))
                                Text(
                                    text = "Tap the + button below to scan your first document, receipt, or note!",
                                    style = MemoraTheme.typography.bodySmall,
                                    color = colors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Recent Memories list
                itemsIndexed(state.recentMemories) { index, memory ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(index * 60L)
                        visible = true
                    }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(250)) + slideInVertically(
                            initialOffsetY = { it / 4 },
                            animationSpec = tween(250)
                        )
                    ) {
                        MemoryCard(
                            memory = memory,
                            onClick = { onNavigateToMemoryDetail(memory.id) },
                            modifier = Modifier.padding(horizontal = MemoraTheme.spacing.space4)
                        )
                    }
                }

                // Bottom spacer for FAB
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    greeting: String,
    totalMemories: Int,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val colors = MemoraTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MemoraTheme.spacing.space4),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = greeting,
                style = MemoraTheme.typography.body,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$totalMemories memories",
                style = MemoraTheme.typography.title,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onSearchClick) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = colors.textSecondary
                )
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = "Profile",
                    tint = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun SmartCollectionsRow(
    collections: List<SmartCollection>,
    onCollectionClick: (SmartCollection) -> Unit
) {
    val colors = MemoraTheme.colors

    Column(modifier = Modifier.padding(start = MemoraTheme.spacing.space4)) {
        Text(
            text = "Collections",
            style = MemoraTheme.typography.heading,
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space3))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space2)
        ) {
            items(collections) { collection ->
                CollectionChip(collection = collection, onClick = { onCollectionClick(collection) })
            }
        }
    }
}

@Composable
private fun CollectionChip(
    collection: SmartCollection,
    onClick: () -> Unit
) {
    val colors = MemoraTheme.colors
    val icon = mapCollectionIcon(collection.icon)

    Row(
        modifier = Modifier
            .clip(MemoraTheme.shapes.full)
            .background(colors.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = MemoraTheme.spacing.space4, vertical = MemoraTheme.spacing.space2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(MemoraTheme.spacing.space2))
        Text(
            text = collection.name,
            style = MemoraTheme.typography.bodySmall,
            color = colors.textPrimary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(MemoraTheme.spacing.space1))
        Text(
            text = "${collection.count}",
            style = MemoraTheme.typography.caption,
            color = colors.textSecondary
        )
    }
}

@Composable
private fun MemoryCard(
    memory: Memory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MemoraTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MemoraTheme.shapes.large)
            .background(colors.surface)
            .border(1.dp, colors.outlineVariant, MemoraTheme.shapes.large)
            .clickable(onClick = onClick)
            .padding(MemoraTheme.spacing.space4)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space3)
        ) {
            // Real Image Thumbnail
            if (memory.imageUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(MemoraTheme.shapes.medium)
                        .background(colors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = memory.imageUrl,
                        contentDescription = memory.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                // Category pill + Confidence
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = memory.category.displayName,
                        style = MemoraTheme.typography.caption,
                        color = colors.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .background(
                                colors.primaryContainer,
                                shape = MemoraTheme.shapes.small
                            )
                            .padding(horizontal = MemoraTheme.spacing.space2, vertical = 2.dp)
                    )
                    MemoraConfidenceBadge(confidence = memory.overallConfidence)
                }

                Spacer(modifier = Modifier.height(MemoraTheme.spacing.space2))

                // Real Title
                Text(
                    text = memory.title,
                    style = MemoraTheme.typography.subheading,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Real Summary
                Text(
                    text = memory.summary,
                    style = MemoraTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Dynamic Real Tags row (cleaned up)
        val validTags = memory.tags.filter { it.value.isNotBlank() && it.value != "ai-extracted" }.take(3)
        if (validTags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(MemoraTheme.spacing.space3))
            Row(horizontalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space2)) {
                validTags.forEach { tag ->
                    val tagText = if (tag.value.startsWith("#")) tag.value else "#${tag.value}"
                    Text(
                        text = tagText,
                        style = MemoraTheme.typography.caption,
                        color = colors.secondary,
                        modifier = Modifier
                            .background(
                                colors.secondary.copy(alpha = 0.08f),
                                shape = MemoraTheme.shapes.small
                            )
                            .padding(horizontal = MemoraTheme.spacing.space2, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
