package com.memora.feature.memory.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memora.core.common.extension.toFormattedDateString
import com.memora.core.model.EntityType
import com.memora.core.model.ExtractedEntity
import com.memora.core.model.MemoryVersion
import com.memora.core.model.SmartAction
import com.memora.core.ui.components.MemoraConfidenceBadge
import com.memora.core.ui.theme.MemoraTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MemoryDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: MemoryDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MemoraTheme.colors

    if (state.isLoading) {
        Box(
            Modifier
                .fillMaxSize()
                .background(colors.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = colors.primary, strokeWidth = 2.dp)
        }
        return
    }

    val memory = state.memory ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MemoraTheme.spacing.space2, vertical = MemoraTheme.spacing.space2),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = colors.textPrimary)
                }
                Row {
                    IconButton(onClick = { /* edit */ }) {
                        Icon(Icons.Outlined.Edit, "Edit", tint = colors.textSecondary)
                    }
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            if (memory.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            "Favorite",
                            tint = if (memory.isFavorite) colors.error else colors.textSecondary
                        )
                    }
                    IconButton(onClick = { /* share */ }) {
                        Icon(Icons.Outlined.Share, "Share", tint = colors.textSecondary)
                    }
                }
            }
        }

        // Image placeholder
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = MemoraTheme.spacing.space4)
                    .clip(MemoraTheme.shapes.large)
                    .background(colors.primaryContainer.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Image,
                    contentDescription = "Image preview",
                    tint = colors.primary.copy(alpha = 0.3f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Header
        item {
            Column(modifier = Modifier.padding(MemoraTheme.spacing.space4)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space2)
                ) {
                    Text(
                        text = memory.category.displayName,
                        style = MemoraTheme.typography.caption,
                        color = colors.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .background(colors.primaryContainer, MemoraTheme.shapes.small)
                            .padding(horizontal = MemoraTheme.spacing.space2, vertical = 3.dp)
                    )
                    MemoraConfidenceBadge(confidence = memory.overallConfidence)
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "Viewed ${memory.viewCount} times",
                        style = MemoraTheme.typography.caption,
                        color = colors.textHint
                    )
                }

                Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))

                Text(
                    text = memory.title,
                    style = MemoraTheme.typography.title,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(MemoraTheme.spacing.space2))

                Text(
                    text = memory.summary,
                    style = MemoraTheme.typography.body,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(MemoraTheme.spacing.space5))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space2),
                    verticalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space2)
                ) {
                    memory.tags.forEach { tag ->
                        Text(
                            text = "#${tag.value}",
                            style = MemoraTheme.typography.caption,
                            color = colors.secondary,
                            modifier = Modifier
                                .background(colors.secondary.copy(alpha = 0.08f), MemoraTheme.shapes.small)
                                .padding(horizontal = MemoraTheme.spacing.space2, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Extracted Entities
        item {
            SectionHeader("Extracted Information")
        }

        items(memory.entities) { entity ->
            DetailEntityRow(entity = entity)
        }

        // Smart Actions
        if (memory.actions.isNotEmpty()) {
            item { SectionHeader("Smart Actions") }
            items(memory.actions) { action ->
                DetailActionRow(action = action, onClick = { /* handle */ })
            }
        }

        // Version History Toggle
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleVersionHistory() }
                    .padding(horizontal = MemoraTheme.spacing.space4, vertical = MemoraTheme.spacing.space4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.History,
                    "History",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(MemoraTheme.spacing.space2))
                Text(
                    text = "Version History (${state.versions.size})",
                    style = MemoraTheme.typography.subheading,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (state.showVersionHistory) "Hide" else "Show",
                    style = MemoraTheme.typography.bodySmall,
                    color = colors.primary
                )
            }
        }

        // Version History Content
        item {
            AnimatedVisibility(
                visible = state.showVersionHistory,
                enter = expandVertically(tween(250)) + fadeIn(),
                exit = shrinkVertically(tween(250)) + fadeOut()
            ) {
                Column(modifier = Modifier.padding(horizontal = MemoraTheme.spacing.space4)) {
                    state.versions.forEach { version ->
                        VersionRow(version = version)
                        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space3))
                    }
                }
            }
        }

        // Metadata
        item {
            Column(modifier = Modifier.padding(MemoraTheme.spacing.space4)) {
                HorizontalDivider(color = colors.outline)
                Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))
                Text(
                    text = "Captured ${memory.capturedAt.toFormattedDateString()}",
                    style = MemoraTheme.typography.caption,
                    color = colors.textHint
                )
                Spacer(modifier = Modifier.height(MemoraTheme.spacing.space1))
                Text(
                    text = "Language: ${memory.detectedLanguage.uppercase()}",
                    style = MemoraTheme.typography.caption,
                    color = colors.textHint
                )
                if (memory.isSynced) {
                    Spacer(modifier = Modifier.height(MemoraTheme.spacing.space1))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Check,
                            contentDescription = null,
                            tint = colors.success,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(MemoraTheme.spacing.space1))
                        Text(
                            text = "Synced to cloud",
                            style = MemoraTheme.typography.caption,
                            color = colors.success
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(MemoraTheme.spacing.space10)) }
    }
}

