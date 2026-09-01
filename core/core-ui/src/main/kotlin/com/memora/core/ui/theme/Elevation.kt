package com.memora.core.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val MemoraElevation = MemoraElevationStyles(
    level0 = 0.dp,
    level1 = 0.5.dp,
    level2 = 1.dp,
    level3 = 2.dp,
    level4 = 4.dp,
    level5 = 8.dp
)

class MemoraElevationStyles(
    val level0: Dp,
    val level1: Dp,
    val level2: Dp,
    val level3: Dp,
    val level4: Dp,
    val level5: Dp
)
