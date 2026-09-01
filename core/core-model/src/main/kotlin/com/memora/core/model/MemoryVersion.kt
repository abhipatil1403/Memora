package com.memora.core.model

import java.time.Instant

data class VersionSnapshot(
    val title: String,
    val summary: String,
    val category: String,
    val tags: List<String>,
    val entities: List<ExtractedEntity>
)

data class MemoryVersion(
    val id: String,
    val editedAt: Instant,
    val editedBy: String,
    val fields: VersionSnapshot,
    val changeDescription: String?
)
