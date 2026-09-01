package com.memora.feature.processing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memora.core.ui.components.MemoraButton
import com.memora.core.ui.theme.MemoraTheme

@Composable
fun ProcessingScreen(
    onNavigateToResult: (String) -> Unit,
    viewModel: ProcessingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MemoraTheme.colors

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = MemoraTheme.spacing.space6),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (state.error != null) {
            // Error State
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(colors.error.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = colors.error,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(MemoraTheme.spacing.space6))

            Text(
                text = "Analysis Failed",
                style = MemoraTheme.typography.title,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(MemoraTheme.spacing.space2))

            Text(
                text = state.error ?: "Unable to analyze this image. Please try again.",
                style = MemoraTheme.typography.body,
                color = colors.error,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(MemoraTheme.spacing.space8))

            MemoraButton(
                onClick = {
                    state.resultMemoryId?.let { onNavigateToResult(it) } ?: onNavigateToResult("fallback")
                },
                text = "Continue to View",
                modifier = Modifier.fillMaxWidth()
            )

        } else {
            // Processing / Success State
            if (!state.isComplete) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(colors.primary.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else {
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(tween(250)) + fadeIn()
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(colors.success.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Check,
                            contentDescription = null,
                            tint = colors.success,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(MemoraTheme.spacing.space8))

            Text(
                text = if (state.isComplete) "Analysis Complete" else "Analyzing image...",
                style = MemoraTheme.typography.title,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(MemoraTheme.spacing.space2))

            Text(
                text = if (state.isComplete) "Memora has extracted all the information"
                else "Memora AI is extracting entities from your image",
                style = MemoraTheme.typography.body,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(MemoraTheme.spacing.space10))

            if (!state.isComplete) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = colors.primary,
                    trackColor = colors.surfaceVariant,
                )
                Spacer(modifier = Modifier.height(MemoraTheme.spacing.space8))
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(MemoraTheme.spacing.space3)
            ) {
                state.steps.forEach { step ->
                    ProcessingStepRow(step = step)
                }
            }

            if (state.isComplete) {
                Spacer(modifier = Modifier.height(MemoraTheme.spacing.space10))
                MemoraButton(
                    onClick = {
                        state.resultMemoryId?.let { onNavigateToResult(it) }
                    },
                    text = "View Result",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ProcessingStepRow(step: ProcessingStep) {
    val colors = MemoraTheme.colors

    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    when {
                        step.isComplete -> colors.success.copy(alpha = 0.1f)
                        step.isActive -> colors.primary.copy(alpha = 0.1f)
                        else -> colors.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                step.isComplete -> Icon(
                    Icons.Outlined.Check,
                    contentDescription = null,
                    tint = colors.success,
                    modifier = Modifier.size(14.dp)
                )
                step.isActive -> Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .alpha(dotAlpha)
                        .background(colors.primary)
                )
                else -> {}
            }
        }

        Spacer(modifier = Modifier.width(MemoraTheme.spacing.space3))

        Text(
            text = step.label,
            style = MemoraTheme.typography.body,
            color = when {
                step.isComplete -> colors.success
                step.isActive -> colors.textPrimary
                else -> colors.textDisabled
            },
            fontWeight = if (step.isActive) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
