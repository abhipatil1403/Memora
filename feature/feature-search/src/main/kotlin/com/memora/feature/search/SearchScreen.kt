package com.memora.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memora.core.model.SearchResult
import com.memora.core.ui.components.MemoraConfidenceBadge
import com.memora.core.ui.theme.MemoraTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onMemoryClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MemoraTheme.colors
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MemoraTheme.spacing.space2, vertical = MemoraTheme.spacing.space2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = colors.textPrimary)
            }
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.updateQuery(it) },
                placeholder = {
                    Text(
                        "Search memories...",
                        color = colors.textHint,
                        style = MemoraTheme.typography.body
                    )
                },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, null, tint = colors.textHint, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(Icons.Outlined.Close, "Clear", tint = colors.textSecondary)
                        }
                    }
                },
                singleLine = true,
                textStyle = MemoraTheme.typography.body.copy(color = colors.textPrimary),
                shape = MemoraTheme.shapes.full,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline,
                    cursorColor = colors.primary
                ),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
            )
        }

        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space2)
        ) {
            // Recent searches (when no query)
            if (state.query.isEmpty() && !state.hasSearched) {
                item {
                    Text(
                        text = "Recent Searches",
                        style = MemoraTheme.typography.subheading,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            horizontal = MemoraTheme.spacing.space4,
                            vertical = MemoraTheme.spacing.space2
                        )
                    )
                }
                items(state.recentSearches) { recent ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.searchFromRecent(recent) }
                            .padding(horizontal = MemoraTheme.spacing.space4, vertical = MemoraTheme.spacing.space3),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.History,
                            null,
                            tint = colors.textHint,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(MemoraTheme.spacing.space3))
                        Text(recent, style = MemoraTheme.typography.body, color = colors.textPrimary)
                    }
                }
            }

            // Loading
            if (state.isSearching) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(MemoraTheme.spacing.space8),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = colors.primary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Results
            if (state.hasSearched && !state.isSearching) {
                item {
                    Text(
                        text = "${state.results.size} result${if (state.results.size != 1) "s" else ""}",
                        style = MemoraTheme.typography.bodySmall,
                        color = colors.textHint,
                        modifier = Modifier.padding(
                            horizontal = MemoraTheme.spacing.space4,
                            vertical = MemoraTheme.spacing.space2
                        )
                    )
                }
                items(state.results) { result ->
                    SearchResultRow(result = result, onClick = { onMemoryClick(result.memory.id) })
                }

                if (state.results.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MemoraTheme.spacing.space10),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.SearchOff,
                                contentDescription = null,
                                tint = colors.textDisabled,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(MemoraTheme.spacing.space4))
                            Text(
                                "No memories found",
                                style = MemoraTheme.typography.subheading,
                                color = colors.textSecondary
                            )
                            Spacer(Modifier.height(MemoraTheme.spacing.space1))
                            Text(
                                "Try a different search term",
                                style = MemoraTheme.typography.bodySmall,
                                color = colors.textHint
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(result: SearchResult, onClick: () -> Unit) {
    val colors = MemoraTheme.colors
    val memory = result.memory

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = MemoraTheme.spacing.space4, vertical = MemoraTheme.spacing.space2)
            .clip(MemoraTheme.shapes.medium)
            .background(colors.surface)
            .padding(MemoraTheme.spacing.space3),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space2)
            ) {
                Text(
                    text = memory.category.displayName,
                    style = MemoraTheme.typography.caption,
                    color = colors.primary,
                    fontWeight = FontWeight.SemiBold
                )
                MemoraConfidenceBadge(confidence = memory.overallConfidence)
            }
            Spacer(Modifier.height(MemoraTheme.spacing.space1))
            Text(
                text = memory.title,
                style = MemoraTheme.typography.subheading,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = memory.summary,
                style = MemoraTheme.typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
