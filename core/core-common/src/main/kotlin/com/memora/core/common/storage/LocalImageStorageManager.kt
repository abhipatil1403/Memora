package com.memora.core.common.storage

import android.content.Context
import com.memora.core.common.result.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalImageStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val memoriesDir: File
        get() = File(context.filesDir, "memories").apply { if (!exists()) mkdirs() }

    private val profileDir: File
        get() = File(context.filesDir, "profile").apply { if (!exists()) mkdirs() }

    suspend fun saveMemoryImage(memoryId: String, imageBytes: ByteArray): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(memoriesDir, "$memoryId.jpg")
                FileOutputStream(file).use { out ->
                    out.write(imageBytes)
                }
                val localUriPath = file.toURI().toString()
                Result.Success(localUriPath)
            } catch (e: Exception) {
                Timber.e(e, "Error saving image locally to Android storage")
                Result.Error(e)
            }
        }
    }

    suspend fun saveProfileAvatar(imageBytes: ByteArray): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(profileDir, "avatar.jpg")
                FileOutputStream(file).use { out ->
                    out.write(imageBytes)
                }
                val localUriPath = file.toURI().toString()
                Result.Success(localUriPath)
            } catch (e: Exception) {
                Timber.e(e, "Error saving avatar locally to Android storage")
                Result.Error(e)
            }
        }
    }

    suspend fun deleteMemoryImage(memoryId: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(memoriesDir, "$memoryId.jpg")
                val deleted = if (file.exists()) file.delete() else true
                Result.Success(deleted)
            } catch (e: Exception) {
                Timber.e(e, "Error deleting local image")
                Result.Error(e)
            }
        }
    }

    fun getMemoryImageFile(memoryId: String): File? {
        val file = File(memoriesDir, "$memoryId.jpg")
        return if (file.exists()) file else null
    }
}