@Composable
private fun SectionHeader(title: String) {
    val colors = MemoraTheme.colors
    Column(modifier = Modifier.padding(horizontal = MemoraTheme.spacing.space4)) {
        HorizontalDivider(color = colors.outline)
        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))
        Text(
            text = title,
            style = MemoraTheme.typography.heading,
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space3))
    }
}

@Composable
private fun DetailEntityRow(entity: ExtractedEntity) {
    val colors = MemoraTheme.colors
    val icon = when (entity.type) {
        EntityType.DATE -> Icons.Outlined.CalendarToday
        EntityType.CONTACT -> Icons.Outlined.Person
        EntityType.ADDRESS -> Icons.Outlined.LocationOn
        EntityType.URL -> Icons.Outlined.Link
        EntityType.AMOUNT -> Icons.Outlined.Payments
        EntityType.QR_CODE -> Icons.Outlined.QrCode2
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MemoraTheme.spacing.space4, vertical = MemoraTheme.spacing.space1)
            .clip(MemoraTheme.shapes.medium)
            .background(colors.surfaceVariant)
            .padding(MemoraTheme.spacing.space3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = colors.primary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(MemoraTheme.spacing.space3))
        Column(modifier = Modifier.weight(1f)) {
            entity.label?.let {
                Text(it, style = MemoraTheme.typography.caption, color = colors.textHint)
            }
            Text(
                entity.value,
                style = MemoraTheme.typography.body,
                color = colors.textPrimary,
                fontWeight = FontWeight.Medium
            )
        }
        MemoraConfidenceBadge(confidence = entity.confidence)
    }
}

@Composable
private fun DetailActionRow(action: SmartAction, onClick: () -> Unit) {
    val colors = MemoraTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MemoraTheme.spacing.space4, vertical = MemoraTheme.spacing.space1)
            .clip(MemoraTheme.shapes.medium)
            .background(colors.primary.copy(alpha = 0.06f))
            .clickable(onClick = onClick)
            .padding(MemoraTheme.spacing.space3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Bolt,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(MemoraTheme.spacing.space3))
        Text(
            action.label,
            style = MemoraTheme.typography.body,
            color = colors.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun VersionRow(version: MemoryVersion) {
    val colors = MemoraTheme.colors
    val isAi = version.editedBy == "ai"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MemoraTheme.shapes.medium)
            .background(colors.surfaceVariant)
            .padding(MemoraTheme.spacing.space3),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (isAi) colors.accent.copy(alpha = 0.1f) else colors.primary.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isAi) Icons.Outlined.SmartToy else Icons.Outlined.Edit,
                contentDescription = null,
                tint = if (isAi) colors.accent else colors.primary,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(MemoraTheme.spacing.space3))
        Column {
            Text(
                text = if (isAi) "AI Extraction" else "User Edit",
                style = MemoraTheme.typography.bodySmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            version.changeDescription?.let {
                Text(it, style = MemoraTheme.typography.caption, color = colors.textSecondary)
            }
            Text(
                text = version.editedAt.toFormattedDateString(),
                style = MemoraTheme.typography.caption,
                color = colors.textHint
            )
        }
    }
}
