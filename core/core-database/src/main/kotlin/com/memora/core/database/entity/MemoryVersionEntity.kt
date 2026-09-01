package com.memora.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "memory_versions",
    foreignKeys = [ForeignKey(
        entity = MemoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["memoryId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("memoryId")]
)
data class MemoryVersionEntity(
    @PrimaryKey val id: String,
    val memoryId: String,
    val editedAt: Long,
    val editedBy: String,             // "ai" | "user"
    val fields: String,               // JSON serialized VersionSnapshot
    val changeDescription: String?
)
