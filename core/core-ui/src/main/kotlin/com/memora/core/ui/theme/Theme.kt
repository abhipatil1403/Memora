package com.memora.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalMemoraColors = staticCompositionLocalOf { MemoraLightColors }
val LocalMemoraTypography = staticCompositionLocalOf { MemoraTypography }
val LocalMemoraShapes = staticCompositionLocalOf { MemoraShapes }
val LocalMemoraSpacing = staticCompositionLocalOf { MemoraSpacing }
val LocalMemoraElevation = staticCompositionLocalOf { MemoraElevation }
val LocalMemoraAnimation = staticCompositionLocalOf { MemoraAnimationSpecs }

@Composable
fun MemoraTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalMemoraColors provides MemoraLightColors,
        LocalMemoraTypography provides MemoraTypography,
        LocalMemoraShapes provides MemoraShapes,
        LocalMemoraSpacing provides MemoraSpacing,
        LocalMemoraElevation provides MemoraElevation,
        LocalMemoraAnimation provides MemoraAnimationSpecs,
    ) {
        MaterialTheme(
            content = content
        )
    }
}

object MemoraTheme {
    val colors: MemoraColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMemoraColors.current

    val typography: MemoraTypographyStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalMemoraTypography.current

    val shapes: MemoraShapeStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalMemoraShapes.current

    val spacing: MemoraSpacingStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalMemoraSpacing.current

    val elevation: MemoraElevationStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalMemoraElevation.current

    val animation: MemoraAnimationStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalMemoraAnimation.current
}
