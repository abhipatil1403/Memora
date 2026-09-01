package com.memora.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.memora.core.ui.theme.MemoraTheme

@Composable
fun MemoraButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = MemoraTheme.spacing.space6)
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = MemoraTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MemoraTheme.colors.primary,
            contentColor = MemoraTheme.colors.onPrimary,
            disabledContainerColor = MemoraTheme.colors.outline,
            disabledContentColor = MemoraTheme.colors.textDisabled
        ),
        contentPadding = contentPadding,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(MemoraTheme.spacing.space3))
            }
            Text(
                text = text,
                style = MemoraTheme.typography.button
            )
        }
    }
}

@Composable
fun MemoraOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = MemoraTheme.spacing.space6)
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = MemoraTheme.shapes.medium,
        border = BorderStroke(1.dp, if (enabled) MemoraTheme.colors.outline else MemoraTheme.colors.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MemoraTheme.colors.textPrimary,
            disabledContentColor = MemoraTheme.colors.textDisabled
        ),
        contentPadding = contentPadding
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(MemoraTheme.spacing.space3))
            }
            Text(
                text = text,
                style = MemoraTheme.typography.button
            )
        }
    }
}

@Composable
fun MemoraTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = MemoraTheme.spacing.space4)
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = MemoraTheme.shapes.medium,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MemoraTheme.colors.primary,
            disabledContentColor = MemoraTheme.colors.textDisabled
        ),
        contentPadding = contentPadding
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(MemoraTheme.spacing.space2))
            }
            Text(
                text = text,
                style = MemoraTheme.typography.button
            )
        }
    }
}
