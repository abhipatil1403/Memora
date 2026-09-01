package com.memora.core.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonElement

class MemoraTypeConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return if (value == null) "[]" else json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            json.decodeFromString<List<String>>(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Since Room only stores primitives, complex objects like List<ExtractedEntity>
    // are stored as JSON strings. I'll just use basic String <-> String since the 
    // Domain Mappers will handle the actual serialization logic for these custom types
    // to keep the Room DAOs generic.
}
