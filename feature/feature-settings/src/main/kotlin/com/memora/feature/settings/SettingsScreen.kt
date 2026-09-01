package com.memora.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memora.core.ui.theme.MemoraTheme

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MemoraTheme.colors

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        item {
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
                    "Settings",
                    style = MemoraTheme.typography.title,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Notifications
        item { SectionTitle("Notifications") }
        item {
            SettingsToggleRow(
                title = "Push Notifications",
                subtitle = "Reminders and AI insights",
                isChecked = state.preferences.notificationsEnabled,
                onToggle = { viewModel.toggleNotifications() }
            )
        }

        item { Spacer(Modifier.height(MemoraTheme.spacing.space2)) }

        // Data
        item { SectionTitle("Data & Privacy") }
        item {
            SettingsToggleRow(
                title = "Cloud Backup",
                subtitle = "Sync memories across devices",
                isChecked = state.preferences.backupEnabled,
                onToggle = { viewModel.toggleBackup() }
            )
        }
        item {
            SettingsRow(
                title = "Language",
                subtitle = state.preferences.language.uppercase(),
                onClick = {}
            )
        }

        item { Spacer(Modifier.height(MemoraTheme.spacing.space2)) }

        // About
        item { SectionTitle("About") }
        item {
            SettingsRow(title = "Version", subtitle = state.appVersion, onClick = {})
        }
        item {
            SettingsRow(title = "Privacy Policy", subtitle = "", onClick = {})
        }
        item {
            SettingsRow(title = "Terms of Service", subtitle = "", onClick = {})
        }

        item { Spacer(Modifier.height(MemoraTheme.spacing.space10)) }
    }
}

@Composable
private fun SectionTitle(title: String) {
    val colors = MemoraTheme.colors
    Column(modifier = Modifier.padding(horizontal = MemoraTheme.spacing.space4)) {
        HorizontalDivider(color = colors.outline)
        Spacer(Modifier.height(MemoraTheme.spacing.space4))
        Text(
            title,
            style = MemoraTheme.typography.subheading,
            color = colors.primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(MemoraTheme.spacing.space2))
    }
}

@Composable
private fun SettingsRow(title: String, subtitle: String, onClick: () -> Unit) {
    val colors = MemoraTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MemoraTheme.spacing.space4, vertical = MemoraTheme.spacing.space3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MemoraTheme.typography.body,
                color = colors.textPrimary,
                fontWeight = FontWeight.Medium
            )
            if (subtitle.isNotEmpty()) {
                Text(subtitle, style = MemoraTheme.typography.caption, color = colors.textHint)
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    val colors = MemoraTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MemoraTheme.spacing.space4, vertical = MemoraTheme.spacing.space2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MemoraTheme.typography.body,
                color = colors.textPrimary,
                fontWeight = FontWeight.Medium
            )
            Text(subtitle, style = MemoraTheme.typography.caption, color = colors.textHint)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onPrimary,
                checkedTrackColor = colors.primary,
                uncheckedThumbColor = colors.textHint,
                uncheckedTrackColor = colors.surfaceVariant
            )
        )
    }
}
