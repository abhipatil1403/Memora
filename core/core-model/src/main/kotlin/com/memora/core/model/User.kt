package com.memora.core.model

import java.time.Instant

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
