package com.memora.feature.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.memora.core.ui.components.MemoraButton
import com.memora.core.ui.theme.MemoraTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
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
    val extractedEntities = state.extractedEntities
    val activeCategories = extractedEntities.filter { it.value.isNotEmpty() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MemoraTheme.spacing.space2,
                        vertical = MemoraTheme.spacing.space2
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = colors.textPrimary)
                }
                Row {
                    IconButton(onClick = { /* toggle favorite */ }) {
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

        // Image Preview (if imageUri exists)
        if (!state.imageUri.isNull_or_blank()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = MemoraTheme.spacing.space4)
                        .clip(MemoraTheme.shapes.large)
                        .background(colors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = state.imageUri,
                        contentDescription = "Image Preview",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))
            }
        }

        // Header Section (Title & Category)
        item {
            Column(modifier = Modifier.padding(horizontal = MemoraTheme.spacing.space4)) {
                Text(
                    text = memory.category.displayName,
                    style = MemoraTheme.typography.caption,
                    color = colors.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .background(colors.primaryContainer, MemoraTheme.shapes.small)
                        .padding(horizontal = MemoraTheme.spacing.space2, vertical = 3.dp)
                )

                Spacer(modifier = Modifier.height(MemoraTheme.spacing.space2))

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

                if (memory.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))
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
        }

        item {
            HorizontalDivider(
                modifier = Modifier.padding(
                    horizontal = MemoraTheme.spacing.space4,
                    vertical = MemoraTheme.spacing.space6
                ),
                color = colors.outline
            )
        }

        // Extracted Information Section
        item {
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { delay(150); visible = true }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(250)) + slideInVertically(
                    initialOffsetY = { it / 5 },
                    animationSpec = tween(250)
                )
            ) {
                Column(modifier = Modifier.padding(horizontal = MemoraTheme.spacing.space4)) {
                    Text(
                        text = "Extracted Information",
                        style = MemoraTheme.typography.heading,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))

                    if (activeCategories.isEmpty()) {
                        // Empty State Graceful Handling
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MemoraTheme.shapes.medium)
                                .background(colors.surfaceVariant)
                                .padding(MemoraTheme.spacing.space6),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = colors.textHint,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(MemoraTheme.spacing.space2))
                                Text(
                                    text = "No entities detected.",
                                    style = MemoraTheme.typography.body,
                                    color = colors.textSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        // Render Active Entity Categories
                        activeCategories.forEach { (categoryKey, values) ->
                            EntityCategoryBlock(
                                categoryKey = categoryKey,
                                values = values
                            )
                            Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))
                        }
                    }
                }
            }
        }

        // Done button
        item {
            Spacer(modifier = Modifier.height(MemoraTheme.spacing.space6))
            MemoraButton(
                onClick = onNavigateToHome,
                text = "Done",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MemoraTheme.spacing.space4,
                        vertical = MemoraTheme.spacing.space4
                    )
            )
        }

        item { Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4)) }
    }
}

@Composable
private fun EntityCategoryBlock(
    categoryKey: String,
    values: List<String>
) {
    val colors = MemoraTheme.colors
    val displayName = getCategoryDisplayName(categoryKey)
    val icon = getCategoryIcon(categoryKey)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MemoraTheme.shapes.medium)
            .background(colors.surfaceVariant)
            .padding(MemoraTheme.spacing.space4)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(MemoraTheme.spacing.space3))

            Text(
                text = displayName,
                style = MemoraTheme.typography.body,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space3))

        values.forEach { itemValue ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.background)
                    .padding(horizontal = MemoraTheme.spacing.space3, vertical = MemoraTheme.spacing.space2)
            ) {
                Text(
                    text = itemValue,
                    style = MemoraTheme.typography.bodySmall,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.isBlank()

private fun getCategoryDisplayName(key: String): String {
    return when (key.uppercase()) {
        "MERCHANT" -> "Merchant"
        "PRODUCT" -> "Products"
        "DATE" -> "Dates"
        "PHONE" -> "Phone"
        "EMAIL" -> "Email"
        "LOCATION" -> "Location"
        "MONEY" -> "Money"
        "ORGANIZATION" -> "Organization"
        "PERSON" -> "Person"
        "EVENT" -> "Event"
        "MEDICINE" -> "Medicine"
        "TIME" -> "Time"
        "URL" -> "URL"
        else -> key.lowercase().replaceFirstChar { it.uppercase() }
    }
}

private fun getCategoryIcon(key: String): ImageVector {
    return when (key.uppercase()) {
        "MERCHANT" -> Icons.Outlined.Storefront
        "PRODUCT" -> Icons.Outlined.ShoppingBag
        "DATE" -> Icons.Outlined.CalendarToday
        "PHONE" -> Icons.Outlined.Phone
        "EMAIL" -> Icons.Outlined.Email
        "LOCATION" -> Icons.Outlined.LocationOn
        "MONEY" -> Icons.Outlined.Payments
        "ORGANIZATION" -> Icons.Outlined.Category
        "PERSON" -> Icons.Outlined.Person
        "EVENT" -> Icons.Outlined.AutoAwesome
        "MEDICINE" -> Icons.Outlined.MedicalServices
        "TIME" -> Icons.Outlined.CalendarToday
        "URL" -> Icons.Outlined.Link
        else -> Icons.Outlined.Info
    }
}
