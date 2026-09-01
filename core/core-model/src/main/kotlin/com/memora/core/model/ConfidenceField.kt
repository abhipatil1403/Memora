package com.memora.core.model

data class ConfidenceField<T>(
    val value: T,
    val confidence: Double
) {
    val isLowConfidence: Boolean get() = confidence < 0.70
    val isMediumConfidence: Boolean get() = confidence in 0.70..0.89
    val isHighConfidence: Boolean get() = confidence >= 0.90
}
