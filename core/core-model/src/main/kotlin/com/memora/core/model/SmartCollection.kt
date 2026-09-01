package com.memora.core.model

enum class CollectionType(val displayName: String) {
    TODAY("Today"),
    THIS_WEEK("This Week"),
    COLLEGE("College"),
    DOCUMENTS("Documents"),
    RECEIPTS("Receipts"),
    BUSINESS("Business"),
    MEDICAL("Medical"),
    IMPORTANT("Important"),
    FAVORITES("Favorites"),
    ARCHIVED("Archived")
}

data class SmartCollection(
    val type: CollectionType,
    val name: String,
    val count: Int,
    val icon: String
)
