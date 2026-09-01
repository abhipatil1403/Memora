package com.memora.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.memora.core.ui.theme.MemoraTheme

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToCollections: () -> Unit,
    onSignOut: () -> Unit
) {
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
                    "Profile",
                    style = MemoraTheme.typography.title,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Avatar + Info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MemoraTheme.spacing.space6),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(colors.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "A",
                        style = MemoraTheme.typography.display,
                        color = colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(MemoraTheme.spacing.space4))
                Text(
                    "Abhi Patil",
                    style = MemoraTheme.typography.title,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(MemoraTheme.spacing.space1))
                Text(
                    "abhi@memora.app",
                    style = MemoraTheme.typography.body,
                    color = colors.textSecondary
                )
            }
        }

        // Stats row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MemoraTheme.spacing.space4)
                    .clip(MemoraTheme.shapes.large)
                    .background(colors.surfaceVariant)
                    .padding(MemoraTheme.spacing.space5),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("47", "Memories")
                StatItem("12", "Collections")
                StatItem("3", "Reminders")
            }
        }

        item { Spacer(Modifier.height(MemoraTheme.spacing.space6)) }

        // Menu items
        item {
            ProfileMenuItem(
                icon = Icons.Outlined.Bookmark,
                label = "Collections",
                onClick = onNavigateToCollections
            )
        }
        item {
            ProfileMenuItem(
                icon = Icons.Outlined.Notifications,
                label = "Reminders",
                onClick = onNavigateToReminders
            )
        }
        item {
            ProfileMenuItem(
                icon = Icons.Outlined.History,
                label = "Activity",
                onClick = {}
            )
        }
        item {
            ProfileMenuItem(
                icon = Icons.Outlined.Settings,
                label = "Settings",
                onClick = onNavigateToSettings
            )
        }

        item {
            HorizontalDivider(
                modifier = Modifier.padding(
                    horizontal = MemoraTheme.spacing.space4,
                    vertical = MemoraTheme.spacing.space4
                ),
                color = colors.outline
            )
        }

        item {
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Outlined.Logout,
                label = "Sign Out",
                onClick = onSignOut,
                tint = colors.error
            )
        }

        item { Spacer(Modifier.height(MemoraTheme.spacing.space10)) }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    val colors = MemoraTheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MemoraTheme.typography.title,
            color = colors.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(MemoraTheme.spacing.space1))
        Text(
            label,
            style = MemoraTheme.typography.caption,
            color = colors.textSecondary
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: androidx.compose.ui.graphics.Color = MemoraTheme.colors.textPrimary
) {
    val colors = MemoraTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = MemoraTheme.spacing.space4, vertical = MemoraTheme.spacing.space4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(MemoraTheme.spacing.space4))
        Text(
            label,
            style = MemoraTheme.typography.body,
            color = tint,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            null,
            tint = colors.textHint,
            modifier = Modifier.size(20.dp)
        )
    }
}
