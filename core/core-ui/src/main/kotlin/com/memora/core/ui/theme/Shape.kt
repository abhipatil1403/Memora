package com.memora.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

val MemoraShapes = MemoraShapeStyles(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    xLarge = RoundedCornerShape(20.dp),
    full = RoundedCornerShape(50)
)

class MemoraShapeStyles(
    val small: RoundedCornerShape,
    val medium: RoundedCornerShape,
    val large: RoundedCornerShape,
    val xLarge: RoundedCornerShape,
    val full: RoundedCornerShape
)
