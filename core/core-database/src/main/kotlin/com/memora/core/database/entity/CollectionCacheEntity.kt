package com.memora.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collection_cache")
data class CollectionCacheEntity(
    @PrimaryKey val type: String,     // CollectionType name
    val name: String,
    val count: Int,
    val lastUpdated: Long
)
