package com.memora.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memora.core.ui.R
import com.memora.core.ui.theme.MemoraTheme

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, animationSpec = tween(800, easing = EaseOutCubic))
    }

    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.Auth -> onNavigateToOnboarding()
            SplashDestination.Home -> onNavigateToHome()
            SplashDestination.Loading -> { /* still loading */ }
        }
    }

    val colors = MemoraTheme.colors
    val gradient = Brush.verticalGradient(
        colors = listOf(colors.primary, colors.accent)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(contentAlpha.value)
        ) {
            // Safe logo loading fallback
            val painterResult = runCatching { painterResource(id = R.drawable.logo1) }.getOrNull()
            if (painterResult != null) {
                Image(
                    painter = painterResult,
                    contentDescription = "Memora Logo",
                    modifier = Modifier.size(96.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Memora Logo",
                    tint = colors.onPrimary,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))

            Text(
                text = "Memora",
                color = colors.onPrimary,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(MemoraTheme.spacing.space2))

            Text(
                text = "Capture Once. Know Forever.",
                color = colors.onPrimary.copy(alpha = 0.7f),
                style = MemoraTheme.typography.body,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
