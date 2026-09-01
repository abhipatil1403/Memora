package com.memora.core.model

data class SearchResult(
    val memory: Memory,
    val relevanceScore: Double,
    val matchedField: String?
)
