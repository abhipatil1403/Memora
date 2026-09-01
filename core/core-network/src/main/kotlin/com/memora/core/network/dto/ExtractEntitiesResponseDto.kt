package com.memora.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExtractEntitiesResponseDto(
    val success: Boolean = false,
    val filename: String? = null,
    val entities: Map<String, List<String>> = emptyMap()
)
