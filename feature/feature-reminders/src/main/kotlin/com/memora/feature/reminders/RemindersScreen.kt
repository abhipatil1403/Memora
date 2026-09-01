package com.memora.feature.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memora.core.common.extension.toFormattedDateString
import com.memora.core.model.Reminder
import com.memora.core.ui.theme.MemoraTheme

@Composable
fun RemindersScreen(
    onNavigateBack: () -> Unit,
    viewModel: RemindersViewModel = hiltViewModel()
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
                "Reminders",
                style = MemoraTheme.typography.title,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.primary, strokeWidth = 2.dp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space2)
            ) {
                if (state.upcoming.isNotEmpty()) {
                    item {
                        Text(
                            "Upcoming",
                            style = MemoraTheme.typography.subheading,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(
                                horizontal = MemoraTheme.spacing.space4,
                                vertical = MemoraTheme.spacing.space2
                            )
                        )
                    }
                    items(state.upcoming) { reminder ->
                        ReminderRow(
                            reminder = reminder,
                            onComplete = { viewModel.completeReminder(reminder.id) }
                        )
                    }
                }

                if (state.past.isNotEmpty()) {
                    item { Spacer(Modifier.height(MemoraTheme.spacing.space4)) }
                    item {
                        Text(
                            "Completed",
                            style = MemoraTheme.typography.subheading,
                            color = colors.textSecondary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(
                                horizontal = MemoraTheme.spacing.space4,
                                vertical = MemoraTheme.spacing.space2
                            )
                        )
                    }
                    items(state.past) { reminder ->
                        ReminderRow(reminder = reminder, onComplete = null)
                    }
                }

                if (state.upcoming.isEmpty() && state.past.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MemoraTheme.spacing.space16),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.NotificationsNone,
                                contentDescription = null,
                                tint = colors.textDisabled,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(MemoraTheme.spacing.space4))
                            Text(
                                "No reminders yet",
                                style = MemoraTheme.typography.subheading,
                                color = colors.textSecondary
                            )
                            Spacer(Modifier.height(MemoraTheme.spacing.space1))
                            Text(
                                "AI will suggest reminders from your captures",
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
private fun ReminderRow(reminder: Reminder, onComplete: (() -> Unit)?) {
    val colors = MemoraTheme.colors
    val isCompleted = reminder.isCompleted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MemoraTheme.spacing.space4)
            .clip(MemoraTheme.shapes.medium)
            .background(colors.surface)
            .padding(MemoraTheme.spacing.space3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isCompleted) colors.success.copy(alpha = 0.1f)
                    else if (reminder.source == "ai_suggested") colors.accent.copy(alpha = 0.1f)
                    else colors.primaryContainer
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Outlined.Check, null, tint = colors.success, modifier = Modifier.size(18.dp))
            } else {
                Icon(Icons.Outlined.Notifications, null, tint = colors.accent, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.width(MemoraTheme.spacing.space3))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reminder.title,
                style = MemoraTheme.typography.body,
                color = if (isCompleted) colors.textHint else colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
            reminder.description?.let {
                Text(it, style = MemoraTheme.typography.caption, color = colors.textSecondary)
            }
            Text(
                text = reminder.dateTime.toFormattedDateString(),
                style = MemoraTheme.typography.caption,
                color = colors.textHint
            )
            if (reminder.source == "ai_suggested") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(MemoraTheme.spacing.space1))
                    Text(
                        "AI Suggested",
                        style = MemoraTheme.typography.caption,
                        color = colors.accent
                    )
                }
            }
        }
        if (onComplete != null) {
            IconButton(onClick = onComplete) {
                Icon(
                    Icons.Outlined.Check,
                    "Complete",
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
