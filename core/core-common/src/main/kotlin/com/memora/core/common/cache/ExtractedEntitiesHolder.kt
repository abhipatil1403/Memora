package com.memora.core.common.cache

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtractedEntitiesHolder @Inject constructor() {
    private var lastExtractedEntities: Map<String, List<String>> = emptyMap()
    private var lastImageUri: String? = null

    fun setExtractedEntities(imageUri: String?, entities: Map<String, List<String>>) {
        this.lastImageUri = imageUri
        this.lastExtractedEntities = entities
    }

    fun getExtractedEntities(): Map<String, List<String>> = lastExtractedEntities
    fun getLastImageUri(): String? = lastImageUri

    fun clear() {
        lastExtractedEntities = emptyMap()
        lastImageUri = null
    }
}
