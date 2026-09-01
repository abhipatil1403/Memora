package com.memora.core.ui.theme

import androidx.compose.ui.graphics.Color

// ── Primary Palette ──────────────────────────────────────────────
val Indigo50 = Color(0xFFEEF2FF)
val Indigo100 = Color(0xFFE0E7FF)
val Indigo600 = Color(0xFF4F46E5)
val Indigo700 = Color(0xFF4338CA)

// ── Accent ───────────────────────────────────────────────────────
val Cyan500 = Color(0xFF06B6D4)
val Violet500 = Color(0xFF8B5CF6)

// ── Neutral — Gray scale (Tailwind Gray) ─────────────────────────
val Gray50 = Color(0xFFF9FAFB)
val Gray100 = Color(0xFFF3F4F6)
val Gray200 = Color(0xFFE5E7EB)
val Gray300 = Color(0xFFD1D5DB)
val Gray400 = Color(0xFF9CA3AF)
val Gray500 = Color(0xFF6B7280)
val Gray600 = Color(0xFF4B5563)
val Gray700 = Color(0xFF374151)
val Gray800 = Color(0xFF1F2937)
val Gray900 = Color(0xFF111827)

// ── Semantic ─────────────────────────────────────────────────────
val Green = Color(0xFF10B981)
val Amber = Color(0xFFF59E0B)
val Red = Color(0xFFEF4444)

// ── Surface tint ─────────────────────────────────────────────────
val Slate50 = Color(0xFFF8FAFC)

// ── Light Color Scheme ───────────────────────────────────────────
val MemoraLightColors = MemoraColors(
    primary = Indigo600,
    onPrimary = Color.White,
    primaryContainer = Indigo50,
    secondary = Cyan500,
    accent = Violet500,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Slate50,
    onBackground = Gray900,
    onSurface = Gray900,
    onSurfaceVariant = Gray600,
    outline = Gray200,
    outlineVariant = Gray100,
    textPrimary = Gray900,
    textSecondary = Gray600,
    textHint = Gray500,
    textDisabled = Gray400,
    success = Green,
    warning = Amber,
    error = Red,
    confidenceHigh = Green,
    confidenceMedium = Amber,
    confidenceLow = Red
)

// Dark colors removed — always use light theme
val MemoraDarkColors = MemoraLightColors

class MemoraColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textHint: Color,
    val textDisabled: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val confidenceHigh: Color,
    val confidenceMedium: Color,
    val confidenceLow: Color
)
