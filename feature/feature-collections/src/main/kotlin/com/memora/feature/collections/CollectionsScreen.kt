package com.memora.feature.collections

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Work
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memora.core.model.SmartCollection
import com.memora.core.ui.theme.MemoraTheme
import kotlinx.coroutines.delay

/**
 * Maps icon name strings to Material [ImageVector] icons.
 */
private fun mapCollectionIcon(iconName: String): ImageVector {
    return when (iconName) {
        "calendar_today" -> Icons.Outlined.CalendarToday
        "date_range" -> Icons.Outlined.DateRange
        "school" -> Icons.Outlined.School
        "description" -> Icons.Outlined.Description
        "receipt_long" -> Icons.Outlined.ReceiptLong
        "work" -> Icons.Outlined.Work
        "local_hospital" -> Icons.Outlined.LocalHospital
        "star" -> Icons.Outlined.Star
        "favorite" -> Icons.Outlined.Favorite
        "archive" -> Icons.Outlined.Archive
        else -> Icons.Outlined.Star
    }
}

@Composable
fun CollectionsScreen(
    onNavigateBack: () -> Unit,
    onCollectionClick: (String) -> Unit,
    viewModel: CollectionsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MemoraTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
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
                text = "Collections",
                style = MemoraTheme.typography.title,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space2))

        Text(
            text = "Smart collections organized by AI",
            style = MemoraTheme.typography.body,
            color = colors.textSecondary,
            modifier = Modifier.padding(horizontal = MemoraTheme.spacing.space4)
        )

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space6))

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.primary, strokeWidth = 2.dp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = MemoraTheme.spacing.space4),
                horizontalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space3),
                verticalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space3),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(state.collections) { index, collection ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { delay(index * 50L); visible = true }

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(200)) + slideInVertically(
                            initialOffsetY = { it / 4 }, animationSpec = tween(200)
                        )
                    ) {
                        CollectionCard(
                            collection = collection,
                            onClick = { onCollectionClick(collection.type.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CollectionCard(collection: SmartCollection, onClick: () -> Unit) {
    val colors = MemoraTheme.colors
    val icon = mapCollectionIcon(collection.icon)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MemoraTheme.shapes.large)
            .background(colors.surface)
            .border(1.dp, colors.outlineVariant, MemoraTheme.shapes.large)
            .clickable(onClick = onClick)
            .padding(MemoraTheme.spacing.space4)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space3))

        Text(
            text = collection.name,
            style = MemoraTheme.typography.subheading,
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${collection.count} items",
                style = MemoraTheme.typography.caption,
                color = colors.textHint
            )
            Spacer(Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                null,
                tint = colors.textHint,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
