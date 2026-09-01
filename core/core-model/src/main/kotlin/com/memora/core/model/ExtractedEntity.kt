package com.memora.core.model

enum class EntityType {
    DATE, CONTACT, ADDRESS, URL, AMOUNT, QR_CODE
}

data class ExtractedEntity(
    val type: EntityType,
    val value: String,
    val label: String?,
    val confidence: Double
) {
    val isLowConfidence: Boolean get() = confidence < 0.70
    val isMediumConfidence: Boolean get() = confidence in 0.70..0.89
    val isHighConfidence: Boolean get() = confidence >= 0.90
}
