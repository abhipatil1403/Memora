package com.memora.core.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.spring

val MemoraAnimationSpecs = MemoraAnimationStyles(
    durationInstant = 100,
    durationFast = 150,
    durationNormal = 250,
    durationSlow = 350,
    easeOut = CubicBezierEasing(0.16f, 1f, 0.3f, 1f),
    easeInOut = CubicBezierEasing(0.65f, 0f, 0.35f, 1f),
    springSpec = spring(dampingRatio = 0.8f, stiffness = 500f)
)

class MemoraAnimationStyles(
    val durationInstant: Int,
    val durationFast: Int,
    val durationNormal: Int,
    val durationSlow: Int,
    val easeOut: Easing,
    val easeInOut: Easing,
    val springSpec: androidx.compose.animation.core.SpringSpec<Float>
)
